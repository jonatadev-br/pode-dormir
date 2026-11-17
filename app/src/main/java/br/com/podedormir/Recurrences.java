package br.com.podedormir;
import android.content.Context;
import android.content.Intent;
import org.json.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Mantém uma próxima ocorrência separada; não depende de temporizadores. */
final class Recurrences {
 private Recurrences(){}
 static int period(JSONObject e){String type=e.optString("recorrencia","nenhuma");return type.equals("diaria")?1:type.equals("semanal")?7:0;}
 static String label(JSONObject e){int p=period(e);return p==1?"Todos os dias":p==7?"Toda semana":"Sem repetição";}
 static synchronized boolean advance(Context c,String id,long now) {
  try {
   JSONArray all=Alarms.load(c);JSONObject current=null;
   for(int i=0;i<all.length();i++){JSONObject e=all.optJSONObject(i);if(e!=null&&e.optString("id").equals(id)){current=e;break;}}
   if(current==null||current.optBoolean("teste")||current.optBoolean("proximaGerada")||period(current)==0||current.optBoolean("horarioInvalido"))return true;
   String zone=TimeZone.getDefault().getID();
   String local=current.optString("despertarLocal",LocalTimeRules.localAt(current.getLong("hora"),zone));
   String nextLocal=RecurrenceRule.next(local,period(current),now,zone);
   String series=current.optString("serieId",current.optString("id"));
   String childId=UUID.nameUUIDFromBytes((series+"|"+nextLocal).getBytes(StandardCharsets.UTF_8)).toString();
   for(int i=0;i<all.length();i++){JSONObject e=all.optJSONObject(i);if(e!=null&&e.optString("id").equals(childId)){current.put("proximaGerada",true);return Alarms.save(c,all);}}
   JSONObject child=new JSONObject(current.toString());child.put("id",childId);child.put("serieId",series);child.put("despertarLocal",nextLocal);child.put("fuso",zone);child.put("confirmado",false);child.put("proximaGerada",false);child.remove("confirmadoEm");child.remove("sonecaAte");
   Long wake=LocalTimeRules.instantFor(nextLocal,zone);
   Long departure=null;
   if(current.has("saidaLocal")){
    long delta=ChronoUnit.DAYS.between(LocalDateTime.parse(local).toLocalDate(),LocalDateTime.parse(nextLocal).toLocalDate());
    String departureLocal=LocalDateTime.parse(current.getString("saidaLocal")).plusDays(delta).toString();child.put("saidaLocal",departureLocal);departure=LocalTimeRules.instantFor(departureLocal,zone);
   }
   boolean invalid=wake==null || (child.has("saidaLocal")&&departure==null) || (wake!=null&&departure!=null&&departure<wake);
   child.put("horarioInvalido",invalid);
   child.put("hora",wake==null?LocalDateTime.parse(nextLocal).atZone(ZoneId.of(zone)).toInstant().toEpochMilli():wake.longValue());
   if(departure!=null)child.put("saida",departure.longValue());
   current.put("serieId",series);current.put("proximaGerada",true);current.put("recorrenciaFalhou",false);child.put("recorrenciaFalhou",false);all.put(child);
   // Grava as duas mudanças de uma vez, antes de pedir alarmes para a próxima data.
   if(!Alarms.save(c,all))return false;
   int submitted=Alarms.schedule(c,child);
   History.add(c,invalid?"Próxima ocorrência de "+child.optString("nome")+" tem horário inexistente ou inconsistente. Edite o compromisso.":"Próxima ocorrência de "+child.optString("nome")+": "+submitted+" solicitação(ões) de alarme enviadas ao Android.");
   return true;
  }catch(Exception ex){
   try{JSONObject failed=Alarms.find(c,id);if(failed!=null){failed.put("recorrenciaFalhou",true);Alarms.put(c,failed);}}catch(Exception ignored){}
   History.add(c,"Não foi possível preparar a próxima ocorrência. Confira o compromisso.");return false;
  }
 }
 /** Remove toda a série de forma persistente antes de cancelar as solicitações. */
 static synchronized boolean removeSeries(Context c,String series) {
  JSONArray old=Alarms.load(c),remaining=new JSONArray();List<JSONObject> removed=new ArrayList<>();
  for(int i=0;i<old.length();i++){JSONObject e=old.optJSONObject(i);if(e==null)continue;if(e.optString("serieId",e.optString("id")).equals(series))removed.add(e);else remaining.put(e);}
  if(!Alarms.save(c,remaining))return false;
  for(JSONObject e:removed){Alarms.cancel(c,e);try{c.startService(new Intent(c,AlarmService.class).setAction(AlarmService.CONFIRM).putExtra("id",e.optString("id")));}catch(RuntimeException ignored){}}
  History.add(c,"Série recorrente removida; cancelamento de "+removed.size()+" ocorrência(s) solicitado.");return true;
 }
}
