package br.com.podedormir;
public final class ReadinessTest {
 static int cases;
 static void check(Readiness.Level actual,Readiness.Level expected){cases++;if(actual!=expected)throw new AssertionError("Esperado "+expected+", recebido "+actual);}
 public static void main(String[] args){
  check(Readiness.evaluate(true,true,true,false,4,false,true,true,80,true,true),Readiness.Level.READY);
  check(Readiness.evaluate(true,true,true,false,4,false,true,true,80,true,false),Readiness.Level.ATTENTION);
  check(Readiness.evaluate(false,true,true,false,4,false,true,true,80,true,true),Readiness.Level.CRITICAL);
  check(Readiness.evaluate(true,false,true,false,4,false,true,true,80,true,true),Readiness.Level.CRITICAL);
  check(Readiness.evaluate(true,true,false,false,4,false,true,true,80,true,true),Readiness.Level.CRITICAL);
  check(Readiness.evaluate(true,true,true,true,4,false,true,true,80,true,true),Readiness.Level.CRITICAL);
  check(Readiness.evaluate(true,true,true,false,0,false,true,true,80,true,true),Readiness.Level.CRITICAL);
  check(Readiness.evaluate(true,true,true,false,4,true,true,true,80,true,true),Readiness.Level.CRITICAL);
  check(Readiness.evaluate(true,true,true,false,4,false,false,true,80,true,true),Readiness.Level.ATTENTION);
  check(Readiness.evaluate(true,true,true,false,4,false,true,false,80,true,true),Readiness.Level.ATTENTION);
  check(Readiness.evaluate(true,true,true,false,4,false,true,true,20,true,true),Readiness.Level.ATTENTION);
  check(Readiness.evaluate(true,true,true,false,4,false,true,true,-1,true,true),Readiness.Level.ATTENTION);
  check(Readiness.evaluate(true,true,true,false,4,false,true,true,80,false,true),Readiness.Level.ATTENTION);
  System.out.println(cases+" testes de classificação de segurança passaram.");
 }
}
