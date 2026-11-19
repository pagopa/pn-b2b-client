package it.pagopa.pn.cucumber.utils;

import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.PollingTimeoutException;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Negative checks on asynchronous effects, built on {@link Poller}: the assertions must hold on every read for the
 * whole window.
 */
public final class ObservationWindow {

    private ObservationWindow() {
    }

    /**
     * Reads {@code call} every {@code interval} for {@code window} and fails with the original {@link AssertionError}
     * as soon as {@code verify} fails on a read.
     *
     * @return the last value read, on which {@code verify} holds
     */
    public static <T> T assertHoldsFor(Duration window, Duration interval, Supplier<T> call, Consumer<T> verify) {
        Predicate<T> violated = value -> {
            try {
                verify.accept(value);
                return false;
            } catch (AssertionError e) {
                return true;
            }
        };
        T violation;
        try {
            violation = Poller.pollUntil(call, violated, PollingConfig.of(window, interval));
        } catch (PollingTimeoutException expected) {
            @SuppressWarnings("unchecked")
            T lastValue = (T) expected.getLastValue();
            return lastValue;
        }
        verify.accept(violation);
        throw new IllegalStateException("Assertion failed during the polling but passed on the same value: " + violation);
    }
}
