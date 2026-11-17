package br.com.podedormir;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35)
public class SetupGuideApi35Test {
 @Test public void missingFullScreenPermissionOpensSystemSettings(){
  Context c=RuntimeEnvironment.getApplication();NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);assertFalse(nm.canUseFullScreenIntent());
  c.getSharedPreferences("tutorial_inicial",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();c.getSharedPreferences("eventos",0).edit().clear().commit();c.getSharedPreferences("primeiro_acesso",0).edit().putBoolean("visto",true).putInt("etapa",3).commit();ActivityController<MainActivity> controller=Robolectric.buildActivity(MainActivity.class).setup();MainActivity a=controller.get();AlertDialog d=ShadowAlertDialog.getLatestAlertDialog();assertTrue(d.isShowing());assertEquals("Abrir ajustes",d.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString());d.getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,shadowOf(a).getNextStartedActivityForResult().intent.getAction());assertFalse(nm.canUseFullScreenIntent());controller.pause().stop().destroy();
 }
}
