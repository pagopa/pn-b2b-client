package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.polling;

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
            System.out.println("Tentativo " + (i + 1));

            if (condition.test(context)) {
                System.out.println("Condizione soddisfatta, stop polling.");
                return;
            }

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        System.out.println("Condizione NON soddisfatta dopo " + maxAttempts + " tentativi.");
    }
}

