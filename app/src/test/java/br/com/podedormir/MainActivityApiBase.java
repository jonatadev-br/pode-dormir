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

abstract class MainActivityApiBase {
 Context ctx; MainActivity a; ShadowAlarmManager alarm; ActivityController<MainActivity> controller;
 @Before public void setup(){ctx=RuntimeEnvironment.getApplication();ctx.getSharedPreferences("tutorial_inicial",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();ctx.getSharedPreferences("primeiro_acesso",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();ctx.getSharedPreferences("eventos",0).edit().clear().commit();ctx.getSharedPreferences("historico",0).edit().clear().commit();ctx.getSharedPreferences("configuracoes",0).edit().clear().commit();ShadowAlarmManager.setCanScheduleExactAlarms(true);controller=Robolectric.buildActivity(MainActivity.class).setup();a=controller.get();alarm=shadowOf((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE));}
 @After public void cleanup(){AlertDialog dialog=ShadowAlertDialog.getLatestAlertDialog();if(dialog!=null&&dialog.isShowing())dialog.dismiss();if(controller!=null)controller.pause().stop().destroy();}
 void create(String name){ViewLookup.button(a.root,"Cadastrar compromisso").performClick();ViewLookup.field(a.root,0).setText(name);ViewLookup.button(a.root,"Salvar compromisso").performClick();}
}
