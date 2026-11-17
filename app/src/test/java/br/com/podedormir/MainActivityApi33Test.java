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

@RunWith(RobolectricTestRunner.class) @Config(sdk=33)
public class MainActivityApi33Test extends MainActivityApiBase {
 @Test public void deniedPermissionSurfacesSchedulingProblem()throws Exception{
  ShadowAlarmManager.setCanScheduleExactAlarms(false);create("Consulta sem permissão");assertEquals(1,Alarms.load(ctx).length());assertTrue(Alarms.load(ctx).getJSONObject(0).optBoolean("pedidoIncompleto"));assertEquals(0,alarm.getScheduledAlarms().size());assertEquals(Readiness.Level.CRITICAL,a.readiness());
 }
}
