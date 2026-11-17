package br.com.podedormir;
/** Rejeita disparos antigos, indevidos ou adiantados; não garante entrega. */
final class AlarmDispatchGuard {
 private AlarmDispatchGuard(){}
 static long expected(long wake,int attempt,int backups,int interval,long snooze){
  if(attempt==-1)return snooze>0?snooze:-1;
  int count=Math.max(0,backups),gap=Math.max(1,Math.min(60,interval));
  if(wake<=0||attempt<0||attempt>count)return -1;
  try{return Math.addExact(wake,Math.multiplyExact((long)attempt*gap,60000L));}catch(ArithmeticException ex){return -1;}
 }
 static boolean accept(long intended,long expected,long now){return expected>0 && intended==expected && now>=expected-1000L;}
}
