package br.com.podedormir;

import android.app.AlarmManager;
import android.content.Context;
import org.robolectric.RuntimeEnvironment;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlarmManager;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

/** Testes com sistema Android simulado; não substituem teste em aparelho. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk=29)
public class AlarmsTest {
 private Context ctx; private ShadowAlarmManager alarms; private long t;
 @Before public void setUp(){ctx=RuntimeEnvironment.getApplication();ShadowAlarmManager.setCanScheduleExactAlarms(true);ctx.getSharedPreferences("eventos",0).edit().clear().commit();ctx.getSharedPreferences("historico",0).edit().clear().commit();alarms=shadowOf((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE));t=System.currentTimeMillis()+3600000L;}
 private JSONObject event(String id,long when,int backups)throws Exception{return new JSONObject().put("id",id).put("nome","Consulta").put("hora",when).put("reservas",backups).put("intervalo",5);}
 @Test public void createsThreeIndependentAttempts() throws Exception {JSONObject e=event("a",t,2);assertTrue(Alarms.put(ctx,e));assertEquals(3,Alarms.schedule(ctx,e));assertEquals(3,alarms.getScheduledAlarms().size());assertEquals(t,alarms.getScheduledAlarms().get(0).triggerAtTime);assertEquals(t+300000,alarms.getScheduledAlarms().get(1).triggerAtTime);assertEquals(t+600000,alarms.getScheduledAlarms().get(2).triggerAtTime);}
 @Test public void editingReplacesOldSchedule()throws Exception{JSONObject e=event("a",t,2);assertTrue(Alarms.put(ctx,e));Alarms.schedule(ctx,e);e.put("hora",t+6000000);e.put("reservas",1);assertTrue(Alarms.put(ctx,e));assertEquals(2,Alarms.schedule(ctx,e));assertEquals(2,alarms.getScheduledAlarms().size());assertEquals(t+6000000,alarms.getScheduledAlarms().get(0).triggerAtTime);}
 @Test public void cancellingOneDoesNotCancelAnother()throws Exception{JSONObject a=event("a",t,2),b=event("b",t+10000,2);Alarms.put(ctx,a);Alarms.put(ctx,b);Alarms.schedule(ctx,a);Alarms.schedule(ctx,b);assertEquals(6,alarms.getScheduledAlarms().size());Alarms.cancel(ctx,a);assertEquals(3,alarms.getScheduledAlarms().size());}
 @Test public void confirmPersistsAndPreventsRestoration()throws Exception{JSONObject e=event("a",t,2);Alarms.put(ctx,e);Alarms.schedule(ctx,e);assertTrue(Alarms.confirm(ctx,"a"));assertTrue(Alarms.find(ctx,"a").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());Alarms.restore(ctx);assertEquals(0,alarms.getScheduledAlarms().size());}
 @Test public void snoozeKeepsBackupsAndConfirmCancelsAll()throws Exception{JSONObject e=event("a",t,2);Alarms.put(ctx,e);Alarms.schedule(ctx,e);assertTrue(Alarms.snooze(ctx,"a",5));assertEquals(4,alarms.getScheduledAlarms().size());assertTrue(Alarms.confirm(ctx,"a"));assertEquals(0,alarms.getScheduledAlarms().size());}
 @Test @Config(sdk=33) public void deniesExactAlarmPermission()throws Exception{JSONObject e=event("a",t,2);Alarms.put(ctx,e);ShadowAlarmManager.setCanScheduleExactAlarms(false);assertFalse(Alarms.allowed(ctx));assertEquals(0,Alarms.schedule(ctx,e));assertEquals(0,alarms.getScheduledAlarms().size());assertTrue(Alarms.find(ctx,"a").optBoolean("pedidoIncompleto"));assertFalse(Alarms.snooze(ctx,"a",5));ShadowAlarmManager.setCanScheduleExactAlarms(true);assertEquals(3,Alarms.schedule(ctx,Alarms.find(ctx,"a")));assertFalse(Alarms.find(ctx,"a").optBoolean("pedidoIncompleto"));}
 @Test public void restoresUpcomingAlarmRequests()throws Exception{JSONObject e=event("a",t,2);Alarms.put(ctx,e);Alarms.schedule(ctx,e);Alarms.cancel(ctx,e);assertEquals(0,alarms.getScheduledAlarms().size());Alarms.restore(ctx);assertEquals(3,alarms.getScheduledAlarms().size());}
 @Test public void invalidTimeIsNotScheduled()throws Exception{JSONObject e=event("a",t,2).put("horarioInvalido",true);Alarms.put(ctx,e);assertEquals(0,Alarms.schedule(ctx,e));assertEquals(0,alarms.getScheduledAlarms().size());}
 @Test public void firedAlarmStartsServiceAndRecordsAttempt()throws Exception{long due=System.currentTimeMillis()-1000;JSONObject e=event("a",due,2);Alarms.put(ctx,e);android.content.Intent i=new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a").putExtra("numero",0).putExtra("disparoPrevisto",due);new AlarmReceiver().onReceive(ctx,i);assertEquals(1,History.load(ctx).length());android.content.Intent started=shadowOf((android.app.Application)ctx).getNextStartedService();assertNotNull(started);assertEquals("a",started.getStringExtra("id"));assertEquals(AlarmService.START,started.getAction());}
 @Test public void confirmedAlarmReceiverIgnoresLateDelivery()throws Exception{JSONObject e=event("a",t,2);Alarms.put(ctx,e);assertTrue(Alarms.confirm(ctx,"a"));int prior=History.load(ctx).length();new AlarmReceiver().onReceive(ctx,new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a"));assertEquals(prior,History.load(ctx).length());assertNull(shadowOf((android.app.Application)ctx).getNextStartedService());}

 @Test public void recurringCreatesSeparateNextAndIsIdempotent()throws Exception{
  String zone=java.util.TimeZone.getDefault().getID();JSONObject e=event("series",t,2).put("recorrencia","diaria").put("serieId","series").put("despertarLocal",LocalTimeRules.localAt(t,zone));
  assertTrue(Alarms.put(ctx,e));assertEquals(3,Alarms.schedule(ctx,e));assertTrue(Recurrences.advance(ctx,"series",System.currentTimeMillis()));assertEquals(2,Alarms.load(ctx).length());assertEquals(6,alarms.getScheduledAlarms().size());
  assertTrue(Recurrences.advance(ctx,"series",System.currentTimeMillis()));assertEquals(2,Alarms.load(ctx).length());assertEquals(6,alarms.getScheduledAlarms().size());
  assertTrue(Alarms.confirm(ctx,"series"));assertEquals(3,alarms.getScheduledAlarms().size());
 }
 @Test public void deleteSeriesRemovesIndependentOccurrences()throws Exception{
  String zone=java.util.TimeZone.getDefault().getID();JSONObject e=event("series",t,2).put("recorrencia","semanal").put("serieId","series").put("despertarLocal",LocalTimeRules.localAt(t,zone));
  Alarms.put(ctx,e);Alarms.schedule(ctx,e);assertTrue(Recurrences.advance(ctx,"series",System.currentTimeMillis()));JSONObject other=event("other",t,1);Alarms.put(ctx,other);Alarms.schedule(ctx,other);
  assertEquals(8,alarms.getScheduledAlarms().size());assertTrue(Recurrences.removeSeries(ctx,"series"));assertEquals(1,Alarms.load(ctx).length());assertEquals(2,alarms.getScheduledAlarms().size());
 }
 @Test public void missedRecurringOccurrenceRestoresNextFuture()throws Exception{
  String zone=java.util.TimeZone.getDefault().getID();long past=System.currentTimeMillis()-86400000L;JSONObject e=event("series",past,2).put("recorrencia","diaria").put("serieId","series").put("despertarLocal",LocalTimeRules.localAt(past,zone));
  Alarms.put(ctx,e);Alarms.restore(ctx);assertEquals(2,Alarms.load(ctx).length());assertEquals(3,alarms.getScheduledAlarms().size());assertTrue(Alarms.find(ctx,"series").optBoolean("proximaGerada"));
 }

 @Test public void staleAlarmAfterEditIsIgnored()throws Exception{
  long old=System.currentTimeMillis()-1000;JSONObject e=event("a",old,2);Alarms.put(ctx,e);e.put("hora",t);Alarms.put(ctx,e);
  new AlarmReceiver().onReceive(ctx,new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a").putExtra("numero",0).putExtra("disparoPrevisto",old));
  assertNull(shadowOf((android.app.Application)ctx).getNextStartedService());assertTrue(History.load(ctx).getJSONObject(0).getString("detalhe").contains("ignorado"));
 }
 @Test public void earlyDeliveryIsIgnored()throws Exception{
  JSONObject e=event("a",t,2);Alarms.put(ctx,e);new AlarmReceiver().onReceive(ctx,new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a").putExtra("numero",0).putExtra("disparoPrevisto",t));assertNull(shadowOf((android.app.Application)ctx).getNextStartedService());
 }
 @Test public void oldSnoozeAndRemovedBackupAreIgnored()throws Exception{
  long due=System.currentTimeMillis()-1000;JSONObject e=event("a",due,0).put("sonecaAte",due+60000);Alarms.put(ctx,e);
  new AlarmReceiver().onReceive(ctx,new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a").putExtra("numero",-1).putExtra("disparoPrevisto",due));assertTrue(Alarms.find(ctx,"a").has("sonecaAte"));
  new AlarmReceiver().onReceive(ctx,new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a").putExtra("numero",1).putExtra("disparoPrevisto",due+300000));assertNull(shadowOf((android.app.Application)ctx).getNextStartedService());
 }
 @Test public void missingTimestampInLegacyAlarmIsIgnored()throws Exception{
  JSONObject e=event("a",System.currentTimeMillis()-1000,2);Alarms.put(ctx,e);new AlarmReceiver().onReceive(ctx,new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a").putExtra("numero",0));assertNull(shadowOf((android.app.Application)ctx).getNextStartedService());
 }
 @Test public void scheduledIntentsCarryCorrectTimes()throws Exception{
  JSONObject e=event("a",t,2);Alarms.put(ctx,e);Alarms.schedule(ctx,e);
  for(int n=0;n<3;n++)assertEquals(t+n*300000L,shadowOf(alarms.getScheduledAlarms().get(n).operation).getSavedIntent().getLongExtra("disparoPrevisto",-1));
  assertTrue(Alarms.snooze(ctx,"a",5));boolean found=false;
  for(ShadowAlarmManager.ScheduledAlarm a:alarms.getScheduledAlarms())if(shadowOf(a.operation).getSavedIntent().getIntExtra("numero",-2)==-1){found=true;assertEquals(Alarms.find(ctx,"a").getLong("sonecaAte"),shadowOf(a.operation).getSavedIntent().getLongExtra("disparoPrevisto",-1));}assertTrue(found);
 }

 @Test public void manyBackupsAndSnoozeHaveDistinctIdentitiesAndConfirmCancelsAll()throws Exception{
  JSONObject e=event("a",t,8),other=event("b",t,4);Alarms.put(ctx,e);Alarms.put(ctx,other);
  assertEquals(9,Alarms.schedule(ctx,e));assertEquals(5,Alarms.schedule(ctx,other));assertTrue(Alarms.snooze(ctx,"a",5));assertEquals(15,alarms.getScheduledAlarms().size());
  assertTrue(Alarms.confirm(ctx,"a"));assertEquals(5,alarms.getScheduledAlarms().size());
  for(ShadowAlarmManager.ScheduledAlarm a:alarms.getScheduledAlarms())assertEquals("b",shadowOf(a.operation).getSavedIntent().getStringExtra("id"));
  assertTrue(Alarms.find(ctx,"a").optBoolean("confirmado"));
 }
 @Test public void editingToFewerBackupsCancelsAllOldOnes()throws Exception{
  JSONObject e=event("a",t,9);Alarms.put(ctx,e);assertEquals(10,Alarms.schedule(ctx,e));
  e=event("a",t+60000,4);Alarms.put(ctx,e);assertEquals(5,Alarms.schedule(ctx,e));assertEquals(5,alarms.getScheduledAlarms().size());
  assertTrue(Alarms.confirm(ctx,"a"));assertEquals(0,alarms.getScheduledAlarms().size());
 }
 @Test public void highBackupAttemptIsAcceptedAndAfterConfirmationIgnored()throws Exception{
  long due=System.currentTimeMillis()-1000;JSONObject e=event("a",due-4*300000,8);Alarms.put(ctx,e);
  android.content.Intent i=new android.content.Intent(ctx,AlarmReceiver.class).putExtra("id","a").putExtra("numero",4).putExtra("disparoPrevisto",due);
  new AlarmReceiver().onReceive(ctx,i);assertNotNull(shadowOf((android.app.Application)ctx).getNextStartedService());
  assertTrue(Alarms.confirm(ctx,"a"));new AlarmReceiver().onReceive(ctx,i);assertNull(shadowOf((android.app.Application)ctx).getNextStartedService());
 }

 @Test public void partialSchedulingWarnsAndConfirmedCancelsAllAccepted()throws Exception{
  JSONObject e=event("a",t,20);Alarms.put(ctx,e);final int[] calls={0};Alarms.monotonicClock=()->calls[0]++<3?0L:2000L;
  try{assertEquals(2,Alarms.schedule(ctx,e));assertTrue(Alarms.find(ctx,"a").optBoolean("pedidoIncompleto"));assertEquals(2,alarms.getScheduledAlarms().size());assertTrue(Alarms.confirm(ctx,"a"));assertEquals(0,alarms.getScheduledAlarms().size());}
  finally{Alarms.monotonicClock=android.os.SystemClock::elapsedRealtime;}
 }

}
