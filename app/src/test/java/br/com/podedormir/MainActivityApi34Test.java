package br.com.podedormir;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Looper;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowAlertDialog;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class) @Config(sdk=34)
public class MainActivityApi34Test extends MainActivityApiBase {
 @Test public void disabledNotificationsAreCriticalOnAndroid14()throws Exception{
  create("Compromisso");android.app.NotificationManager manager=(android.app.NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);shadowOf(manager).setNotificationsEnabled(false);assertEquals(Readiness.Level.CRITICAL,a.readiness());a.checks();ViewLookup.textMatching(a.root,"(?s).*Notificações do aplicativo: desativadas.*");
 }
}
