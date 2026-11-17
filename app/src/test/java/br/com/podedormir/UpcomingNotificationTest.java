package br.com.podedormir;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class) @Config(sdk=29)
public class UpcomingNotificationTest {
 Context c;NotificationManager nm;long t;
 @Before public void setup(){c=RuntimeEnvironment.getApplication();c.getSharedPreferences("eventos",0).edit().clear().commit();nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);nm.cancel(UpcomingNotification.ID);shadowOf(nm).setNotificationsEnabled(true);t=System.currentTimeMillis()+3600000;}
 JSONObject event(String id,String name,long when)throws Exception{return new JSONObject().put("id",id).put("nome",name).put("hora",when).put("reservas",2).put("intervalo",5);}
 Notification current(){return shadowOf(nm).getNotification(UpcomingNotification.ID);}
 @Test public void showsNearestAndWakeTimeAndUsesSeparateQuietChannel()throws Exception{
  Alarms.put(c,event("b","Consulta",t+3600000));Alarms.put(c,event("a","Exame",t));Notification n=current();assertNotNull(n);
  assertEquals("Próximo compromisso: Exame",n.extras.getCharSequence(Notification.EXTRA_TITLE).toString());assertEquals(UpcomingNotification.wakeText(t),n.extras.getCharSequence(Notification.EXTRA_TEXT).toString());assertEquals(Notification.VISIBILITY_PUBLIC,n.visibility);assertEquals(UpcomingNotification.CHANNEL,n.getChannelId());assertNotNull(n.contentIntent);assertEquals(NotificationManager.IMPORTANCE_LOW,nm.getNotificationChannel(UpcomingNotification.CHANNEL).getImportance());
 }
 @Test public void editConfirmAndDeleteUpdateNotification()throws Exception{
  JSONObject a=event("a","Exame",t),b=event("b","Consulta",t+3600000);Alarms.put(c,a);Alarms.put(c,b);a.put("nome","Novo exame");Alarms.put(c,a);assertTrue(current().extras.getCharSequence(Notification.EXTRA_TITLE).toString().contains("Novo exame"));
  assertTrue(Alarms.confirm(c,"a"));assertTrue(current().extras.getCharSequence(Notification.EXTRA_TITLE).toString().contains("Consulta"));assertTrue(Alarms.remove(c,"b"));assertNull(current());
 }
 @Test public void ignoresPastInvalidTestAndConfirmedEntries()throws Exception{
  Alarms.put(c,event("a","Passado",t-7200000));Alarms.put(c,event("b","Inválido",t).put("horarioInvalido",true));Alarms.put(c,event("c","Teste",t).put("teste",true));Alarms.put(c,event("d","Confirmado",t).put("confirmado",true));assertNull(current());
 }
 @Test public void incompleteScheduleIsReportedAndCorruptionRemovesOldNotification()throws Exception{
  Alarms.put(c,event("a","Exame",t).put("pedidoIncompleto",true));assertTrue(current().extras.getCharSequence(Notification.EXTRA_TEXT).toString().contains("incompleto"));
  c.getSharedPreferences("eventos",0).edit().putString("lista","{inválido").commit();UpcomingNotification.refresh(c);assertNull(current());
 }
 @Test public void disabledNotificationsDoNotProduceNewReminder()throws Exception{
  shadowOf(nm).setNotificationsEnabled(false);Alarms.put(c,event("a","Exame",t));assertNull(current());shadowOf(nm).setNotificationsEnabled(true);UpcomingNotification.refresh(c);assertNotNull(current());
 }
 @Test public void ringingNotificationCarriesEventAndTime(){
  try{Alarms.put(c,event("a","Exame",t));}catch(Exception e){throw new AssertionError(e);}Notification n=AlarmService.notice(c,"a","Exame");assertEquals(Notification.VISIBILITY_PUBLIC,n.visibility);assertTrue(n.extras.getCharSequence(Notification.EXTRA_TEXT).toString().contains(UpcomingNotification.wakeText(t)));assertNotNull(n.fullScreenIntent);
 }
}
