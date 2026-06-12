package it.pagopa.pn.interop.cucumber.config.concurrency;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ScenariosConcurrencyHooks {
    private final ScenariosConcurrencyManager concurrencyManager;
    private final ScenariosConcurrencyAuditor concurrencyAuditor;
    private List<String> acquiredLocks;

    @Before(order = Integer.MIN_VALUE)
    public void eventuallyLock(Scenario scenario) {
        this.acquiredLocks = concurrencyManager.acquireLocksFor(scenario);
        this.concurrencyAuditor.recordStart(scenario.getName());
    }

    @After(order = Integer.MAX_VALUE)
    public void eventuallyUnlock(Scenario scenario) {
        try {
            concurrencyAuditor.recordEnd(scenario.getName());
        } finally {
            // Il rilascio DEVE essere in un finally per garantire
            // che i semafori tornino liberi anche se l'auditor fallisce
            concurrencyManager.releaseLocks(this.acquiredLocks);
        }
    }
}
