package br.com.podedormir;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

/** Verifica fluxo de serviço simulado, não mede áudio ou restrições reais. */
@RunWith(RobolectricTestRunner.class) @Config(sdk=29)
public class AlarmServiceTest {
 Context ctx;
 @Before public void before(){ctx=RuntimeEnvironment.getApplication();ctx.getSharedPreferences("eventos",0).edit().clear().commit();ctx.getSharedPreferences("alertas_ativos",0).edit().clear().commit();ctx.getSharedPreferences("historico",0).edit().clear().commit();ctx.getSharedPreferences("configuracoes",0).edit().putBoolean("vibrar",false).commit();}
 JSONObject event(String id)throws Exception{return new JSONObject().put("id",id).put("nome","Compromisso").put("hora",System.currentTimeMillis()+60000);}
 Intent start(String id){return new Intent(ctx,AlarmService.class).setAction(AlarmService.START).putExtra("id",id);}
 Set<String> persisted(){return new HashSet<>(ctx.getSharedPreferences("alertas_ativos",0).getStringSet("ids",new HashSet<>()));}
 @Test public void restartedServiceAttemptsToResumeActiveAlert()throws Exception{
  Alarms.put(ctx,event("a"));ServiceController<AlarmService> one=Robolectric.buildService(AlarmService.class).create();AlarmService s=one.get();assertEquals(Service.START_STICKY,s.onStartCommand(start("a"),0,1));assertTrue(persisted().contains("a"));assertNotNull(shadowOf(s).getLastForegroundNotification());one.destroy();
  ServiceController<AlarmService> two=Robolectric.buildService(AlarmService.class).create();AlarmService restarted=two.get();assertEquals(Service.START_STICKY,restarted.onStartCommand(null,0,2));assertNotNull(shadowOf(restarted).getLastForegroundNotification());assertTrue(persisted().contains("a"));two.destroy();
 }
 @Test public void confirmedAlertIsNotResumed()throws Exception{
  Alarms.put(ctx,event("a"));ServiceController<AlarmService> one=Robolectric.buildService(AlarmService.class).create();one.get().onStartCommand(start("a"),0,1);assertTrue(Alarms.confirm(ctx,"a"));one.destroy();
  ServiceController<AlarmService> two=Robolectric.buildService(AlarmService.class).create();AlarmService service=two.get();assertEquals(Service.START_NOT_STICKY,service.onStartCommand(null,0,2));assertTrue(persisted().isEmpty());assertTrue(shadowOf(service).isStoppedBySelf());two.destroy();
 }
 @Test public void confirmingOneOfTwoKeepsOtherActive()throws Exception{
  Alarms.put(ctx,event("a"));Alarms.put(ctx,event("b"));ServiceController<AlarmService> c=Robolectric.buildService(AlarmService.class).create();AlarmService s=c.get();s.onStartCommand(start("a"),0,1);s.onStartCommand(start("b"),0,2);assertEquals(2,persisted().size());assertTrue(Alarms.confirm(ctx,"a"));assertEquals(Service.START_STICKY,s.onStartCommand(new Intent(ctx,AlarmService.class).setAction(AlarmService.CONFIRM).putExtra("id","a"),0,3));assertFalse(persisted().contains("a"));assertTrue(persisted().contains("b"));assertFalse(shadowOf(s).isStoppedBySelf());c.destroy();
 }
 @Test public void missingEventDoesNotCreateActiveAlert(){ServiceController<AlarmService> c=Robolectric.buildService(AlarmService.class).create();AlarmService s=c.get();assertEquals(Service.START_NOT_STICKY,s.onStartCommand(start("inexistente"),0,1));assertTrue(persisted().isEmpty());c.destroy();}
 @Test public void foregroundNotificationIncludesFullScreenRequest(){
  android.app.Notification n=AlarmService.notice(ctx,"a","Consulta");assertNotNull(n.fullScreenIntent);assertEquals(android.app.Notification.CATEGORY_ALARM,n.category);assertNotNull(n.contentIntent);
 }

}
