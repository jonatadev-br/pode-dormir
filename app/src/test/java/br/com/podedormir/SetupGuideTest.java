package br.com.podedormir;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.os.Build;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class) @Config(sdk=29)
public class SetupGuideTest {
 Context c;ActivityController<MainActivity> controller;MainActivity a;String maker;
 @Before public void setup(){c=RuntimeEnvironment.getApplication();c.getSharedPreferences("tutorial_inicial",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();c.getSharedPreferences("primeiro_acesso",0).edit().clear().commit();c.getSharedPreferences("eventos",0).edit().clear().commit();maker=Build.MANUFACTURER;ReflectionHelpers.setStaticField(Build.class,"MANUFACTURER","Xiaomi");ShadowAlarmManager.setCanScheduleExactAlarms(true);}
 @After public void cleanup(){AlertDialog d=ShadowAlertDialog.getLatestAlertDialog();if(d!=null&&d.isShowing())d.dismiss();if(controller!=null)controller.pause().stop().destroy();ReflectionHelpers.setStaticField(Build.class,"MANUFACTURER",maker);}
 void launch(){controller=Robolectric.buildActivity(MainActivity.class).setup();a=controller.get();}
 AlertDialog dialog(){AlertDialog d=ShadowAlertDialog.getLatestAlertDialog();assertNotNull(d);assertTrue(d.isShowing());return d;}
 void press(int which){dialog().getButton(which).performClick();shadowOf(android.os.Looper.getMainLooper()).idle();}
 @Test public void firstOpenGuidesToNotificationsAndXiaomiSettings()throws Exception{
  launch();assertEquals("Começar",dialog().getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());press(AlertDialog.BUTTON_POSITIVE);
  assertEquals("Abrir ajustes",dialog().getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);assertNotNull(nm.getNotificationChannel("alarmes_continuos_v1"));assertNotNull(nm.getNotificationChannel(UpcomingNotification.CHANNEL));
  press(AlertDialog.BUTTON_POSITIVE);assertEquals(Settings.ACTION_APP_NOTIFICATION_SETTINGS,shadowOf(a).getNextStartedActivityForResult().intent.getAction());a.onResume();
  assertEquals("Abrir informações do app",dialog().getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());press(AlertDialog.BUTTON_POSITIVE);assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,shadowOf(a).getNextStartedActivityForResult().intent.getAction());a.onResume();
  assertEquals("Entendi",dialog().getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());assertEquals(0,c.getSharedPreferences("primeiro_acesso",0).getInt("etapa",-1));press(AlertDialog.BUTTON_POSITIVE);
 }
 @Test public void skippingWelcomeDoesNotRepeatAndCanBeReopened(){
  launch();press(AlertDialog.BUTTON_NEGATIVE);assertTrue(c.getSharedPreferences("primeiro_acesso",0).getBoolean("visto",false));a.onResume();assertFalse(ShadowAlertDialog.getLatestAlertDialog().isShowing());
  a.checks();ViewLookup.button(a.root,"Configurar permissões").performClick();assertEquals("Abrir ajustes",dialog().getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());
 }
 @Test public void skippingSettingsNeverClaimsPermissionWasGranted(){
  launch();press(AlertDialog.BUTTON_POSITIVE);press(AlertDialog.BUTTON_NEGATIVE);press(AlertDialog.BUTTON_NEGATIVE);assertEquals("Entendi",dialog().getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());assertEquals(0,c.getSharedPreferences("primeiro_acesso",0).getInt("etapa",-1));
 }
}
