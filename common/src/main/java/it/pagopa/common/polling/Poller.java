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
 * Motore di polling comune a SEND e Interop basato su Awaitility.
 * <p>
 * La chiamata viene eseguita subito, sul thread chiamante, e poi ripetuta a ogni {@code interval}
 * finche' la condizione di stop non e' soddisfatta, oppure finche' non si esauriscono
 * {@code timeout} o {@code maxAttempts}. Nessun nuovo tentativo parte a timeout scaduto;
 * se il prossimo intervallo non rientra nel tempo residuo, il polling termina senza attendere.
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
    private static final String DEFAULT_TIMEOUT_MESSAGE = "Polling esaurito senza soddisfare la condizione richiesta";

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

        class PollingAttempt {
            final long start = System.nanoTime();
            Duration nextHeartbeat = HEARTBEAT_INTERVAL;
            int attempts;
            T lastValue;

            Duration elapsed() {
                return Duration.ofNanos(System.nanoTime() - start);
            }

            PollingTimeoutException timeoutException() {
                return new PollingTimeoutException(onTimeoutMessage.apply(lastValue), lastValue, attempts, elapsed());
            }

            boolean evaluate() {
                checkInterrupted();
                if (attempts > 0 && elapsed().compareTo(config.timeout()) >= 0) {
                    throw timeoutException();
                }
                attempts++;
                boolean satisfied;
                try {
                    lastValue = call.get();
                    satisfied = stopCondition.test(lastValue);
                } catch (RuntimeException e) {
                    throw new PollingExecutionException("Errore al tentativo " + attempts + ": " + e.getMessage(), e);
                }
                checkInterrupted();

                Duration elapsed = elapsed();
                if (satisfied) {
                    log.debug("Polling riuscito al tentativo {} (elapsed={}ms)", attempts, elapsed.toMillis());
                    return true;
                }
                if (attempts >= config.maxAttempts()
                        || config.interval().compareTo(config.timeout().minus(elapsed)) >= 0) {
                    throw timeoutException();
                }
                if (elapsed.compareTo(nextHeartbeat) >= 0) {
                    log.info("Polling in corso: tentativo {}, elapsed={}s, timeout={}s",
                            attempts, elapsed.toSeconds(), config.timeout().toSeconds());
                    nextHeartbeat = elapsed.plus(HEARTBEAT_INTERVAL);
                } else {
                    log.debug("Tentativo {} non soddisfatto, attendo {}ms", attempts, config.interval().toMillis());
                }
                return false;
            }
        }

        PollingAttempt attempt = new PollingAttempt();
        // Awaitility richiede un timeout positivo; zero mantiene una singola chiamata immediata.
        if (config.timeout().isZero()) {
            attempt.evaluate();
        } else {
            try {
                Awaitility.await()
                        .pollInSameThread()
                        .ignoreNoExceptions()
                        .pollDelay(Duration.ZERO)
                        .pollInterval(awaitilityDuration(config.interval()))
                        .atMost(awaitilityDuration(config.timeout()))
                        .until(attempt::evaluate);
            } catch (ConditionTimeoutException e) {
                checkInterrupted();
                throw attempt.timeoutException();
            }
        }
        return attempt.lastValue;
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
