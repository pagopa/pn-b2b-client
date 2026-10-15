package it.pagopa.common.polling;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Motore di polling comune a SEND e Interop, basato su Awaitility.
 * <p>
 * La chiamata viene eseguita subito, sul thread chiamante, e poi ripetuta a ogni {@code interval}
 * finche' la condizione di stop non e' soddisfatta, oppure finche' non si esauriscono
 * {@code timeout} o {@code maxAttempts}.
 * <ul>
 *     <li>condizione mai soddisfatta: {@link PollingTimeoutException} con l'ultimo valore letto;</li>
 *     <li>eccezione della chiamata o della condizione, thread interrotto: {@link PollingExecutionException}.</li>
 * </ul>
 * Le chiamate mantengono i propri timeout di I/O; l'interruzione viene verificata a ogni tentativo.
 */
public final class Poller {
    private static final Logger log = LoggerFactory.getLogger(Poller.class);
    static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(30);
    // Awaitility converte le durate in nanosecondi per gestire le attese.
    private static final Duration MAX_AWAITILITY_DURATION = Duration.ofNanos(Long.MAX_VALUE);
    private static final String DEFAULT_TIMEOUT_MESSAGE = "Polling exhausted without satisfying the stop condition";

    private Poller() {
    }

    public static <T> T pollUntil(Supplier<T> call, Predicate<T> stopCondition, PollingProfile profile) {
        return pollUntil(call, stopCondition, profile.toConfig());
    }

    public static <T> T pollUntil(Supplier<T> call, Predicate<T> stopCondition, PollingConfig config) {
        return pollUntil(call, stopCondition, config, lastValue -> DEFAULT_TIMEOUT_MESSAGE);
    }

    public static <T> T pollUntil(Supplier<T> call, Predicate<T> stopCondition, PollingConfig config,
                                 Function<T, String> onTimeoutMessage) {
        Objects.requireNonNull(call, "call");
        Objects.requireNonNull(stopCondition, "stopCondition");
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(onTimeoutMessage, "onTimeoutMessage");

        long start = System.nanoTime();
        AtomicInteger attempts = new AtomicInteger();
        AtomicReference<T> lastValue = new AtomicReference<>();

        Callable<Boolean> attempt = () -> {
            checkInterrupted();
            attempts.incrementAndGet();
            T value;
            boolean satisfied;
            try {
                value = call.get();
                satisfied = stopCondition.test(value);
            } catch (RuntimeException e) {
                throw new PollingExecutionException("Error on attempt: " + attempts.get() + ": " + e.getMessage(), e);
            }
            lastValue.set(value);
            checkInterrupted();
            if (satisfied) {
                return true;
            }
            // pollInSameThread() non ha un thread di sorveglianza su atMost: senza questo controllo,
            // Awaitility dormirebbe l'intero pollInterval anche quando supera il timeout residuo,
            // facendo scadere il polling in ritardo (fino a un intero interval in piu').
            Duration elapsed = Duration.ofNanos(System.nanoTime() - start);
            boolean maxAttemptsReached = attempts.get() >= config.maxAttempts();
            boolean nextIntervalExceedsRemainingTime = config.interval().compareTo(config.timeout().minus(elapsed)) >= 0;
            if (maxAttemptsReached || nextIntervalExceedsRemainingTime) {
                throw timeoutException(start, attempts.get(), value, onTimeoutMessage);
            }
            return false;
        };

        try {
            Awaitility.await()
                    .pollInSameThread()
                    .pollDelay(Duration.ZERO)
                    .pollInterval(awaitilityDuration(config.interval()))
                    .atMost(awaitilityDuration(config.timeout()))
                    .until(attempt);
        } catch (ConditionTimeoutException e) {
            checkInterrupted();
            throw timeoutException(start, attempts.get(), lastValue.get(), onTimeoutMessage);
        }
        return lastValue.get();
    }

    private static <T> PollingTimeoutException timeoutException(long start, int attempts, T lastValue,
                                                                  Function<T, String> onTimeoutMessage) {
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);
        log.warn("Polling exhausted after {} attempts (elapsed={}ms), last value read: {}",
                attempts, elapsed.toMillis(), lastValue);
        return new PollingTimeoutException(onTimeoutMessage.apply(lastValue), lastValue, attempts, elapsed);
    }

    private static Duration awaitilityDuration(Duration duration) {
        return duration.compareTo(MAX_AWAITILITY_DURATION) > 0 ? MAX_AWAITILITY_DURATION : duration;
    }

    private static void checkInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            throw new PollingExecutionException("Polling stopped", new InterruptedException());
        }
    }
}
