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

/** Fluxos de interface em Android simulado, sem aparelho nem som real. */
@RunWith(RobolectricTestRunner.class) @Config(sdk=29)
public class MainActivityTest {
 Context ctx; MainActivity a; ShadowAlarmManager alarm; ActivityController<MainActivity> controller;
 @Before public void setup(){ctx=RuntimeEnvironment.getApplication();ctx.getSharedPreferences("tutorial_inicial",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();ctx.getSharedPreferences("primeiro_acesso",0).edit().putBoolean("visto",true).putInt("etapa",0).commit();ctx.getSharedPreferences("eventos",0).edit().clear().commit();ctx.getSharedPreferences("historico",0).edit().clear().commit();ctx.getSharedPreferences("configuracoes",0).edit().clear().commit();ShadowAlarmManager.setCanScheduleExactAlarms(true);controller=Robolectric.buildActivity(MainActivity.class).setup();a=controller.get();alarm=shadowOf((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE));}
 @After public void cleanup(){AlertDialog dialog=ShadowAlertDialog.getLatestAlertDialog();if(dialog!=null&&dialog.isShowing())dialog.dismiss();if(controller!=null)controller.pause().stop().destroy();}
 void option(String name,int index){ViewLookup.button(a.root,"Opções: "+name).performClick();AlertDialog menu=ShadowAlertDialog.getLatestAlertDialog();menu.getListView().performItemClick(menu.getListView().getChildAt(index),index,menu.getListView().getAdapter().getItemId(index));}
 void create(String name){ViewLookup.button(a.root,"Cadastrar compromisso").performClick();ViewLookup.field(a.root,0).setText(name);ViewLookup.button(a.root,"Salvar compromisso").performClick();}
 @Test public void createEditDuplicateAndDelete()throws Exception{
  create("Consulta");assertEquals(1,Alarms.load(ctx).length());assertEquals(3,alarm.getScheduledAlarms().size());String id=Alarms.load(ctx).getJSONObject(0).getString("id");
  a.list();option("Consulta",0);ViewLookup.field(a.root,0).setText("Consulta alterada");ViewLookup.button(a.root,"Salvar compromisso").performClick();assertEquals("Consulta alterada",Alarms.find(ctx,id).getString("nome"));assertEquals(3,alarm.getScheduledAlarms().size());
  a.list();option("Consulta alterada",1);ViewLookup.field(a.root,0).setText("Segunda consulta");ViewLookup.button(a.root,"Salvar compromisso").performClick();assertEquals(2,Alarms.load(ctx).length());assertEquals(6,alarm.getScheduledAlarms().size());
  a.list();option("Consulta alterada",2);AlertDialog dialog=ShadowAlertDialog.getLatestAlertDialog();assertNotNull(dialog);dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(Looper.getMainLooper()).idle();assertNull(Alarms.find(ctx,id));assertEquals(1,Alarms.load(ctx).length());assertEquals(3,alarm.getScheduledAlarms().size());
 }
 @Test public void recurrenceCreatesAndDeletesSeries()throws Exception{
  ViewLookup.button(a.root,"Cadastrar compromisso").performClick();ViewLookup.field(a.root,0).setText("Exame");ViewLookup.button(a.root,"Mostrar mais opções do compromisso").performClick();ViewLookup.button(a.root,"Escolher repetição").performClick();AlertDialog picker=ShadowAlertDialog.getLatestAlertDialog();picker.getListView().performItemClick(picker.getListView().getChildAt(1),1,picker.getListView().getAdapter().getItemId(1));ViewLookup.button(a.root,"Salvar compromisso").performClick();assertEquals(2,Alarms.load(ctx).length());assertEquals(6,alarm.getScheduledAlarms().size());
  a.list();option("Exame",3);ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();shadowOf(Looper.getMainLooper()).idle();assertEquals(0,Alarms.load(ctx).length());assertEquals(0,alarm.getScheduledAlarms().size());
 }
 @Test public void invalidNameDoesNotCreateEvent(){ViewLookup.button(a.root,"Cadastrar compromisso").performClick();ViewLookup.button(a.root,"Salvar compromisso").performClick();assertEquals(0,Alarms.load(ctx).length());assertEquals(0,alarm.getScheduledAlarms().size());}
 @Test public void selectingLightThemePersistsAndUpdatesColors(){
  a.settings();ViewLookup.button(a.root,"Escolher tema").performClick();AlertDialog dialog=ShadowAlertDialog.getLatestAlertDialog();dialog.getListView().performItemClick(dialog.getListView().getChildAt(1),1,dialog.getListView().getAdapter().getItemId(1));shadowOf(Looper.getMainLooper()).idle();
  assertEquals("claro",ctx.getSharedPreferences("configuracoes",0).getString("tema",""));assertFalse(a.darkTheme());assertEquals(ThemePalette.background(false),((android.graphics.drawable.ColorDrawable)a.root.getBackground()).getColor());
 }




 @Test public void homeShowsExperimentalWarningAndSafetyNavigation(){
  ViewLookup.textMatching(a.root,"(?s).*Não use este aplicativo como único despertador.*");ViewLookup.textMatching(a.root,"PRÓXIMO COMPROMISSO");ViewLookup.textMatching(a.root,"VERIFICAÇÃO DE SEGURANÇA");
  ViewLookup.button(a.root,"Meus alarmes");ViewLookup.button(a.root,"Verificação de segurança");ViewLookup.button(a.root,"Mais opções").performClick();ViewLookup.button(a.root,"Preparar para dormir");ViewLookup.button(a.root,"Configurações");ViewLookup.button(a.root,"Histórico");
 }
 @Test public void formLabelsReferToEditableFields(){
  ViewLookup.button(a.root,"Cadastrar compromisso").performClick();android.widget.EditText name=ViewLookup.field(a.root,0);android.widget.TextView label=ViewLookup.textMatching(a.root,"Nome do compromisso");assertEquals(name.getId(),label.getLabelFor());assertTrue(name.getMinimumHeight()>0);
 }
 @Test public void bedtimeDoesNotClaimGlobalSuccess(){
  a.bedtime();ViewLookup.textMatching(a.root,"(?s).*Nenhum compromisso futuro.*");ViewLookup.textMatching(a.root,"(?s).*verificação é parcial.*");ViewLookup.button(a.root,"Verificação de segurança");
 }

 @Test public void homeUsesTwoCardsAndKeepsWarningVisible(){
  int cards=0;for(android.widget.LinearLayout item:ViewLookup.all(a.root,android.widget.LinearLayout.class))if(item.getBackground() instanceof android.graphics.drawable.GradientDrawable)cards++;
  assertEquals(2,cards);ViewLookup.textMatching(a.root,"(?s).*Não use este aplicativo como único despertador.*");
 }



 @Test public void acceptsMoreThanTwoBackupsInForm()throws Exception{
  ViewLookup.button(a.root,"Cadastrar compromisso").performClick();ViewLookup.field(a.root,0).setText("Consulta");
  ViewLookup.button(a.root,"Mostrar mais opções do compromisso").performClick();android.widget.TextView label=ViewLookup.textMatching(a.root,"Número de alarmes de reserva");android.widget.EditText field=a.findViewById(label.getLabelFor());field.setText("7");
  ViewLookup.button(a.root,"Salvar compromisso").performClick();assertEquals(7,Alarms.load(ctx).getJSONObject(0).getInt("reservas"));assertEquals(8,alarm.getScheduledAlarms().size());
 }

 @Test public void optionalDetailsStartHiddenAndValuesSurviveClosing()throws Exception{
  ViewLookup.button(a.root,"Cadastrar compromisso").performClick();android.widget.Button toggle=ViewLookup.button(a.root,"Mostrar mais opções do compromisso");android.widget.TextView label=ViewLookup.textMatching(a.root,"Número de alarmes de reserva");assertFalse(label.isShown());toggle.performClick();assertTrue(label.isShown());android.widget.EditText field=a.findViewById(label.getLabelFor());field.setText("7");ViewLookup.textMatching(a.root,"Reservas: 7 · intervalo: 5 min");ViewLookup.button(a.root,"Ocultar mais opções do compromisso").performClick();assertFalse(label.isShown());ViewLookup.field(a.root,0).setText("Consulta");ViewLookup.button(a.root,"Salvar compromisso").performClick();assertEquals(7,Alarms.load(ctx).getJSONObject(0).getInt("reservas"));
 }
 @Test public void settingsDetailsAreInitiallyHiddenAndAccessible(){
  a.settings();android.widget.TextView label=ViewLookup.textMatching(a.root,"Quantidade padrão de reservas");assertFalse(label.isShown());ViewLookup.button(a.root,"Mostrar ajustes de perfil e alarme").performClick();assertTrue(label.isShown());ViewLookup.button(a.root,"Ocultar ajustes de perfil e alarme").performClick();assertFalse(label.isShown());ViewLookup.button(a.root,"Salvar configurações");
 }

}
