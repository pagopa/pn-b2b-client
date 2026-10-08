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
        if (interval.isNegative()) {
            throw new IllegalArgumentException("interval non puo' essere negativo");
        }
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts deve essere almeno 1");
        }
    }

    public static PollingConfig of(Duration timeout, Duration interval) {
        // Numero massimo di tentativi che possono partire all'interno della finestra temporale
        long safetyCap = timeout.toMillis() / Math.max(interval.toMillis(), 1) + 1;
        return new PollingConfig(timeout, interval, (int) Math.min(safetyCap, Integer.MAX_VALUE));
    }
}
