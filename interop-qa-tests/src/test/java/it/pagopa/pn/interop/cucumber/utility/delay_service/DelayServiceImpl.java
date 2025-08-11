package it.pagopa.pn.interop.cucumber.utility.delay_service;

import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class DelayServiceImpl implements DelayService {

    private static final Duration DEFAULT_DELAY = Duration.ofMillis(2500);

    @Override
    public void delay() {
        this.delayFor(DEFAULT_DELAY);
    }

    // The result will be approximated down to the nearest nanosecond.
    @Override
    public void delayScaledBy(double factor) {
        double effectiveDelayNanos = DEFAULT_DELAY.toNanos() * factor;
        long finalDelayNanos = (long) Math.floor(effectiveDelayNanos);
        this.delayFor(Duration.ofNanos(finalDelayNanos));
    }

    @Override
    public void delayForSeconds(int seconds) {
        delayFor(Duration.ofSeconds(seconds));
    }

    @Override
    public void delayForMillis(long millis) {
        delayFor(Duration.ofMillis(millis));
    }

    @Override
    public void delayFor(Duration delay) {
        try {
            if(delay.isNegative()) {
                throw new IllegalArgumentException("Delay MUST not be negative");
            }

            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            throw new DelayException("Error occurred trying to delay workflow", e);
        }
    }
}