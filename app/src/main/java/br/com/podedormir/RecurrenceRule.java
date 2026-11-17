package br.com.podedormir;
import java.time.*;
import java.time.temporal.ChronoUnit;

/** Horário civil da próxima ocorrência diária ou semanal após um instante. */
final class RecurrenceRule {
 private RecurrenceRule(){}
 static String next(String original,int days,long nowMillis,String zone){
  if(days!=1&&days!=7)throw new IllegalArgumentException("Intervalo inválido");
  LocalDateTime anchor=LocalDateTime.parse(original);
  LocalDateTime localNow=LocalDateTime.ofInstant(Instant.ofEpochMilli(nowMillis),ZoneId.of(zone));
  long difference=ChronoUnit.DAYS.between(anchor.toLocalDate(),localNow.toLocalDate());
  long occurrences=Math.max(1,difference/days);
  LocalDateTime candidate=anchor.plusDays(Math.multiplyExact(occurrences,(long)days));
  for(int n=0;n<4;n++){
   Long instant=LocalTimeRules.instantFor(candidate.toString(),zone);
   if(instant==null ? candidate.isAfter(localNow) : instant>nowMillis)return candidate.toString();
   candidate=candidate.plusDays(days);
  }
  throw new IllegalStateException("Não foi possível calcular a próxima ocorrência");
 }
}
