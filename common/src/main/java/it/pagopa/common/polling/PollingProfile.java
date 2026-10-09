package it.pagopa.common.polling;

import java.time.Duration;

/**
 * Profili di polling predefiniti per i casi d'uso piu' comuni.
 * Per tempi dipendenti dall'ambiente o dall'elemento atteso costruire un {@link PollingConfig} dedicato.
 */
public enum PollingProfile {
    EXTRA_RAPID(Duration.ofMillis(200), Duration.ofSeconds(3)),
    RAPID(Duration.ofMillis(500), Duration.ofSeconds(10)),
    SLOW(Duration.ofSeconds(15), Duration.ofMinutes(30));

    private final Duration interval;
    private final Duration timeout;

    PollingProfile(Duration interval, Duration timeout) {
        this.interval = interval;
        this.timeout = timeout;
    }

    public PollingConfig toConfig() {
        return PollingConfig.of(timeout, interval);
    }
}
