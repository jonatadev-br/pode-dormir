package br.com.podedormir;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowActivity;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class) @Config(sdk=33) @LooperMode(LooperMode.Mode.PAUSED)
public class SetupGuideApi33Test {
 @Test public void deniedNotificationsAndExactAlarmsAreRequestedWithoutAssumingGrant(){
  Context c=RuntimeEnvironment.getApplication();c.getSharedPreferences("tutorial_inicial",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();c.getSharedPreferences("primeiro_acesso",0).edit().clear().commit();c.getSharedPreferences("eventos",0).edit().clear().commit();shadowOf(RuntimeEnvironment.getApplication()).denyPermissions("android.permission.POST_NOTIFICATIONS");ShadowAlarmManager.setCanScheduleExactAlarms(false);
  ActivityController<MainActivity> controller=Robolectric.buildActivity(MainActivity.class).setup();MainActivity a=controller.get();ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(android.os.Looper.getMainLooper()).idle();
  AlertDialog d=ShadowAlertDialog.getLatestAlertDialog();assertEquals("Continuar",d.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());d.getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(android.os.Looper.getMainLooper()).idle();
  ShadowActivity.PermissionsRequest request=shadowOf(a).getLastRequestedPermission();assertNotNull(request);assertEquals(SetupGuide.NOTIFICATIONS_REQUEST,request.requestCode);assertEquals("android.permission.POST_NOTIFICATIONS",request.requestedPermissions[0]);
  a.onRequestPermissionsResult(SetupGuide.NOTIFICATIONS_REQUEST,new String[]{"android.permission.POST_NOTIFICATIONS"},new int[]{PackageManager.PERMISSION_DENIED});d=ShadowAlertDialog.getLatestAlertDialog();assertEquals("Abrir ajustes",d.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());d.getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,shadowOf(a).getNextStartedActivityForResult().intent.getAction());assertFalse(Alarms.allowed(c));
  ShadowAlarmManager.setCanScheduleExactAlarms(true);controller.pause().stop().destroy();
 }
}
