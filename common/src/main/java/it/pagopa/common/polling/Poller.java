package it.pagopa.common.polling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Motore di polling comune a SEND e Interop, senza dipendenze da Spring o Awaitility.
 * <p>
 * La chiamata viene eseguita subito e poi ripetuta a ogni {@code interval} finche' la condizione di stop
 * non e' soddisfatta, oppure finche' non si esauriscono {@code timeout} o {@code maxAttempts}.
 * Un nuovo tentativo parte solo prima della scadenza del timeout: se il tentativo successivo non puo'
 * partire in tempo, il polling termina subito senza un'ultima attesa inutile.
 * <ul>
 *     <li>condizione mai soddisfatta: {@link PollingTimeoutException} con l'ultimo valore letto;</li>
 *     <li>eccezione della chiamata o della condizione, thread interrotto: {@link PollingExecutionException}.</li>
 * </ul>
 */
public final class Poller {
    private static final Logger log = LoggerFactory.getLogger(Poller.class);

    /**
     * Log INFO periodico basato sul tempo e non sul numero di tentativi: con intervalli brevi
     * (es. EXTRA_RAPID, 200ms) un heartbeat ogni N tentativi produrrebbe un log INFO ogni pochi secondi.
     */
    static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(30);
    private static final String DEFAULT_TIMEOUT_MESSAGE = "Polling esaurito senza soddisfare la condizione richiesta";

    /** Attesa tra due tentativi; sostituibile nei test per non attendere davvero. */
    @FunctionalInterface
    interface Sleeper {
        void sleep(Duration duration) throws InterruptedException;
    }

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
        return pollUntil(call, stopCondition, config, onTimeoutMessage, System::nanoTime,
                duration -> Thread.sleep(duration.toMillis()));
    }

    static <T> T pollUntil(Supplier<T> call, Predicate<T> stopCondition, PollingConfig config,
                           Function<T, String> onTimeoutMessage, LongSupplier nanoClock, Sleeper sleeper) {
        Objects.requireNonNull(call, "call");
        Objects.requireNonNull(stopCondition, "stopCondition");
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(onTimeoutMessage, "onTimeoutMessage");

        long start = nanoClock.getAsLong();
        long nextHeartbeat = HEARTBEAT_INTERVAL.toNanos();
        int attempt = 0;
        T lastValue = null;

        while (true) {
            attempt++;
            boolean satisfied;
            try {
                lastValue = call.get();
                satisfied = stopCondition.test(lastValue);
            } catch (RuntimeException e) {
                throw new PollingExecutionException("Errore al tentativo " + attempt + ": " + e.getMessage(), e);
            }

            Duration elapsed = elapsedSince(start, nanoClock);
            if (satisfied) {
                log.debug("Polling riuscito al tentativo {} (elapsed={}ms)", attempt, elapsed.toMillis());
                return lastValue;
            }

            // Il prossimo tentativo partirebbe a timeout scaduto: inutile attendere
            Duration remaining = config.timeout().minus(elapsed);
            if (attempt >= config.maxAttempts() || config.interval().compareTo(remaining) >= 0) {
                break;
            }

            if (elapsed.toNanos() >= nextHeartbeat) {
                log.info("Polling in corso: tentativo {}, elapsed={}s, timeout={}s",
                        attempt, elapsed.toSeconds(), config.timeout().toSeconds());
                nextHeartbeat = elapsed.toNanos() + HEARTBEAT_INTERVAL.toNanos();
            } else {
                log.debug("Tentativo {} non soddisfatto, attendo {}ms", attempt, config.interval().toMillis());
            }

            sleep(sleeper, config.interval());

            // Nessun tentativo parte a timeout scaduto
            if (elapsedSince(start, nanoClock).compareTo(config.timeout()) >= 0) {
                break;
            }
        }

        throw new PollingTimeoutException(onTimeoutMessage.apply(lastValue), lastValue, attempt,
                elapsedSince(start, nanoClock));
    }

    private static Duration elapsedSince(long start, LongSupplier nanoClock) {
        return Duration.ofNanos(nanoClock.getAsLong() - start);
    }

    private static void sleep(Sleeper sleeper, Duration duration) {
        try {
            sleeper.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PollingExecutionException("Polling interrotto", e);
        }
    }
}
