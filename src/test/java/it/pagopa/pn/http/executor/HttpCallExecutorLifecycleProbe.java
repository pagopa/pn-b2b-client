package it.pagopa.pn.http.executor;

import it.pagopa.common.http.HttpCallExecutor;
import org.springframework.test.util.AopTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/** Records bean targets only; Cucumber owns the scenario scope and its cleanup. */
public class HttpCallExecutorLifecycleProbe {
    private final Map<String, HttpCallExecutor> scenarioTargets = new HashMap<>();
    private final CyclicBarrier parallelScenarios = new CyclicBarrier(2);

    public synchronized void registerScenario(String scenarioName, HttpCallExecutor executor) {
        assertFalse(scenarioTargets.containsKey(scenarioName), "Scenario locale duplicato: " + scenarioName);
        HttpCallExecutor target = AopTestUtils.getTargetObject(executor);
        scenarioTargets.forEach((previousName, previousTarget) ->
                assertNotSame(previousTarget, target,
                        "Executor condiviso fra gli scenari " + previousName + " e " + scenarioName));
        scenarioTargets.put(scenarioName, target);
    }

    public synchronized void verifySameScenarioTarget(String scenarioName, HttpCallExecutor executor) {
        assertSame(scenarioTargets.get(scenarioName), AopTestUtils.getTargetObject(executor),
                "I due consumer non condividono l'executor dello scenario " + scenarioName);
    }

    public void awaitParallelScenarios() {
        try {
            parallelScenarios.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Test del lifecycle parallelo interrotto", exception);
        } catch (BrokenBarrierException | TimeoutException exception) {
            throw new AssertionError("I due scenari locali non hanno raggiunto insieme la barriera", exception);
        }
    }
}
