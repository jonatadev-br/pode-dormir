package br.com.podedormir;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.Uri;
import android.os.*;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;

/** Reproduz som fora da atividade, mas não garante entrega em todos os aparelhos. */
public class AlarmService extends Service {
 static final String START="br.com.podedormir.INICIAR", CONFIRM="br.com.podedormir.CONFIRMAR";
 private static final String CHANNEL="alarmes_continuos_v1";
 private static final int FOREGROUND_ID=17001;
 private final LinkedHashSet<String> active=new LinkedHashSet<>();
 private Ringtone sound; private Vibrator vibrator;
 private final Handler rampHandler=new Handler(Looper.getMainLooper());
 private long rampStarted;
 private static final String ACTIVE_STORE="alertas_ativos";
 @Override public void onCreate(){
  super.onCreate();
  Set<String> stored=getSharedPreferences(ACTIVE_STORE,0).getStringSet("ids",new HashSet<>());
  for(String id:new HashSet<>(stored)){JSONObject event=Alarms.find(this,id);if(event!=null&&!event.optBoolean("confirmado",false))active.add(id);}
  saveActive();
 }
 private void saveActive(){if(!getSharedPreferences(ACTIVE_STORE,0).edit().putStringSet("ids",new HashSet<>(active)).commit())History.add(this,"Não foi possível persistir os alertas ativos.");}
 static void channel(Context c) {
  NotificationManager nm=(NotificationManager)c.getSystemService(NOTIFICATION_SERVICE);
  NotificationChannel ch=new NotificationChannel(CHANNEL,"Alarmes",NotificationManager.IMPORTANCE_HIGH);
  ch.setDescription("Avisos de despertar. Verifique o volume e as permissões do aparelho.");
  // Som contínuo é reproduzido pelo serviço; não tocar duas fontes simultaneamente.
  ch.setSound(null,null); nm.createNotificationChannel(ch);
 }
 static Notification notice(Context c,String id,String name) {
  channel(c);
  Intent open=new Intent(c,AlarmActivity.class).setData(Uri.parse("podedormir://tela/"+id)).putExtra("id",id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
  PendingIntent pi=PendingIntent.getActivity(c,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  JSONObject event=Alarms.find(c,id);String details=name+(event==null?"":" · "+UpcomingNotification.wakeText(event.optLong("hora")));
  Notification.Builder builder=new Notification.Builder(c,CHANNEL).setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
   .setContentTitle("Hora de acordar!").setContentText(details).setStyle(new Notification.BigTextStyle().bigText(details)).setVisibility(Notification.VISIBILITY_PUBLIC).setCategory(Notification.CATEGORY_ALARM)
   .setPriority(Notification.PRIORITY_MAX).setOngoing(true).setContentIntent(pi).setFullScreenIntent(pi,true);
  if(Build.VERSION.SDK_INT>=31)builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
  return builder.build();
 }
 static void fallback(Context c,String id,String name) {
  try {
   NotificationManager nm=(NotificationManager)c.getSystemService(NOTIFICATION_SERVICE);
   String fallbackChannel="alarme_emergencial_v1";
   NotificationChannel ch=new NotificationChannel(fallbackChannel,"Avisos de alarme",NotificationManager.IMPORTANCE_HIGH);
   ch.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
   ch.enableVibration(true); nm.createNotificationChannel(ch);
   Intent open=new Intent(c,AlarmActivity.class).setData(Uri.parse("podedormir://tela/"+id)).putExtra("id",id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
   PendingIntent pi=PendingIntent.getActivity(c,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
   JSONObject event=Alarms.find(c,id);String info=name+(event==null?"":" · "+UpcomingNotification.wakeText(event.optLong("hora")))+" — abra para desativar.";
   Notification notification=new Notification.Builder(c,fallbackChannel).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Hora de acordar!").setContentText(info).setStyle(new Notification.BigTextStyle().bigText(info)).setVisibility(Notification.VISIBILITY_PUBLIC).setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX).setContentIntent(pi).setFullScreenIntent(pi,true).setAutoCancel(true).build();
   nm.notify(FOREGROUND_ID,notification);
  } catch(RuntimeException ignored) { }
 }
 @Override public int onStartCommand(Intent intent,int flags,int startId) {
  String id=intent==null?null:intent.getStringExtra("id");
  if(intent!=null && CONFIRM.equals(intent.getAction())){
   active.remove(id);saveActive();
  } else if(intent!=null && START.equals(intent.getAction())){
   JSONObject e=Alarms.find(this,id);
   if(e!=null&&!e.optBoolean("confirmado",false)){active.add(id);saveActive();}
  } else if(intent==null&&!active.isEmpty()) {
   History.add(this,"O Android reiniciou o serviço; tentando retomar alertas não confirmados.");
  }
  if(active.isEmpty()){stopSelf();return START_NOT_STICKY;}
  // Várias ocorrências podem estar ativas; o alerta visível mostra uma delas.
  String visible=id!=null&&active.contains(id)?id:active.iterator().next();
  JSONObject visibleEvent=Alarms.find(this,visible);
  if(visibleEvent==null){active.remove(visible);saveActive();stopSelf();return START_NOT_STICKY;}
  try {startForeground(FOREGROUND_ID,notice(this,visible,visibleEvent.optString("nome")));}
  catch(RuntimeException ex){History.add(this,"Não foi possível iniciar o serviço de alarme em primeiro plano.");fallback(this,visible,visibleEvent.optString("nome"));stopSelf();return START_NOT_STICKY;}
  if(sound==null){
   String selected=getSharedPreferences("configuracoes",0).getString("som","");
   Uri standard=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
   if(standard==null)standard=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
   boolean started=playSound(selected.isEmpty()?standard:Uri.parse(selected));
   if(!started && !selected.isEmpty()){
    History.add(this,"Não foi possível iniciar o som escolhido; tentativa com o som padrão do aparelho.");
    started=playSound(standard);
   }
   if(!started)History.add(this,"Não foi possível iniciar a reprodução do som do alarme. Verifique o aparelho.");
  }
  if(vibrator==null && getSharedPreferences("configuracoes",0).getBoolean("vibrar",true))try { vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);if(vibrator!=null)vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,700,500},0)); }catch(RuntimeException ignored){}
  return START_STICKY;
 }
 private boolean playSound(Uri uri){
  if(uri==null)return false;
  try {
   Ringtone candidate=RingtoneManager.getRingtone(this,uri);if(candidate==null)return false;
   candidate.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
   candidate.setLooping(true);
   SharedPreferences prefs=getSharedPreferences("configuracoes",0);
   int target=prefs.getInt("volume_app",100);boolean gradual=prefs.getBoolean("volume_gradual",false);
   try{candidate.setVolume(VolumeCurve.level(target,gradual,0));}catch(RuntimeException unsupported){History.add(this,"O aparelho não aceitou o ajuste de volume do aplicativo.");}
   candidate.play();sound=candidate;
   if(gradual){
    rampStarted=SystemClock.elapsedRealtime();
    rampHandler.postDelayed(new Runnable(){@Override public void run(){
     if(sound!=candidate)return;long elapsed=SystemClock.elapsedRealtime()-rampStarted;
     try{candidate.setVolume(VolumeCurve.level(target,true,elapsed));}catch(RuntimeException ex){History.add(AlarmService.this,"O aumento gradual de volume não está disponível neste aparelho.");return;}
     if(elapsed<60000)rampHandler.postDelayed(this,1000);
    }},1000);
   }
   return true;
  }catch(RuntimeException ex){return false;}
 }
 @Override public void onDestroy(){ rampHandler.removeCallbacksAndMessages(null);if(sound!=null)sound.stop();if(vibrator!=null)vibrator.cancel();super.onDestroy(); }
 @Override public IBinder onBind(Intent intent){return null;}
}
