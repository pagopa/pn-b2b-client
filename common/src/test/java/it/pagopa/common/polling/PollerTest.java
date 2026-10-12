package it.pagopa.common.polling;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class PollerTest {
    private static final PollingConfig SHORT_POLLING = PollingConfig.of(Duration.ofSeconds(5), Duration.ofMillis(10));

    @AfterEach
    void clearInterrupt() {
        Thread.interrupted();
    }

    private <T> T poll(Supplier<T> call, Predicate<T> stop, PollingConfig config) {
        return Poller.pollUntil(call, stop, config, v -> "condizione non soddisfatta: " + v);
    }

    private static Supplier<String> sequence(String... values) {
        Iterator<String> it = List.of(values).iterator();
        return () -> it.hasNext() ? it.next() : values[values.length - 1];
    }

    @Test
    void returnsImmediatelyWhenFirstAttemptSatisfiesCondition() {
        PollingConfig config = PollingConfig.of(Duration.ofSeconds(1), Duration.ofSeconds(5));

        assertTimeout(Duration.ofSeconds(1), () -> assertEquals("OK", poll(() -> "OK", "OK"::equals, config)));
    }

    @Test
    void retriesUntilConditionIsSatisfied() {
        Supplier<String> responses = sequence("CREATING", "CREATING", "OK");
        AtomicInteger calls = new AtomicInteger();

        String result = poll(() -> {
            calls.incrementAndGet();
            return responses.get();
        }, "OK"::equals, SHORT_POLLING);

        assertEquals("OK", result);
        assertEquals(3, calls.get());
    }

    @Test
    void callbacksRunOnCallingThread() {
        Thread caller = Thread.currentThread();
        ThreadLocal<String> scenarioState = ThreadLocal.withInitial(() -> "OK");
        AtomicInteger calls = new AtomicInteger();

        assertEquals("OK", poll(() -> {
            assertSame(caller, Thread.currentThread());
            return calls.incrementAndGet() == 1 ? "WAIT" : scenarioState.get();
        }, status -> {
            assertSame(caller, Thread.currentThread());
            return "OK".equals(status);
        }, SHORT_POLLING));
        assertEquals(2, calls.get());
        scenarioState.remove();
    }

    @Test
    void throwsTimeoutWithLastValueWhenTimeIsExhausted() {
        PollingConfig config = PollingConfig.of(Duration.ofMillis(250), Duration.ofMillis(50));
        AtomicInteger calls = new AtomicInteger();

        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class,
                () -> poll(() -> "WAIT-" + calls.incrementAndGet(), "OK"::equals, config));

        assertEquals("WAIT-" + calls.get(), ex.getLastValue());
        assertEquals(calls.get(), ex.getAttempts());
        assertTrue(ex.getAttempts() >= 1 && ex.getAttempts() <= config.maxAttempts());
        assertTrue(ex.getElapsed().compareTo(config.timeout().minus(config.interval())) >= 0);
        assertTrue(ex.getMessage().startsWith("condizione non soddisfatta: WAIT-" + calls.get()));
    }

    @Test
    void stopsWithoutSleepingWhenNextAttemptCannotStartInTime() {
        AtomicInteger calls = new AtomicInteger();
        // Il limite dei tentativi consente retry, ma l'intervallo supera il tempo disponibile.
        PollingConfig config = new PollingConfig(Duration.ofMillis(100), Duration.ofSeconds(5), 10);

        PollingTimeoutException ex = assertTimeout(Duration.ofSeconds(1),
                () -> assertThrows(PollingTimeoutException.class, () -> poll(() -> {
                    calls.incrementAndGet();
                    return "WAIT";
                }, "OK"::equals, config)));

        assertEquals(1, calls.get());
        assertEquals("WAIT", ex.getLastValue());
        assertEquals(1, ex.getAttempts());
    }

    @Test
    void timeoutAccountsForCallLatency() {
        AtomicInteger calls = new AtomicInteger();
        PollingConfig config = new PollingConfig(Duration.ofMillis(50), Duration.ofMillis(5), 10);

        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class, () -> poll(() -> {
            calls.incrementAndGet();
            try {
                Thread.sleep(75);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
            return "WAIT";
        }, "OK"::equals, config));

        assertEquals(1, calls.get());
        assertEquals(1, ex.getAttempts());
        assertTrue(ex.getElapsed().compareTo(config.timeout()) >= 0);
    }

    @Test
    void stopsAtMaxAttemptsEvenIfTimeRemains() {
        PollingConfig config = new PollingConfig(Duration.ofSeconds(5), Duration.ofMillis(10), 3);
        AtomicInteger calls = new AtomicInteger();

        PollingTimeoutException ex = assertTimeout(Duration.ofSeconds(1),
                () -> assertThrows(PollingTimeoutException.class, () -> poll(() -> {
                    calls.incrementAndGet();
                    return "WAIT";
                }, "OK"::equals, config)));

        assertEquals(3, calls.get());
        assertEquals(3, ex.getAttempts());
    }

    @Test
    void zeroTimeoutStillPerformsOneAttempt() {
        PollingConfig config = PollingConfig.of(Duration.ZERO, Duration.ofSeconds(1));

        assertEquals("OK", poll(() -> "OK", "OK"::equals, config));
        PollingTimeoutException ex = assertThrows(PollingTimeoutException.class,
                () -> poll(() -> "WAIT", "OK"::equals, config));
        assertEquals(1, ex.getAttempts());
        assertEquals("WAIT", ex.getLastValue());
    }

    @Test
    void wrapsCallExceptionWithoutRetrying() {
        IllegalStateException cause = new IllegalStateException("404 Not Found");
        AtomicInteger calls = new AtomicInteger();

        PollingExecutionException ex = assertThrows(PollingExecutionException.class, () -> poll(() -> {
            calls.incrementAndGet();
            throw cause;
        }, v -> true, SHORT_POLLING));

        assertSame(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("404 Not Found"));
        assertEquals(1, calls.get());
    }

    @Test
    void wrapsPredicateExceptionWithoutRetrying() {
        IllegalStateException cause = new IllegalStateException("invalid status");
        AtomicInteger calls = new AtomicInteger();

        PollingExecutionException ex = assertThrows(PollingExecutionException.class, () -> poll(() -> {
            calls.incrementAndGet();
            return "WAIT";
        }, status -> {
            throw cause;
        }, SHORT_POLLING));

        assertSame(cause, ex.getCause());
        assertEquals(1, calls.get());
    }

    @Test
    void interruptedThreadDoesNotStartPollingAndRetainsFlag() {
        AtomicInteger calls = new AtomicInteger();
        Thread.currentThread().interrupt();

        PollingExecutionException ex = assertThrows(PollingExecutionException.class, () -> poll(() -> {
            calls.incrementAndGet();
            return "WAIT";
        }, "OK"::equals, SHORT_POLLING));

        assertInstanceOf(InterruptedException.class, ex.getCause());
        assertTrue(Thread.currentThread().isInterrupted());
        assertEquals(0, calls.get());
    }

    @Test
    void interruptionDuringWaitStopsBeforeNextCallAndRetainsFlag() throws InterruptedException {
        CountDownLatch firstCall = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        AtomicReference<PollingExecutionException> failure = new AtomicReference<>();
        AtomicBoolean interrupted = new AtomicBoolean();
        Thread pollingThread = new Thread(() -> {
            try {
                poll(() -> {
                    calls.incrementAndGet();
                    firstCall.countDown();
                    return "WAIT";
                }, "OK"::equals, PollingConfig.of(Duration.ofSeconds(5), Duration.ofMillis(250)));
            } catch (PollingExecutionException e) {
                failure.set(e);
                interrupted.set(Thread.currentThread().isInterrupted());
            }
        });
        pollingThread.setDaemon(true);
        pollingThread.start();
        try {
            assertTrue(firstCall.await(1, TimeUnit.SECONDS));
            Awaitility.await().atMost(Duration.ofSeconds(1)).pollInterval(Duration.ofMillis(1))
                    .until(() -> pollingThread.getState() == Thread.State.TIMED_WAITING);
            pollingThread.interrupt();
            pollingThread.join(2000);

            assertFalse(pollingThread.isAlive());
            assertNotNull(failure.get());
            assertInstanceOf(InterruptedException.class, failure.get().getCause());
            assertTrue(interrupted.get());
            assertEquals(1, calls.get());
        } finally {
            pollingThread.interrupt();
        }
    }

    @Test
    void realSleepPathWorksWithProfile() {
        assertEquals("OK", Poller.pollUntil(sequence("WAIT", "OK"), "OK"::equals, PollingProfile.EXTRA_RAPID));
    }

    @Test
    void subMillisecondIntervalDoesNotExhaustAttemptsPrematurely() {
        PollingConfig config = PollingConfig.of(Duration.ofSeconds(5), Duration.ofNanos(500_000));
        Supplier<String> responses = sequence("WAIT", "WAIT", "WAIT", "OK");
        List<Long> callTimes = new ArrayList<>();

        assertEquals("OK", poll(() -> {
            callTimes.add(System.nanoTime());
            return responses.get();
        }, "OK"::equals, config));
        assertEquals(4, callTimes.size());
        for (int i = 1; i < callTimes.size(); i++) {
            assertTrue(callTimes.get(i) - callTimes.get(i - 1) >= config.interval().toNanos());
        }
    }

    @Test
    void acceptsNullValueWhenConditionIsSatisfied() {
        assertNull(poll(() -> null, value -> value == null, SHORT_POLLING));
    }

    @Test
    void hugeTimeoutDoesNotOverflowAwaitilityWait() {
        PollingConfig config = PollingConfig.of(Duration.ofSeconds(Long.MAX_VALUE), Duration.ofMillis(1));

        assertEquals("OK", poll(sequence("WAIT", "OK"), "OK"::equals, config));
    }

    @Test
    void configOfComputesSafetyCap() {
        assertEquals(11, PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(1)).maxAttempts());
        assertEquals(3, PollingConfig.of(Duration.ofMillis(2500), Duration.ofSeconds(1)).maxAttempts());
        assertEquals(1, PollingConfig.of(Duration.ZERO, Duration.ofSeconds(1)).maxAttempts());
        assertEquals(5, PollingConfig.of(Duration.ofMillis(2), Duration.ofNanos(500_000)).maxAttempts());
        assertEquals(4, PollingConfig.of(Duration.ofMillis(5), Duration.ofNanos(1_500_000)).maxAttempts());
    }

    @Test
    void configOfSaturatesOverflowingQuotient() {
        assertEquals(Integer.MAX_VALUE,
                PollingConfig.of(Duration.ofSeconds(Long.MAX_VALUE), Duration.ofMillis(1)).maxAttempts());
        assertEquals(Integer.MAX_VALUE,
                PollingConfig.of(Duration.ofNanos(Long.MAX_VALUE), Duration.ofNanos(1)).maxAttempts());
        assertEquals(Integer.MAX_VALUE,
                PollingConfig.of(Duration.ofSeconds(10), Duration.ofNanos(1)).maxAttempts());
    }

    @Test
    void profilesUseReviewedIntervalsAndTimeouts() {
        assertEquals(PollingConfig.of(Duration.ofSeconds(10), Duration.ofMillis(500)), PollingProfile.EXTRA_RAPID.toConfig());
        assertEquals(PollingConfig.of(Duration.ofSeconds(30), Duration.ofSeconds(1)), PollingProfile.RAPID.toConfig());
        assertEquals(PollingConfig.of(Duration.ofMinutes(30), Duration.ofSeconds(15)), PollingProfile.SLOW.toConfig());
    }

    @Test
    void configRejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> PollingConfig.of(Duration.ofSeconds(1), Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new PollingConfig(Duration.ofSeconds(1), Duration.ZERO, 1));
        assertThrows(IllegalArgumentException.class, () -> PollingConfig.of(Duration.ofSeconds(1), Duration.ofNanos(-1)));
        assertThrows(IllegalArgumentException.class, () -> PollingConfig.of(Duration.ofSeconds(-1), Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> new PollingConfig(Duration.ofSeconds(1), Duration.ofSeconds(1), 0));
        assertThrows(NullPointerException.class, () -> PollingConfig.of(null, Duration.ofSeconds(1)));
        assertThrows(NullPointerException.class, () -> PollingConfig.of(Duration.ofSeconds(1), null));
    }
}
