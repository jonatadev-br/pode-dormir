package br.com.podedormir;

/** Regras puras de duração; não comprovam que o sistema entregará o alarme. */
final class SnoozePolicy {
    private SnoozePolicy() {}
    static long until(long now, int minutes) {
        if(minutes<1 || minutes>60)throw new IllegalArgumentException("Duração inválida");
        return Math.addExact(now,Math.multiplyExact((long)minutes,60000L));
    }
}
