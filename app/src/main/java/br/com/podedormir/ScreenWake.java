package br.com.podedormir;

import android.content.Context;
import android.os.PowerManager;

/** Tentativa curta, ao disparar; não é prova de que a tela ligou. */
final class ScreenWake {
 private ScreenWake(){}
 @SuppressWarnings("deprecation")
 static void attempt(Context c){
  try{
   PowerManager pm=(PowerManager)c.getSystemService(Context.POWER_SERVICE);
   if(pm==null||pm.isInteractive())return;
   PowerManager.WakeLock lock=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP,"PodeDormir:acenderAoAlarme");
   lock.setReferenceCounted(false);lock.acquire(10000L);
  }catch(RuntimeException ex){History.add(c,"O sistema não aceitou a tentativa de acender a tela do alarme.");}
 }
}
