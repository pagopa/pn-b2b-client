package it.pagopa.pn.interop.cucumber.utility;

import java.time.Instant;
import java.util.function.BooleanSupplier;

public class PollingUtils {

    /**
     * Polls until the given condition is true or timeout is reached.
     * @param condition The condition to check (as lambda)
     * @param timeoutMs Max time to wait in milliseconds
     * @param pollIntervalMs Polling interval in milliseconds
     * @return true if the condition became true within the timeout, false otherwise
     */
    public static boolean pollUntil(BooleanSupplier condition, long timeoutMs, long pollIntervalMs) {
        Instant deadline = Instant.now().plusMillis(timeoutMs);
        while (Instant.now().isBefore(deadline)) {
            if (condition.getAsBoolean()) {
                return true;
            }
            try {
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            }
        }
        return false;
    }
}
