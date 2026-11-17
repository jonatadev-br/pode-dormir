package br.com.podedormir;
import android.app.*;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.JSONObject;

public class AlarmActivity extends Activity {
 String id; java.util.function.LongSupplier wallClock=System::currentTimeMillis; private final Handler clockHandler=new Handler(Looper.getMainLooper());
 private float touchStartX,touchStartY;private boolean tracking,swiping;
 @Override public void onCreate(Bundle state) { super.onCreate(state); setShowWhenLocked(true);setTurnScreenOn(true);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);display(getIntent()); }
 @Override public void onNewIntent(Intent i){super.onNewIntent(i);setIntent(i);display(i);}
 void display(Intent i) {
  clockHandler.removeCallbacksAndMessages(null);tracking=false;swiping=false;id=i.getStringExtra("id"); JSONObject e=Alarms.find(this,id);if(e==null||e.optBoolean("confirmado",false)){finish();return;}
  ScrollView sc=new ScrollView(this);LinearLayout l=new LinearLayout(this);l.setPadding(32,64,32,32);l.setOrientation(1);l.setBackgroundColor(0xff101b38);sc.setBackgroundColor(0xff101b38);sc.setClipToPadding(true);sc.addView(l);
  TextView clock=new TextView(this);clock.setTextColor(-1);clock.setTextSize(48);l.addView(clock);
  Runnable tick=new Runnable(){@Override public void run(){clock.setText(new SimpleDateFormat("HH:mm",new Locale("pt","BR")).format(new Date(wallClock.getAsLong())));clockHandler.postDelayed(this,1000);}};tick.run();
  TextView t=new TextView(this);t.setTextColor(-1);t.setTextSize(28);t.setText("Hora de acordar!\n"+e.optString("nome"));l.addView(t);
  Button b=new Button(this);b.setText("↑  Arraste para cima ou toque para desativar");b.setAllCaps(false);b.setTextSize(21);b.setTextColor(0xff10251b);b.setBackgroundTintList(ColorStateList.valueOf(0xffa9e5bf));b.setMinHeight((int)(72*getResources().getDisplayMetrics().density));l.addView(b);
  TextView help=new TextView(this);help.setTextColor(0xffdce6ff);help.setTextSize(16);help.setPadding(0,12,0,16);help.setText("Arraste em qualquer lugar da tela. Você também pode usar os botões de volume. As reservas serão canceladas.");l.addView(help);
  b.setOnClickListener(v->dismissAlarm());
  setContentView(sc);SystemInsets.apply(sc);
  if(!e.optBoolean("teste")) {
   int minutes=getSharedPreferences("configuracoes",0).getInt("soneca",5);
   Button snooze=new Button(this);snooze.setText("Soneca: "+minutes+" min");snooze.setAllCaps(false);snooze.setTextSize(19);snooze.setTextColor(0xff152342);snooze.setBackgroundTintList(ColorStateList.valueOf(0xffdce6ff));snooze.setMinHeight((int)(60*getResources().getDisplayMetrics().density));l.addView(snooze);
   snooze.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Ativar soneca?")
    .setMessage("O som atual será encerrado. Um novo alerta será solicitado em "+minutes+" minuto(s). Os alarmes de reserva continuam ativos e podem tocar antes.")
    .setNegativeButton("Cancelar",null).setPositiveButton("Ativar soneca",(d,w)->{
      if(!Alarms.snooze(this,id,minutes)){Toast.makeText(this,"Não foi possível programar a soneca. O alerta continuará ativo.",Toast.LENGTH_LONG).show();return;}
      try {startService(new Intent(this,AlarmService.class).setAction(AlarmService.CONFIRM).putExtra("id",id));}
      catch(RuntimeException ex){Toast.makeText(this,"Soneca solicitada, mas não foi possível encerrar o alerta atual.",Toast.LENGTH_LONG).show();}
      finish();
    }).show());
  }
 }
 /** Um gesto para cima na área da atividade desativa; barras do sistema e diálogos são do Android. */
 @Override public boolean dispatchTouchEvent(MotionEvent event){
  int action=event.getActionMasked();
  if(action==MotionEvent.ACTION_DOWN){tracking=true;swiping=false;touchStartX=event.getRawX();touchStartY=event.getRawY();super.dispatchTouchEvent(event);return true;}
  if(action==MotionEvent.ACTION_POINTER_DOWN){tracking=false;if(swiping){swiping=false;return true;}return super.dispatchTouchEvent(event);}
  if(action==MotionEvent.ACTION_CANCEL){boolean consumed=swiping;tracking=false;swiping=false;return consumed||super.dispatchTouchEvent(event);}
  if(tracking&&(action==MotionEvent.ACTION_MOVE||action==MotionEvent.ACTION_UP)){
   float up=touchStartY-event.getRawY(),side=Math.abs(event.getRawX()-touchStartX);
   if(!swiping&&up>=72*getResources().getDisplayMetrics().density&&up>side){
    MotionEvent cancel=MotionEvent.obtain(event);cancel.setAction(MotionEvent.ACTION_CANCEL);super.dispatchTouchEvent(cancel);cancel.recycle();swiping=true;
   }
   if(action==MotionEvent.ACTION_UP){boolean dismiss=swiping;tracking=false;swiping=false;if(dismiss){dismissAlarm();return true;}}
   else if(swiping)return true;
  }
  return super.dispatchTouchEvent(event);
 }
 private void dismissAlarm(){
  if(isFinishing())return;
  if(!Alarms.confirm(this,id)){Toast.makeText(this,"Não foi possível registrar a confirmação. O alerta continua ativo.",Toast.LENGTH_LONG).show();return;}
  Intent service=new Intent(this,AlarmService.class).setAction(AlarmService.CONFIRM).putExtra("id",id);
  try{startService(service);finish();}catch(RuntimeException ex){Toast.makeText(this,"Não foi possível encerrar o som. Tente novamente.",Toast.LENGTH_LONG).show();}
 }
 @Override public boolean onKeyDown(int keyCode,KeyEvent event){
  if(keyCode==KeyEvent.KEYCODE_VOLUME_UP||keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){if(event.getRepeatCount()==0)dismissAlarm();return true;}
  return super.onKeyDown(keyCode,event);
 }
 @Override public boolean onKeyUp(int keyCode,KeyEvent event){
  if(keyCode==KeyEvent.KEYCODE_VOLUME_UP||keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)return true;
  return super.onKeyUp(keyCode,event);
 }
 @Override protected void onDestroy(){clockHandler.removeCallbacksAndMessages(null);super.onDestroy();}
 @Override public void onBackPressed() { }
}
