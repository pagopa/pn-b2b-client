package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.conf.InteropClientConfigs;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
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

    public <T> T makePolling(
            Supplier<T> promise,
            Predicate<T> shouldStop,
            String errorMessage,
            int maxTries,
            long sleepMillis
    ) {
        try {
            for (int i = 0; i < maxTries; i++) {

                Thread.sleep(sleepMillis);

                T response = promise.get();

                if (shouldStop.test(response)) {
                    return response;
                }
            }
        } catch (InterruptedException e) {
            log.error("Unexpected thread interruption during polling: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error during polling: " + e.getMessage());
        }

        throw new PollingPredicateException("Eventual consistency error: " + errorMessage);
    }

}
