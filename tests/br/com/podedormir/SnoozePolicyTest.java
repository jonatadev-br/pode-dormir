package br.com.podedormir;
public final class SnoozePolicyTest {
 static void eq(long a,long b){if(a!=b)throw new AssertionError(a+" != "+b);}
 static void invalid(long now,int minutes){try{SnoozePolicy.until(now,minutes);throw new AssertionError("Aceitou duração inválida");}catch(IllegalArgumentException | ArithmeticException expected){}}
 public static void main(String[] args){eq(SnoozePolicy.until(1000L,1),61000L);eq(SnoozePolicy.until(0L,60),3600000L);invalid(0,0);invalid(0,61);invalid(Long.MAX_VALUE,1);System.out.println("5 testes de duração da soneca passaram.");}
}
