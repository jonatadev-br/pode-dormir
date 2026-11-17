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

@RunWith(RobolectricTestRunner.class) @Config(sdk=35)
public class MainActivityApi35Test extends MainActivityApiBase {
 @Test public void android15InsetsProtectHomeContent(){
  android.view.View scroll=(android.view.View)a.root.getParent();android.view.WindowInsets insets=new android.view.WindowInsets.Builder().setInsets(android.view.WindowInsets.Type.systemBars(),android.graphics.Insets.of(3,24,5,32)).build();scroll.dispatchApplyWindowInsets(insets);
  assertEquals(3,scroll.getPaddingLeft());assertEquals(24,scroll.getPaddingTop());assertEquals(5,scroll.getPaddingRight());assertEquals(32,scroll.getPaddingBottom());
 }
 @Test public void android15DeniedExactPermissionWarns()throws Exception{
  ShadowAlarmManager.setCanScheduleExactAlarms(false);create("Compromisso");assertTrue(Alarms.load(ctx).getJSONObject(0).optBoolean("pedidoIncompleto"));assertEquals(0,alarm.getScheduledAlarms().size());assertEquals(Readiness.Level.CRITICAL,a.readiness());
 }
}
