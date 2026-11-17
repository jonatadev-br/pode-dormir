package br.com.podedormir;
import java.time.Instant;
public final class RecurrenceRuleTest {
 static int count;
 static void eq(String a,String b){count++;if(!b.equals(a))throw new AssertionError("Esperado "+b+", obtido "+a);}
 static long t(String iso){return Instant.parse(iso).toEpochMilli();}
 public static void main(String[] args){
  eq(RecurrenceRule.next("2026-11-15T06:00",1,t("2026-11-15T09:00:00Z"),"America/Sao_Paulo"),"2026-11-16T06:00");
  eq(RecurrenceRule.next("2026-11-15T06:00",7,t("2026-11-15T08:00:00Z"),"America/Sao_Paulo"),"2026-11-22T06:00");
  eq(RecurrenceRule.next("2026-11-15T06:00",1,t("2026-11-19T10:00:00Z"),"America/Sao_Paulo"),"2026-11-20T06:00");
  eq(RecurrenceRule.next("2024-02-28T06:00",1,t("2024-02-28T09:00:00Z"),"UTC"),"2024-02-29T06:00");
  eq(RecurrenceRule.next("2026-03-07T02:30",1,t("2026-03-07T07:30:00Z"),"America/New_York"),"2026-03-08T02:30");
  eq(RecurrenceRule.next("2026-11-01T01:30",1,t("2026-11-01T05:30:00Z"),"America/New_York"),"2026-11-02T01:30");
  System.out.println(count+" testes de cálculo de recorrência passaram.");
 }
}
