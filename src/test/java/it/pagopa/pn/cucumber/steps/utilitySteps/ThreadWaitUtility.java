package it.pagopa.pn.cucumber.steps.utilitySteps;

import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.springframework.stereotype.Component;

@Component
public class ThreadWaitUtility {

    public void tryWait(SharedSteps sharedSteps) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void tryWait(SharedSteps sharedSteps, long multiplier) {
        try {
            Thread.sleep(sharedSteps.getWait() * multiplier);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}
