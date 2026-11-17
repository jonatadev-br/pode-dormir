package br.com.podedormir;

/** Classificação conservadora: uma condição desconhecida nunca é sucesso. */
final class Readiness {
    enum Level { CRITICAL, ATTENTION, READY }
    private Readiness() {}
    static Level evaluate(boolean futureEvent, boolean exact, boolean notifications,
                          boolean channelBlocked, int alarmVolume, boolean invalidEvent,
                          boolean fullScreen, boolean batteryExempt, int batteryPercent,
                          boolean dndOff, boolean everyScheduleVerified) {
        if(!futureEvent || !exact || !notifications || channelBlocked || alarmVolume<=0 || invalidEvent)
            return Level.CRITICAL;
        if(!fullScreen || !batteryExempt || batteryPercent<0 || batteryPercent<=20 || !dndOff || !everyScheduleVerified)
            return Level.ATTENTION;
        return Level.READY;
    }
}
