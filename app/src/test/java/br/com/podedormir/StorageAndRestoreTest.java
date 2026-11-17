package br.com.podedormir;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlarmManager;
import java.time.LocalDate;
import java.util.TimeZone;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class) @Config(sdk=29)
public class StorageAndRestoreTest {
 Context ctx;ShadowAlarmManager manager;
 @Before public void setup(){ctx=RuntimeEnvironment.getApplication();ctx.getSharedPreferences("eventos",0).edit().clear().commit();ctx.getSharedPreferences("historico",0).edit().clear().commit();ShadowAlarmManager.setCanScheduleExactAlarms(true);manager=shadowOf((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE));}
 JSONObject event(String id,long t)throws Exception{return new JSONObject().put("id",id).put("nome","Exame").put("hora",t).put("reservas",2).put("intervalo",5);}
 @Test public void corruptJsonCannotBeOverwritten()throws Exception{
  ctx.getSharedPreferences("eventos",0).edit().putString("lista","{conteúdo inválido").commit();assertFalse(Alarms.storageValid(ctx));assertFalse(Alarms.put(ctx,event("novo",System.currentTimeMillis()+3600000)));assertEquals("{conteúdo inválido",ctx.getSharedPreferences("eventos",0).getString("lista",""));Alarms.restore(ctx);assertEquals("{conteúdo inválido",ctx.getSharedPreferences("eventos",0).getString("lista",""));assertEquals(0,manager.getScheduledAlarms().size());
 }
 @Test public void malformedEventAndDuplicateIdsAreRejected()throws Exception{
  String missing="[{\"id\":\"a\",\"nome\":\"Exame\"}]";ctx.getSharedPreferences("eventos",0).edit().putString("lista",missing).commit();assertFalse(Alarms.storageValid(ctx));assertFalse(Alarms.put(ctx,event("novo",System.currentTimeMillis()+3600000)));assertEquals(missing,ctx.getSharedPreferences("eventos",0).getString("lista",""));
  long t=System.currentTimeMillis()+3600000;String repeated="["+event("a",t)+","+event("a",t)+"]";ctx.getSharedPreferences("eventos",0).edit().putString("lista",repeated).commit();assertFalse(Alarms.storageValid(ctx));assertFalse(Alarms.save(ctx,new org.json.JSONArray()));assertEquals(repeated,ctx.getSharedPreferences("eventos",0).getString("lista",""));
 }
 @Test public void damagedHistoryIsPreservedUntilExplicitClear(){
  ctx.getSharedPreferences("historico",0).edit().putString("registros","[inválido").commit();assertFalse(History.storageValid(ctx));assertFalse(History.add(ctx,"não sobrescrever"));assertEquals("[inválido",ctx.getSharedPreferences("historico",0).getString("registros",""));assertTrue(History.clear(ctx));assertTrue(History.storageValid(ctx));assertTrue(History.add(ctx,"novo registro"));
 }
 @Test public void onlySupportedRestoreActionsReschedule()throws Exception{
  JSONObject e=event("a",System.currentTimeMillis()+3600000);Alarms.put(ctx,e);Alarms.schedule(ctx,e);Alarms.cancel(ctx,e);assertEquals(0,manager.getScheduledAlarms().size());
  RestoreReceiver r=new RestoreReceiver();r.onReceive(ctx,new Intent("acao.desconhecida"));assertEquals(0,manager.getScheduledAlarms().size());r.onReceive(ctx,new Intent(Intent.ACTION_BOOT_COMPLETED));assertEquals(3,manager.getScheduledAlarms().size());
 }
 @Test public void timezoneChangePreservesLocalTime()throws Exception{
  TimeZone original=TimeZone.getDefault();try{TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));String local=LocalDate.now().plusDays(3).toString()+"T06:00";long before=LocalTimeRules.instantFor(local,"America/Sao_Paulo");JSONObject e=event("a",before).put("despertarLocal",local).put("fuso","America/Sao_Paulo");Alarms.put(ctx,e);Alarms.schedule(ctx,e);TimeZone.setDefault(TimeZone.getTimeZone("America/Manaus"));new RestoreReceiver().onReceive(ctx,new Intent(Intent.ACTION_TIMEZONE_CHANGED));assertEquals(before+3600000,Alarms.find(ctx,"a").getLong("hora"));assertEquals(3,manager.getScheduledAlarms().size());android.app.NotificationManager notifications=(android.app.NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);android.app.Notification reminder=shadowOf(notifications).getNotification(UpcomingNotification.ID);assertNotNull(reminder);assertTrue(reminder.extras.getCharSequence(android.app.Notification.EXTRA_TEXT).toString().contains(UpcomingNotification.wakeText(before+3600000)));}finally{TimeZone.setDefault(original);}
 }
 @Test public void corruptStorageShowsWarningInsteadOfEmptyState(){
  ctx.getSharedPreferences("tutorial_inicial",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();ctx.getSharedPreferences("primeiro_acesso",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();ctx.getSharedPreferences("eventos",0).edit().putString("lista","[incompleto").commit();org.robolectric.android.controller.ActivityController<MainActivity> c=Robolectric.buildActivity(MainActivity.class).setup();MainActivity a=c.get();assertEquals(Readiness.Level.CRITICAL,a.readiness());ViewLookup.textMatching(a.root,"(?s).*Não foi possível ler os compromissos.*");ViewLookup.textMatching(a.root,"(?s).*Os dados foram preservados.*");a.checks();ViewLookup.textMatching(a.root,"(?s).*não podemos verificar seus alarmes.*");c.pause().stop().destroy();
 }
}
