package br.com.podedormir;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPowerManager;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class) @Config(sdk=29)
public class ScreenWakeTest {
 Context c;PowerManager power;
 @Before public void setup(){c=RuntimeEnvironment.getApplication();c.getSharedPreferences("eventos",0).edit().clear().commit();c.getSharedPreferences("historico",0).edit().clear().commit();power=(PowerManager)c.getSystemService(Context.POWER_SERVICE);ShadowPowerManager.clearWakeLocks();}
 @Test public void screenOffRequestsTimedWakeLock(){
  shadowOf(power).setIsInteractive(false);ScreenWake.attempt(c);PowerManager.WakeLock lock=ShadowPowerManager.getLatestWakeLock();assertNotNull(lock);assertTrue(lock.isHeld());assertEquals("PodeDormir:acenderAoAlarme",shadowOf(lock).getTag());org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofSeconds(11));assertFalse(lock.isHeld());
 }
 @Test public void screenAlreadyOnDoesNotAcquire(){shadowOf(power).setIsInteractive(true);ScreenWake.attempt(c);assertNull(ShadowPowerManager.getLatestWakeLock());}
 @Test public void validAlarmRequestsWakeButEarlyAlarmDoesNot()throws Exception{
  shadowOf(power).setIsInteractive(false);long due=System.currentTimeMillis()-1000;JSONObject e=new JSONObject().put("id","a").put("nome","Exame").put("hora",due).put("reservas",2).put("intervalo",5);Alarms.put(c,e);
  new AlarmReceiver().onReceive(c,new Intent(c,AlarmReceiver.class).putExtra("id","a").putExtra("numero",0).putExtra("disparoPrevisto",due+60000));assertNull(ShadowPowerManager.getLatestWakeLock());
  new AlarmReceiver().onReceive(c,new Intent(c,AlarmReceiver.class).putExtra("id","a").putExtra("numero",0).putExtra("disparoPrevisto",due));assertNotNull(ShadowPowerManager.getLatestWakeLock());
 }
}
