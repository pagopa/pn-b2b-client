package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.conf.InteropClientConfigs;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PollingService {
    private final InteropClientConfigs interopClientConfigs;

    public <T> T makePolling(Supplier<T> promise, Predicate<T> shouldStop, String errorMessage) {
        try {
            for (int i = 0; i < interopClientConfigs.getMaxPollingTry(); i++) {
                Thread.sleep(interopClientConfigs.getMaxPollingSleep());

                // Execute the provided function and obtain the result
                T response = promise.get();

                boolean shouldStopPolling = shouldStop.test(response);
                if (shouldStopPolling) {
                    return response;
                }
            }
        } catch (InterruptedException e) {
            log.error("Unexpected thread interruption  during polling: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error during polling: " + e.getMessage());
        }

        throw new PollingPredicateException("Eventual consistency error: " + errorMessage);
    }

    public static <T> T makePolling(Supplier<T> promise, Predicate<T> shouldStop, String errorMessage, int maxTries, long pollingFrequencyMillis) {
        try {
            for (int i = 0; i < maxTries; i++) {
                // Esegue la funzione di polling
                T response = promise.get();

                // Verifica se la condizione di stop è soddisfatta
                if (shouldStop.test(response)) {
                    return response;
                }

                // Attendi prima del prossimo tentativo (solo se non è l'ultimo)
                if (i < maxTries - 1) {
                    Thread.sleep(pollingFrequencyMillis);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted during polling", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error during polling: " + e.getMessage(), e);
        }

        throw new PollingPredicateException("Eventual consistency error: " + errorMessage);
    }

}
