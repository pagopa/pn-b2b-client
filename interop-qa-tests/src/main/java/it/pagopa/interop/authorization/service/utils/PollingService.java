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

    public <T> void makePolling(Supplier<T> promise, Predicate<T> shouldStop, String errorMessage) {
        try {
            for (int i = 0; i < interopClientConfigs.getMaxPollingTry(); i++) {
                Thread.sleep(interopClientConfigs.getMaxPollingSleep());

                // Execute the provided function and obtain the result
                T response = promise.get();

                boolean shouldStopPolling = shouldStop.test(response);
                if (shouldStopPolling) {
                    return;
                }
            }
        } catch (InterruptedException e) {
            log.error("Unexpected thread interruption  during polling: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error during shouldStop polling logic evaluation: " + e.getMessage());
        }

        throw new IllegalArgumentException("Eventual consistency error: " + errorMessage);
    }
}
