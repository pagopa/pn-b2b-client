package it.pagopa.interop.tracing.client.polling;

import java.util.function.Predicate;

public class AbstractPolling<T> {

    private final T context;
    private final Predicate<T> condition;

    public AbstractPolling(T context, Predicate<T> condition) {
        this.context = context;
        this.condition = condition;
    }

    // Template Method: scheletro del polling
    public final void executePolling(int maxAttempts, long intervalMillis) {
        for (int i = 0; i < maxAttempts; i++) {

            if (condition.test(context)) {
                return;
            }

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
