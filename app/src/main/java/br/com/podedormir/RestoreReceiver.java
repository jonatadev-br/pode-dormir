package br.com.podedormir;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Aceita apenas os eventos de sistema necessários ao reagendamento. */
public class RestoreReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){
  String action=i==null?null:i.getAction();
  if(Intent.ACTION_BOOT_COMPLETED.equals(action)||Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)||
     Intent.ACTION_TIME_CHANGED.equals(action)||Intent.ACTION_TIMEZONE_CHANGED.equals(action)||
     AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action)) Alarms.restore(c);
 }
}
