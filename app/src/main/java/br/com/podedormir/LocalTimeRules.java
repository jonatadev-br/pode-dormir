package br.com.podedormir;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/** Operações puras para conversão entre horário civil e instante. */
final class LocalTimeRules {
    private LocalTimeRules() { }
    static String localAt(long millis, String zone) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of(zone))
                .withSecond(0).withNano(0).toString();
    }
    /** Retorna null se o horário não existe por avanço do relógio. Em hora duplicada, escolhe a primeira ocorrência. */
    static Long instantFor(String local, String zone) {
        LocalDateTime date=LocalDateTime.parse(local);
        ZoneId z=ZoneId.of(zone);
        List<ZoneOffset> offsets=z.getRules().getValidOffsets(date);
        if(offsets.isEmpty()) return null;
        return ZonedDateTime.ofLocal(date,z,offsets.get(0)).toInstant().toEpochMilli();
    }
}
