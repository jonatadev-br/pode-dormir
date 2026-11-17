package br.com.podedormir;
import java.time.Instant;

/** Testes de JVM sem dependência de Android; executar conforme LEIA-ME. */
public final class LocalTimeRulesTest {
    private static void eq(Object actual,Object expected) {
        if(expected==null ? actual!=null : !expected.equals(actual))
            throw new AssertionError("Esperado " + expected + "; obtido " + actual);
    }
    public static void main(String[] args) {
        eq(LocalTimeRules.localAt(Instant.parse("2026-11-15T09:00:00Z").toEpochMilli(),"America/Sao_Paulo"),"2026-11-15T06:00");
        eq(LocalTimeRules.instantFor("2026-11-15T06:00","America/Sao_Paulo"),Instant.parse("2026-11-15T09:00:00Z").toEpochMilli());
        eq(LocalTimeRules.instantFor("2026-11-15T06:00","America/Manaus"),Instant.parse("2026-11-15T10:00:00Z").toEpochMilli());
        eq(LocalTimeRules.instantFor("2026-03-08T02:30","America/New_York"),null);
        eq(LocalTimeRules.instantFor("2026-11-01T01:30","America/New_York"),Instant.parse("2026-11-01T05:30:00Z").toEpochMilli());
        eq(LocalTimeRules.instantFor("2026-01-01T00:00","UTC"),Instant.parse("2026-01-01T00:00:00Z").toEpochMilli());
        System.out.println("6 testes de conversão de horário passaram.");
    }
}
