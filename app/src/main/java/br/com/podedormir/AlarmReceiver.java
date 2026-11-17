package br.com.podedormir;
import android.content.*;
import android.os.Build;
import org.json.JSONObject;

public class AlarmReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c, Intent intent) {
  String id=intent.getStringExtra("id"); JSONObject event=Alarms.find(c,id);
  if(event==null || event.optBoolean("confirmado",false) || event.optBoolean("horarioInvalido",false))return;
  int attempt=intent.getIntExtra("numero",-2);
  long expected=AlarmDispatchGuard.expected(event.optLong("hora",0),attempt,event.optInt("reservas",2),event.optInt("intervalo",5),event.optLong("sonecaAte",0));
  if(!AlarmDispatchGuard.accept(intent.getLongExtra("disparoPrevisto",-1),expected,System.currentTimeMillis())){
   History.add(c,"Um disparo desatualizado ou adiantado foi ignorado: "+event.optString("nome")+". Confira os alarmes cadastrados.");return;
  }
  ScreenWake.attempt(c);UpcomingNotification.refresh(c);
  if(!Recurrences.advance(c,id,System.currentTimeMillis()))History.add(c,"A próxima ocorrência não pôde ser preparada. Verifique a série.");
  if(attempt==-1) Alarms.clearFiredSnooze(c,id);
  History.add(c,"O Android acionou "+(attempt==-1?"a soneca":"a tentativa "+((long)attempt+1))+" do compromisso: "+event.optString("nome")+". Isso não comprova que o som foi ouvido.");
  Intent service=new Intent(c,AlarmService.class).setAction(AlarmService.START).putExtra("id",id);
  try { if(Build.VERSION.SDK_INT>=26)c.startForegroundService(service);else c.startService(service); }
  catch(RuntimeException failure) { AlarmService.fallback(c,id,event.optString("nome")); }
 }
}
