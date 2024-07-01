package it.pagopa.pn.cucumber.steps.utilitySteps;

import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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
