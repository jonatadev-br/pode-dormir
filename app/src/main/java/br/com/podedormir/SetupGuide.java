package br.com.podedormir;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import java.util.Locale;

/** Guia opcional: não afirma que os ajustes do fabricante foram concedidos. */
final class SetupGuide {
 static final int NOTIFICATIONS_REQUEST=106, SETTINGS_REQUEST=107;
 private static final String STORE="primeiro_acesso";
 private final MainActivity activity;
 private AlertDialog dialog;
 SetupGuide(MainActivity activity){this.activity=activity;}
 private SharedPreferences prefs(){return activity.getSharedPreferences(STORE,0);}
 private int step(){return prefs().getInt("etapa",0);}
 private void step(int value){prefs().edit().putInt("etapa",value).commit();}
 private void show(AlertDialog.Builder builder){
  AlertDialog next=builder.setCancelable(false).create();dialog=next;
  next.setOnDismissListener(d->{if(dialog==next)dialog=null;});next.show();
 }
 boolean onResume(){
  if(dialog!=null&&dialog.isShowing())return true;
  if(!prefs().getBoolean("visto",false)){
   show(new AlertDialog.Builder(activity).setTitle("Vamos preparar os alertas?")
    .setMessage("Vamos conferir as permissões e abrir os ajustes necessários. Algumas opções precisam ser ativadas por você. Os alarmes ainda precisam ser testados no aparelho.")
    .setNegativeButton("Agora não",(d,w)->{prefs().edit().putBoolean("visto",true).commit();activity.maybeStartTutorial();}).setPositiveButton("Começar",(d,w)->start()));return true;
  }else if(step()>0){next();return true;}
  return false;
 }
 void start(){prefs().edit().putBoolean("visto",true).putInt("etapa",1).commit();dialog=null;next();}
 void permissionResult(){UpcomingNotification.refresh(activity);if(step()>0){dialog=null;next();}}
 private void skipTo(int next){step(next);dialog=null;next();}
 private void settings(Intent intent,int next){
  step(next);dialog=null;
  try{activity.startActivityForResult(intent,SETTINGS_REQUEST);}
  catch(RuntimeException e){activity.toast("Não foi possível abrir este ajuste. Confira nas configurações do aparelho.");next();}
 }
 private boolean isXiaomi(){String maker=Build.MANUFACTURER==null?"":Build.MANUFACTURER.toLowerCase(Locale.ROOT);return maker.contains("xiaomi")||maker.contains("redmi")||maker.contains("poco");}
 void next(){
  if(dialog!=null&&dialog.isShowing())return;
  int current=step();NotificationManager nm=(NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
  if(current==1){
   if(Build.VERSION.SDK_INT<33||activity.checkSelfPermission("android.permission.POST_NOTIFICATIONS")==PackageManager.PERMISSION_GRANTED){skipTo(2);return;}
   show(new AlertDialog.Builder(activity).setTitle("Permitir notificações")
    .setMessage("Precisamos delas para mostrar o alarme e o próximo compromisso. O Android perguntará se você permite.")
    .setNegativeButton("Agora não",(d,w)->skipTo(2)).setPositiveButton("Continuar",(d,w)->{
     step(2);dialog=null;try{activity.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},NOTIFICATIONS_REQUEST);}catch(RuntimeException e){activity.toast("Não foi possível solicitar notificações. Abra os ajustes do aparelho.");next();}
    }));return;
  }
  if(current==2){
   if(Build.VERSION.SDK_INT<31||Alarms.allowed(activity)){skipTo(3);return;}
   show(new AlertDialog.Builder(activity).setTitle("Permitir alarmes e lembretes")
    .setMessage("Para solicitar o despertar no horário escolhido, permita alarmes exatos nos ajustes do Android.")
    .setNegativeButton("Agora não",(d,w)->skipTo(3)).setPositiveButton("Abrir ajustes",(d,w)->settings(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+activity.getPackageName())),3)));return;
  }
  if(current==3){
   if(Build.VERSION.SDK_INT<34||nm.canUseFullScreenIntent()){skipTo(4);return;}
   show(new AlertDialog.Builder(activity).setTitle("Permitir tela cheia")
    .setMessage("Essa opção ajuda a mostrar os controles quando o alarme tocar, inclusive com a tela bloqueada. O Android ainda pode restringir a exibição.")
    .setNegativeButton("Agora não",(d,w)->skipTo(4)).setPositiveButton("Abrir ajustes",(d,w)->settings(new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,Uri.parse("package:"+activity.getPackageName())),4)));return;
  }
  if(current==4){
   try{AlarmService.channel(activity);UpcomingNotification.ensureChannel(activity);}catch(RuntimeException ignored){}
   show(new AlertDialog.Builder(activity).setTitle("Notificações na tela bloqueada")
    .setMessage("Nos ajustes, ative as notificações do Pode Dormir! e dos canais Alarmes e Próximo compromisso. Se existirem, ative também a tela de bloqueio e as notificações flutuantes. O nome e o horário podem ficar visíveis para outras pessoas.")
    .setNegativeButton("Agora não",(d,w)->skipTo(5)).setPositiveButton("Abrir ajustes",(d,w)->settings(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,activity.getPackageName()),5)));return;
  }
  if(current==5){
   if(!isXiaomi()){skipTo(6);return;}
   show(new AlertDialog.Builder(activity).setTitle("Mais ajustes da Xiaomi")
    .setMessage("Se o seu aparelho tiver Outras permissões, procure Mostrar na tela de bloqueio e Abrir janelas em segundo plano. Os nomes e a disponibilidade variam entre aparelhos.")
    .setNegativeButton("Agora não",(d,w)->skipTo(6)).setPositiveButton("Abrir informações do app",(d,w)->settings(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+activity.getPackageName())),6)));return;
  }
  if(current==6){
   step(0);UpcomingNotification.refresh(activity);activity.home();
   show(new AlertDialog.Builder(activity).setTitle("Confira antes de dormir")
    .setMessage("Confira a verificação de segurança e teste o alarme no aparelho. Estas etapas não garantem que a tela ou o som funcionarão. Use outro despertador de apoio.")
    .setPositiveButton("Entendi",(d,w)->activity.maybeStartTutorial()));
  }
 }
}
