package br.com.podedormir;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import org.junit.After;
import org.junit.Before;
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
public class TutorialGuideTest {
 Context c;MainActivity a;ActivityController<MainActivity> controller;String manufacturer;
 @Before public void setup(){c=RuntimeEnvironment.getApplication();c.getSharedPreferences("primeiro_acesso",0).edit().clear().commit();c.getSharedPreferences("tutorial_inicial",0).edit().clear().commit();c.getSharedPreferences("eventos",0).edit().clear().commit();ShadowAlarmManager.setCanScheduleExactAlarms(true);manufacturer=Build.MANUFACTURER;ReflectionHelpers.setStaticField(Build.class,"MANUFACTURER","Outro");controller=Robolectric.buildActivity(MainActivity.class).setup();a=controller.get();}
 @After public void cleanup(){AlertDialog d=ShadowAlertDialog.getLatestAlertDialog();if(d!=null&&d.isShowing())d.dismiss();if(controller!=null)controller.pause().stop().destroy();ReflectionHelpers.setStaticField(Build.class,"MANUFACTURER",manufacturer);}
 AlertDialog current(){AlertDialog d=ShadowAlertDialog.getLatestAlertDialog();assertNotNull(d);assertTrue(d.isShowing());return d;}
 void press(int which){current().getButton(which).performClick();shadowOf(Looper.getMainLooper()).idle();}
 String title(){return shadowOf(current()).getTitle().toString();}
 @Test public void tutorialStartsAfterPermissionGuideAndNavigatesWithoutCreatingEvents(){
  assertEquals("Vamos preparar os alertas?",title());press(AlertDialog.BUTTON_POSITIVE);assertEquals("Notificações na tela bloqueada",title());press(AlertDialog.BUTTON_NEGATIVE);assertEquals("Confira antes de dormir",title());assertFalse(c.getSharedPreferences("tutorial_inicial",0).getBoolean("visto",false));press(AlertDialog.BUTTON_POSITIVE);
  assertEquals("Conheça o Pode Dormir!",title());press(AlertDialog.BUTTON_POSITIVE);assertTrue(title().startsWith("1 de 4"));press(AlertDialog.BUTTON_POSITIVE);assertTrue(title().startsWith("2 de 4"));press(AlertDialog.BUTTON_NEGATIVE);assertTrue(title().startsWith("1 de 4"));press(AlertDialog.BUTTON_POSITIVE);press(AlertDialog.BUTTON_POSITIVE);assertTrue(title().startsWith("3 de 4"));assertTrue(shadowOf(current()).getMessage().toString().contains("qualquer lugar"));press(AlertDialog.BUTTON_POSITIVE);assertTrue(title().startsWith("4 de 4"));press(AlertDialog.BUTTON_POSITIVE);
  assertTrue(c.getSharedPreferences("tutorial_inicial",0).getBoolean("visto",false));assertEquals(0,Alarms.load(c).length());
 }
 @Test public void skippingGuidedSetupOffersTutorialAndCanSkipOrReplayIt(){
  press(AlertDialog.BUTTON_NEGATIVE);assertEquals("Conheça o Pode Dormir!",title());press(AlertDialog.BUTTON_NEGATIVE);assertTrue(c.getSharedPreferences("tutorial_inicial",0).getBoolean("visto",false));
  a.more();ViewLookup.button(a.root,"Ver tutorial").performClick();assertEquals("Conheça o Pode Dormir!",title());press(AlertDialog.BUTTON_POSITIVE);assertTrue(title().startsWith("1 de 4"));press(AlertDialog.BUTTON_NEUTRAL);assertTrue(c.getSharedPreferences("tutorial_inicial",0).getBoolean("visto",false));
 }
 @Test public void previouslyStartedTutorialResumesAtSavedStep(){
  press(AlertDialog.BUTTON_NEGATIVE);press(AlertDialog.BUTTON_POSITIVE);press(AlertDialog.BUTTON_POSITIVE);assertTrue(title().startsWith("2 de 4"));assertEquals(2,c.getSharedPreferences("tutorial_inicial",0).getInt("etapa",0));
  controller.pause().stop().destroy();controller=Robolectric.buildActivity(MainActivity.class).setup();a=controller.get();assertTrue(title().startsWith("2 de 4"));
 }
}
