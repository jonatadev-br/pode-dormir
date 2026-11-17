package br.com.podedormir;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.Set;

/** Persistência local e identificação independente de cada tentativa. */
final class Alarms {
    static final String PREF="eventos";
    /** Evita substituir o armazenamento caso o conteúdo existente esteja ilegível. */
    private static boolean validArray(JSONArray data) {
        try {
            Set<String> ids=new HashSet<>();
            for(int i=0;i<data.length();i++){
                JSONObject e=data.getJSONObject(i);String id=e.optString("id","");
                if(id.trim().isEmpty()||!ids.add(id)||e.optString("nome","").trim().isEmpty()||!e.has("hora")||e.getLong("hora")<=0)return false;
            }
            return true;
        }catch(Exception ex){return false;}
    }
    static boolean storageValid(Context c){
        try{return validArray(new JSONArray(c.getSharedPreferences(PREF,0).getString("lista","[]")));}
        catch(Exception ex){return false;}
    }
    static JSONArray load(Context c) {
        try { JSONArray a=new JSONArray(c.getSharedPreferences(PREF,0).getString("lista","[]"));return validArray(a)?a:new JSONArray(); }
        catch(Exception e) { return new JSONArray(); }
    }
    // Não sobrescreve dados ilegíveis. Gravação síncrona protege confirmações.
    static boolean save(Context c, JSONArray data) {
        if(data==null||!storageValid(c)||!validArray(data))return false;
        boolean saved=c.getSharedPreferences(PREF,0).edit().putString("lista",data.toString()).commit();
        if(saved)UpcomingNotification.refresh(c);
        return saved;
    }
    static boolean allowed(Context c) {
        return Build.VERSION.SDK_INT < 31 || ((AlarmManager)c.getSystemService(Context.ALARM_SERVICE)).canScheduleExactAlarms();
    }
    private static Intent alarmIntent(Context c,String id,int n){
        return new Intent(c,AlarmReceiver.class).setData(Uri.parse("podedormir://alarme/"+id+"/"+n)).putExtra("id",id).putExtra("numero",n);
    }
    static PendingIntent pending(Context c,String id,int n,long when){
        return PendingIntent.getBroadcast(c,0,alarmIntent(c,id,n).putExtra("disparoPrevisto",when),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    private static final String TRACK="tentativas_programadas";
    static java.util.function.LongSupplier monotonicClock=android.os.SystemClock::elapsedRealtime;
    private static void cancelOne(Context c,AlarmManager a,String id,int n){
        PendingIntent pi=PendingIntent.getBroadcast(c,0,alarmIntent(c,id,n),PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE);
        if(pi!=null){a.cancel(pi);pi.cancel();}
    }
    static void cancel(Context c, JSONObject e) {
        AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);String id=e.optString("id");
        android.content.SharedPreferences track=c.getSharedPreferences(TRACK,0);
        int first=track.getInt(id+"/primeira",0),last=track.getInt(id+"/ultima",-1);
        for(long n=Math.max(0,first);n<=last;n++)cancelOne(c,a,id,(int)n);
        // Compatibilidade com principal, reservas e soneca das versões até 0.15.
        for(int n=0;n<=3;n++)cancelOne(c,a,id,n);
        cancelOne(c,a,id,-1);
        track.edit().remove(id+"/primeira").remove(id+"/ultima").commit();
    }
    /** Retorna solicitações aceitas, não prova de entrega futura. */
    static int schedule(Context c, JSONObject e) {
        cancel(c,e);
        if(e.optBoolean("confirmado",false) || e.optBoolean("teste",false) || e.optBoolean("horarioInvalido",false)) return 0;
        if(!allowed(c)){markScheduleIssue(c,e,true);return 0;}
        if(!markScheduleIssue(c,e,true))return 0;
        AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        android.content.SharedPreferences track=c.getSharedPreferences(TRACK,0);
        int count=0,backups=Math.max(0,e.optInt("reservas",2));
        int interval=Math.max(1,Math.min(60,e.optInt("intervalo",5)));
        long wake=e.optLong("hora"), step=interval*60000L, now=System.currentTimeMillis();
        long first=now<wake?0:(now-wake)/step+1;
        long began=monotonicClock.getAsLong();boolean failed=false;
        String id=e.optString("id");
        for(long n=first;n<=backups;n++) {
            // Evita bloquear a interface/receptor indefinidamente para entradas enormes.
            if(monotonicClock.getAsLong()-began>=1500){failed=true;break;}
            long t=AlarmDispatchGuard.expected(wake,(int)n,backups,interval,0);
            if(t<=0){failed=true;break;}
            if(!track.edit().putInt(id+"/primeira",(int)first).putInt(id+"/ultima",(int)n).commit()){failed=true;break;}
            Intent show=new Intent(c,MainActivity.class);
            PendingIntent ui=PendingIntent.getActivity(c,0,show,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            try { a.setAlarmClock(new AlarmManager.AlarmClockInfo(t,ui),pending(c,id,(int)n,t)); count++; }
            catch(RuntimeException ex) { failed=true;break; }
        }
        markScheduleIssue(c,e,failed);
        long snooze=e.optLong("sonecaAte",0);
        if(snooze>System.currentTimeMillis()) {
            try { scheduleSnoozeIntent(c,e,snooze); }
            catch(RuntimeException ignored) { markScheduleIssue(c,e,true);History.add(c,"Não foi possível restaurar a soneca de "+e.optString("nome")+"."); }
        }
        return count;
    }
    private static boolean markScheduleIssue(Context c,JSONObject event,boolean failed){
        try{if(find(c,event.optString("id"))!=null){event.put("pedidoIncompleto",failed);return put(c,event);}}catch(Exception ignored){}return false;
    }
    private static void scheduleSnoozeIntent(Context c,JSONObject e,long when) {
        AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent ui=PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        a.setAlarmClock(new AlarmManager.AlarmClockInfo(when,ui),pending(c,e.optString("id"),-1,when));
    }
    static boolean snooze(Context c,String id,int minutes) {
        JSONObject e=find(c,id);
        if(e==null || e.optBoolean("teste") || e.optBoolean("confirmado") || !allowed(c))return false;
        try {
            long when=SnoozePolicy.until(System.currentTimeMillis(),minutes);
            e.put("sonecaAte",when);
            if(!put(c,e))return false;
            try { scheduleSnoozeIntent(c,e,when); }
            catch(RuntimeException ex) {
                e.remove("sonecaAte"); put(c,e); return false;
            }
            History.add(c,"Soneca solicitada para "+e.optString("nome")+" em "+minutes+" minuto(s). As reservas continuam ativas.");
            return true;
        } catch(Exception ex) {return false;}
    }
    static void clearFiredSnooze(Context c,String id) {
        JSONObject e=find(c,id); if(e==null || !e.has("sonecaAte"))return;
        e.remove("sonecaAte"); put(c,e);
    }
    static void restore(Context c) {
        if(!storageValid(c)){History.add(c,"Não foi possível ler os compromissos locais. Os dados não foram sobrescritos.");return;}
        JSONArray a=load(c);
        String zone=java.util.TimeZone.getDefault().getID();
        // Persiste primeiro, para que a verificação mostre as falhas também após reinício.
        for(int j=0;j<a.length();j++) {
            JSONObject e=a.optJSONObject(j);
            if(e==null || e.optBoolean("teste") || e.optBoolean("confirmado")) continue;
            try {
                if(!e.has("despertarLocal")) {
                    // Cadastros antigos não informam o fuso original; mantém o instante.
                    e.put("despertarLocal",LocalTimeRules.localAt(e.optLong("hora"),zone));
                    if(e.has("saida"))e.put("saidaLocal",LocalTimeRules.localAt(e.optLong("saida"),zone));
                }
                Long wake=LocalTimeRules.instantFor(e.getString("despertarLocal"),zone);
                Long departure=e.has("saidaLocal")?LocalTimeRules.instantFor(e.getString("saidaLocal"),zone):null;
                boolean invalid=wake==null || (e.has("saidaLocal") && departure==null) || (departure!=null && wake!=null && departure<wake);
                e.put("horarioInvalido",invalid);
                e.put("fuso",zone);
                if(!invalid) { e.put("hora",wake.longValue()); if(departure!=null)e.put("saida",departure.longValue()); }
            } catch(Exception ex) {
                try{e.put("horarioInvalido",true);}catch(Exception ignored){}
            }
        }
        if(!save(c,a))return;
        for(int j=0;j<a.length();j++){JSONObject e=a.optJSONObject(j);if(e!=null) {schedule(c,e);if(!e.optBoolean("teste")&&e.optLong("hora")<=System.currentTimeMillis())Recurrences.advance(c,e.optString("id"),System.currentTimeMillis());}}
    }
    static JSONObject find(Context c,String id) { JSONArray a=load(c); for(int j=0;j<a.length();j++){JSONObject e=a.optJSONObject(j); if(e!=null && e.optString("id").equals(id)) return e;} return null; }
    static boolean put(Context c,JSONObject event) {
        JSONArray old=load(c), updated=new JSONArray(); boolean found=false;
        for(int i=0;i<old.length();i++) { JSONObject e=old.optJSONObject(i); if(e==null)continue; if(e.optString("id").equals(event.optString("id"))) {updated.put(event);found=true;} else updated.put(e); }
        if(!found)updated.put(event);
        return save(c,updated);
    }
    static boolean remove(Context c,String id) {
        JSONArray a=load(c), out=new JSONArray(); for(int i=0;i<a.length();i++){JSONObject e=a.optJSONObject(i);if(e!=null&&!e.optString("id").equals(id))out.put(e);} return save(c,out);
    }
    static boolean confirm(Context c,String id) {
        JSONObject e=find(c,id); if(e==null) return false;
        try { e.put("confirmado",true); e.put("confirmadoEm",System.currentTimeMillis()); }
        catch(Exception ex) { return false; }
        if(!put(c,e))return false; cancel(c,e); if(e.optBoolean("teste")) remove(c,id); else History.add(c,"Despertar confirmado: "+e.optString("nome")+". Cancelamento das reservas solicitado."); return true;
    }
}
