package br.com.podedormir;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Mostra somente o próximo compromisso, sem afirmar que o alarme foi garantido. */
final class UpcomingNotification {
 static final String CHANNEL="proximo_compromisso_v1";
 static final int ID=17002;
 private UpcomingNotification(){}
 static String wakeText(long time){return "Acordar: "+new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm",new Locale("pt","BR")).format(new Date(time));}
 static void ensureChannel(Context c){
  NotificationManager manager=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);if(manager==null)return;
  NotificationChannel ch=new NotificationChannel(CHANNEL,"Próximo compromisso",NotificationManager.IMPORTANCE_LOW);
  ch.setDescription("Mostra o nome do compromisso e o horário de acordar; o aparelho pode ocultar detalhes na tela de bloqueio.");ch.setSound(null,null);ch.enableVibration(false);ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);manager.createNotificationChannel(ch);
 }
 static void refresh(Context c){
  NotificationManager manager=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(manager==null)return;
  try{
   if(!Alarms.storageValid(c)){manager.cancel(ID);return;}
   JSONArray all=Alarms.load(c);JSONObject next=null;long now=System.currentTimeMillis();
   for(int i=0;i<all.length();i++){
    JSONObject e=all.optJSONObject(i);if(e==null||e.optBoolean("teste")||e.optBoolean("confirmado")||e.optBoolean("horarioInvalido")||e.optLong("hora")<=now)continue;
    if(next==null||e.optLong("hora")<next.optLong("hora"))next=e;
   }
   if(next==null){manager.cancel(ID);return;}
   ensureChannel(c);
   if(!manager.areNotificationsEnabled())return;
   String title="Próximo compromisso: "+next.optString("nome");
   String details=wakeText(next.optLong("hora"))+(next.optBoolean("pedidoIncompleto")?" · Atenção: agendamento incompleto":"");
   PendingIntent open=PendingIntent.getActivity(c,17302,new Intent(c,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
   Notification n=new Notification.Builder(c,CHANNEL).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle(title).setContentText(details).setStyle(new Notification.BigTextStyle().bigText(details)).setVisibility(Notification.VISIBILITY_PUBLIC).setCategory(Notification.CATEGORY_REMINDER).setOnlyAlertOnce(true).setOngoing(true).setContentIntent(open).build();
   manager.notify(ID,n);
  }catch(RuntimeException ignored){ /* Permissões e configurações do sistema podem impedir a notificação. */ }
 }
}
