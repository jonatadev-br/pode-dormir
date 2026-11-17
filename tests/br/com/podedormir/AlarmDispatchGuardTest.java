package br.com.podedormir;
public final class AlarmDispatchGuardTest {
 static int cases;static void ok(boolean b){cases++;if(!b)throw new AssertionError("Falha no teste "+cases);}
 public static void main(String[] args){
  long t=10000000L;
  ok(AlarmDispatchGuard.expected(t,0,2,5,0)==t);ok(AlarmDispatchGuard.expected(t,1,2,5,0)==t+300000);ok(AlarmDispatchGuard.expected(t,2,2,5,0)==t+600000);
  ok(AlarmDispatchGuard.expected(t,2,1,5,0)==-1);ok(AlarmDispatchGuard.expected(t,7,9,5,0)==t+2100000);ok(AlarmDispatchGuard.expected(t,-2,2,5,0)==-1);ok(AlarmDispatchGuard.expected(t,4,2,5,0)==-1);
  ok(AlarmDispatchGuard.expected(t,-1,2,5,t+120000)==t+120000);ok(AlarmDispatchGuard.expected(t,-1,2,5,0)==-1);ok(AlarmDispatchGuard.expected(Long.MAX_VALUE,2,2,60,0)==-1);
  ok(AlarmDispatchGuard.accept(t,t,t));ok(AlarmDispatchGuard.accept(t,t,t-1000));ok(!AlarmDispatchGuard.accept(t,t,t-1001));ok(!AlarmDispatchGuard.accept(t-300000,t,t));ok(!AlarmDispatchGuard.accept(-1,t,t));ok(AlarmDispatchGuard.accept(t,t,t+3600000));
  System.out.println(cases+" verificações de disparo passaram.");
 }
}
