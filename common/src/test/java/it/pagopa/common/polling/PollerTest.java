package it.pagopa.common.polling;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class PollerTest {

    /** Orologio finto: avanza solo quando il Poller "dorme" o la chiamata simula latenza. */
    private static final class FakeTime {
        long nanos;
        final List<Duration> sleeps = new ArrayList<>();

        long now() {
            return nanos;
        }

        void sleep(Duration duration) {
            sleeps.add(duration);
            nanos += duration.toNanos();
        }

        void advance(Duration duration) {
            nanos += duration.toNanos();
        }
    }

    private final FakeTime time = new FakeTime();

    @AfterEach
    void clearInterrupt() {
        Thread.interrupted();
    }

    private <T> T poll(Supplier<T> call, java.util.function.Predicate<T> stop, PollingConfig config) {
        return Poller.pollUntil(call, stop, config, v -> "condizione non soddisfatta", time::now, time::sleep);
    }

    private static Supplier<String> sequence(String... values) {
        Iterator<String> it = List.of(values).iterator();
        return () -> it.hasNext() ? it.next() : values[values.length - 1];
    }

    @Test
    void returnsImmediatelyWhenFirstAttemptSatisfiesCondition() {
        String result = poll(() -> "OK", "OK"::equals, PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(1)));

        assertEquals("OK", result);
        assertTrue(time.sleeps.isEmpty());
    }

    @Test
    void retriesUntilConditionIsSatisfied() {
        String result = poll(sequence("CREATING", "CREATING", "OK"), "OK"::equals,
                PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(1)));

        assertEquals("OK", result);
        assertEquals(List.of(Duration.ofSeconds(1), Duration.ofSeconds(1)), time.sleeps);
    }

    @Test
    void throwsTimeoutWithLastValueWhenTimeIsExhausted() {
        PollingConfig config = PollingConfig.of(Duration.ofSeconds(3), Duration.ofSeconds(1));

        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class,
                () -> poll(sequence("A", "B", "C", "D", "E"), "OK"::equals, config));

        // Tentativi a 0s, 1s, 2s: il successivo partirebbe a 3s, a timeout scaduto, quindi si termina a 2s
        assertEquals("C", ex.getLastValue());
        assertEquals(3, ex.getAttempts());
        assertEquals(Duration.ofSeconds(2), ex.getElapsed());
        assertTrue(ex.getMessage().startsWith("condizione non soddisfatta"));
    }

    @Test
    void doesNotCallAgainWhenNextAttemptWouldStartAfterTimeout() {
        int[] calls = {0};
        Supplier<String> waitThenOk = sequence("WAIT", "OK");
        PollingConfig config = PollingConfig.of(Duration.ofSeconds(1), Duration.ofSeconds(5));

        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class, () -> poll(() -> {
            calls[0]++;
            return waitThenOk.get();
        }, "OK"::equals, config));

        assertEquals(1, calls[0]);
        assertEquals("WAIT", ex.getLastValue());
        assertTrue(time.sleeps.isEmpty());
    }

    @Test
    void stopsWithoutUselessSleepWhenNextAttemptCannotStartInTime() {
        PollingConfig config = PollingConfig.of(Duration.ofMillis(2500), Duration.ofSeconds(1));

        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class,
                () -> poll(() -> "X", "OK"::equals, config));

        assertEquals(List.of(Duration.ofSeconds(1), Duration.ofSeconds(1)), time.sleeps);
        assertEquals(Duration.ofSeconds(2), ex.getElapsed());
        assertEquals(3, ex.getAttempts());
    }

    @Test
    void timeoutAccountsForCallLatency() {
        PollingConfig config = PollingConfig.of(Duration.ofSeconds(3), Duration.ofSeconds(1));

        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class, () -> poll(() -> {
            time.advance(Duration.ofSeconds(1));
            return "X";
        }, "OK"::equals, config));

        assertEquals(2, ex.getAttempts());
    }

    @Test
    void stopsAtMaxAttemptsEvenIfTimeRemains() {
        PollingConfig config = new PollingConfig(Duration.ofMinutes(1), Duration.ofSeconds(1), 3);

        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class,
                () -> poll(() -> "X", "OK"::equals, config));

        assertEquals(3, ex.getAttempts());
    }

    @Test
    void zeroTimeoutStillPerformsOneAttempt() {
        PollingConfig config = PollingConfig.of(Duration.ZERO, Duration.ofSeconds(1));

        assertEquals("OK", poll(() -> "OK", "OK"::equals, config));
        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class,
                () -> poll(() -> "X", "OK"::equals, config));
        assertEquals(1, ex.getAttempts());
    }

    @Test
    void wrapsCallExceptionWithoutRetrying() {
        IllegalStateException cause = new IllegalStateException("404 Not Found");
        int[] calls = {0};

        PollingExecutionException ex = assertThrows(PollingExecutionException.class, () -> poll(() -> {
            calls[0]++;
            throw cause;
        }, v -> true, PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(1))));

        assertSame(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("404 Not Found"));
        assertEquals(1, calls[0]);
    }

    @Test
    void wrapsPredicateException() {
        assertThrows(PollingExecutionException.class, () -> poll(() -> (String) null, String::isEmpty,
                PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(1))));
    }

    @Test
    void interruptionRestoresFlagAndThrows() {
        PollingExecutionException ex = assertThrows(PollingExecutionException.class,
                () -> Poller.pollUntil(() -> "X", "OK"::equals,
                        PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(1)),
                        v -> "msg", time::now, d -> {
                            throw new InterruptedException();
                        }));

        assertInstanceOf(InterruptedException.class, ex.getCause());
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void realSleepPathWorksWithProfile() {
        assertEquals("OK", Poller.pollUntil(sequence("X", "OK"), "OK"::equals, PollingProfile.EXTRA_RAPID));
    }

    @Test
    void subMillisecondIntervalDoesNotExhaustAttemptsPrematurely() {
        PollingConfig config = PollingConfig.of(Duration.ofMillis(2), Duration.ofNanos(500_000));

        assertEquals(5, config.maxAttempts());
        assertEquals("OK", poll(sequence("WAIT", "WAIT", "WAIT", "OK"), "OK"::equals, config));
        assertEquals(Duration.ofNanos(1_500_000), Duration.ofNanos(time.nanos));
        assertEquals(3, time.sleeps.size());
    }

    @Test
    void configOfComputesSafetyCap() {
        assertEquals(11, PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(1)).maxAttempts());
        assertEquals(3, PollingConfig.of(Duration.ofMillis(2500), Duration.ofSeconds(1)).maxAttempts());
        assertEquals(1, PollingConfig.of(Duration.ZERO, Duration.ofSeconds(1)).maxAttempts());
        assertEquals(121, PollingProfile.SLOW.toConfig().maxAttempts());
        assertEquals(4, PollingConfig.of(Duration.ofMillis(5), Duration.ofNanos(1_500_000)).maxAttempts());
    }

    @Test
    void configRejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> PollingConfig.of(Duration.ofSeconds(1), Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new PollingConfig(Duration.ofSeconds(1), Duration.ZERO, 1));
        assertThrows(IllegalArgumentException.class, () -> PollingConfig.of(Duration.ofSeconds(1), Duration.ofNanos(-1)));
        assertThrows(IllegalArgumentException.class, () -> PollingConfig.of(Duration.ofSeconds(-1), Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> new PollingConfig(Duration.ofSeconds(1), Duration.ofSeconds(1), 0));
        assertThrows(NullPointerException.class, () -> PollingConfig.of(null, Duration.ofSeconds(1)));
    }
}
