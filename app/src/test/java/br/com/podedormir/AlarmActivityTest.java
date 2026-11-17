package br.com.podedormir;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowAlertDialog;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class) @Config(sdk=29) @LooperMode(LooperMode.Mode.PAUSED)
public class AlarmActivityTest {
 Context ctx;ShadowAlarmManager alarms;
 @Before public void setup(){ctx=RuntimeEnvironment.getApplication();ctx.getSharedPreferences("eventos",0).edit().clear().commit();ctx.getSharedPreferences("configuracoes",0).edit().clear().commit();ShadowAlarmManager.setCanScheduleExactAlarms(true);alarms=shadowOf((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE));}
 JSONObject event()throws Exception{return new JSONObject().put("id","alarme").put("nome","Consulta").put("hora",System.currentTimeMillis()+3600000).put("reservas",2).put("intervalo",5);}
 ActivityController<AlarmActivity> launch(){return Robolectric.buildActivity(AlarmActivity.class,new Intent(ctx,AlarmActivity.class).putExtra("id","alarme")).setup();}
 @Test public void clockUpdatesAndConfirmationCancelsBackups()throws Exception{
  JSONObject event=event();Alarms.put(ctx,event);Alarms.schedule(ctx,event);ActivityController<AlarmActivity> controller=launch();AlarmActivity a=controller.get();View view=a.findViewById(android.R.id.content);
  TextView clock=ViewLookup.textMatching(view,"[0-9]{2}:[0-9]{2}");String before=clock.getText().toString();long simulatedTime=System.currentTimeMillis()+120000L;a.wallClock=()->simulatedTime;shadowOf(Looper.getMainLooper()).idleFor(1,TimeUnit.SECONDS);assertNotEquals(before,clock.getText().toString());ViewLookup.textMatching(view,"(?s).*Hora de acordar!.*Consulta.*");
  a.onBackPressed();assertFalse(a.isFinishing());ViewLookup.button(view,"↑  Arraste para cima ou toque para desativar").performClick();shadowOf(Looper.getMainLooper()).idle();assertTrue(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());assertTrue(a.isFinishing());controller.destroy();
 }
 @Test public void snoozeSchedulesWithoutCancellingBackups()throws Exception{
  JSONObject event=event();Alarms.put(ctx,event);Alarms.schedule(ctx,event);ActivityController<AlarmActivity> controller=launch();AlarmActivity a=controller.get();ViewLookup.button(a.findViewById(android.R.id.content),"Soneca: 5 min").performClick();AlertDialog dialog=ShadowAlertDialog.getLatestAlertDialog();dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(Looper.getMainLooper()).idle();assertFalse(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertTrue(Alarms.find(ctx,"alarme").has("sonecaAte"));assertEquals(4,alarms.getScheduledAlarms().size());controller.destroy();
 }
 @Test @Config(sdk=33) public void deniedExactAlarmPermissionKeepsAlertOpenOnSnooze()throws Exception{
  JSONObject event=event();Alarms.put(ctx,event);Alarms.schedule(ctx,event);ShadowAlarmManager.setCanScheduleExactAlarms(false);ActivityController<AlarmActivity> controller=launch();AlarmActivity a=controller.get();
  ViewLookup.button(a.findViewById(android.R.id.content),"Soneca: 5 min").performClick();ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(Looper.getMainLooper()).idle();
  assertFalse(a.isFinishing());assertFalse(Alarms.find(ctx,"alarme").has("sonecaAte"));assertEquals(3,alarms.getScheduledAlarms().size());controller.destroy();
 }

 @Test @Config(sdk=35) public void android15AlarmScreenReceivesInsets()throws Exception{
  JSONObject e=event();Alarms.put(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity activity=c.get();android.view.ViewGroup content=activity.findViewById(android.R.id.content);android.view.View scroll=content.getChildAt(0);
  android.view.WindowInsets insets=new android.view.WindowInsets.Builder().setInsets(android.view.WindowInsets.Type.systemBars(),android.graphics.Insets.of(0,26,0,32)).build();scroll.dispatchApplyWindowInsets(insets);assertEquals(26,scroll.getPaddingTop());assertEquals(32,scroll.getPaddingBottom());ViewLookup.button(content,"↑  Arraste para cima ou toque para desativar");c.destroy();
 }

 @Test public void awakeButtonCancelsManyBackupsAndSnooze()throws Exception{
  JSONObject e=event().put("reservas",8);Alarms.put(ctx,e);assertEquals(9,Alarms.schedule(ctx,e));assertTrue(Alarms.snooze(ctx,"alarme",5));assertEquals(10,alarms.getScheduledAlarms().size());
  ActivityController<AlarmActivity> c=launch();ViewLookup.button(c.get().findViewById(android.R.id.content),"↑  Arraste para cima ou toque para desativar").performClick();shadowOf(Looper.getMainLooper()).idle();
  assertTrue(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());c.destroy();
 }

 @Test public void slidingUpDismissesAndCancelsBackups()throws Exception{
  JSONObject e=event().put("reservas",6);Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();View b=ViewLookup.button(a.findViewById(android.R.id.content),"↑  Arraste para cima ou toque para desativar");
  long t=android.os.SystemClock.uptimeMillis();a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t,android.view.MotionEvent.ACTION_DOWN,10,400,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+100,android.view.MotionEvent.ACTION_MOVE,10,200,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+200,android.view.MotionEvent.ACTION_UP,10,20,0));
  assertTrue(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());assertTrue(a.isFinishing());c.destroy();
 }
 @Test public void shortOrDownwardDragDoesNotDismiss()throws Exception{
  JSONObject e=event();Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();View b=ViewLookup.button(a.findViewById(android.R.id.content),"↑  Arraste para cima ou toque para desativar");long t=android.os.SystemClock.uptimeMillis();
  a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t,android.view.MotionEvent.ACTION_DOWN,10,50,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+100,android.view.MotionEvent.ACTION_UP,10,150,0));
  assertFalse(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(3,alarms.getScheduledAlarms().size());c.destroy();
 }
 @Test public void volumeKeysDismissWhenActivityIsOpen()throws Exception{
  JSONObject e=event().put("reservas",5);Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();assertTrue(a.onKeyDown(android.view.KeyEvent.KEYCODE_VOLUME_DOWN,new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_VOLUME_DOWN)));
  assertTrue(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());assertTrue(a.isFinishing());c.destroy();
 }

 @Test public void volumeUpAlsoDismisses()throws Exception{
  JSONObject e=event();Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();assertTrue(a.onKeyDown(android.view.KeyEvent.KEYCODE_VOLUME_UP,new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_VOLUME_UP)));assertTrue(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());c.destroy();
 }
 @Test public void cancelledGestureDoesNotDismiss()throws Exception{
  JSONObject e=event();Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();View b=ViewLookup.button(a.findViewById(android.R.id.content),"↑  Arraste para cima ou toque para desativar");long t=android.os.SystemClock.uptimeMillis();a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t,android.view.MotionEvent.ACTION_DOWN,10,400,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+50,android.view.MotionEvent.ACTION_CANCEL,10,1,0));assertFalse(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(3,alarms.getScheduledAlarms().size());c.destroy();
 }

 @Test public void swipeFromOtherScreenAreaDismisses()throws Exception{
  JSONObject e=event().put("reservas",4);Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();long t=android.os.SystemClock.uptimeMillis();
  a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t,android.view.MotionEvent.ACTION_DOWN,350,600,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+150,android.view.MotionEvent.ACTION_MOVE,350,350,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+250,android.view.MotionEvent.ACTION_UP,350,200,0));
  assertTrue(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());c.destroy();
 }
 @Test public void cancelledAfterSwipeDoesNotDismiss()throws Exception{
  JSONObject e=event();Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();long t=android.os.SystemClock.uptimeMillis();
  a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t,android.view.MotionEvent.ACTION_DOWN,60,400,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+100,android.view.MotionEvent.ACTION_MOVE,60,100,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+150,android.view.MotionEvent.ACTION_CANCEL,60,100,0));assertFalse(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(3,alarms.getScheduledAlarms().size());c.destroy();
 }

 @Test public void upwardReleaseWithoutMoveStillDismisses()throws Exception{
  JSONObject e=event();Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();long t=android.os.SystemClock.uptimeMillis();
  a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t,android.view.MotionEvent.ACTION_DOWN,200,400,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+200,android.view.MotionEvent.ACTION_UP,200,100,0));assertTrue(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(0,alarms.getScheduledAlarms().size());c.destroy();
 }
 @Test public void predominantlyHorizontalDragDoesNotDismiss()throws Exception{
  JSONObject e=event();Alarms.put(ctx,e);Alarms.schedule(ctx,e);ActivityController<AlarmActivity> c=launch();AlarmActivity a=c.get();long t=android.os.SystemClock.uptimeMillis();
  a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t,android.view.MotionEvent.ACTION_DOWN,10,400,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+100,android.view.MotionEvent.ACTION_MOVE,300,300,0));a.dispatchTouchEvent(android.view.MotionEvent.obtain(t,t+200,android.view.MotionEvent.ACTION_UP,350,290,0));assertFalse(Alarms.find(ctx,"alarme").optBoolean("confirmado"));assertEquals(3,alarms.getScheduledAlarms().size());c.destroy();
 }

}
