package it.pagopa.pn.cucumber.steps.utilitySteps;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class ThreadUtils {

    private static void threadWait(long wait, TimeUnit timeUnit) {
        try {
            await()
                    .atMost(wait, timeUnit)
                    .pollDelay(getWait(wait-1, timeUnit), getTimeUnit(wait-1, timeUnit))
                    .with()
                    .until(() -> false);
        } catch (ConditionTimeoutException exception) {
            log.info("Thread wait ended.");
        }
    }

    private static long getWait(long wait, TimeUnit timeUnit) {
        if (timeUnit.equals(TimeUnit.MILLISECONDS)) {
            return checkWait(wait);
        }
        return wait;
    }
    private static long checkWait(long wait) {

        return wait == 0 ? 999 : wait;
    }

    private static TimeUnit getTimeUnit(long wait, TimeUnit timeUnit) {
        if (getWait(wait, timeUnit) == 999) {
            return TimeUnit.NANOSECONDS;
        }
        return timeUnit;
    }

    public static void threadWaitMilliseconds(long wait) {
        threadWait(wait, TimeUnit.MILLISECONDS);
    }

    public static void threadWaitSeconds(long wait) {
        threadWait(wait, TimeUnit.SECONDS);
    }

    public static void threadWaitMinutes(long wait) {
        threadWait(wait, TimeUnit.MINUTES);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ThreadUtils.threadWaitMilliseconds(1);
        long end = System.currentTimeMillis();
        long duration = (end-start);
        System.out.println("End execution: " + end);
        System.out.println("total duration: " + duration + "ms");
    }
}
