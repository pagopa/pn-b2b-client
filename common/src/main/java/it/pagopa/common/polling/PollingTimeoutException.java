package it.pagopa.common.polling;

import java.time.Duration;

/**
 * Il polling ha esaurito tempo o tentativi senza che la condizione di stop fosse soddisfatta.
 */
public class PollingTimeoutException extends RuntimeException {
    private final transient Object lastValue;
    private final int attempts;
    private final Duration elapsed;

    public PollingTimeoutException(String message, Object lastValue, int attempts, Duration elapsed) {
        super(message + " (tentativi=" + attempts + ", elapsed=" + elapsed.toMillis() + "ms)");
        this.lastValue = lastValue;
        this.attempts = attempts;
        this.elapsed = elapsed;
    }

    @SuppressWarnings("unchecked")
    public <T> T getLastValue() {
        return (T) lastValue;
    }

    public int getAttempts() {
        return attempts;
    }

    public Duration getElapsed() {
        return elapsed;
    }
}
