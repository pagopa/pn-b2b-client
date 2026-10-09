package it.pagopa.common.polling;

import java.time.Duration;
import java.util.Objects;

/**
 * Configurazione immutabile di un polling.
 * Il limite primario e' il tempo massimo ({@code timeout}); {@code maxAttempts} e' un limite di sicurezza aggiuntivo.
 */
public record PollingConfig(Duration timeout, Duration interval, int maxAttempts) {

    public PollingConfig {
        Objects.requireNonNull(timeout, "timeout");
        Objects.requireNonNull(interval, "interval");
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("timeout non puo' essere negativo");
        }
        if (interval.isNegative() || interval.isZero()) {
            throw new IllegalArgumentException("interval deve essere positivo");
        }
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts deve essere almeno 1");
        }
    }

    public static PollingConfig of(Duration timeout, Duration interval) {
        // Numero massimo di tentativi che possono partire all'interno della finestra temporale
        // Valida prima della divisione, mantenendo la stessa precisione dell'attesa.
        new PollingConfig(timeout, interval, 1);
        long intervals = timeout.dividedBy(interval);
        int safetyCap = (int) Math.min(intervals, Integer.MAX_VALUE - 1L) + 1;
        return new PollingConfig(timeout, interval, safetyCap);
    }
}
