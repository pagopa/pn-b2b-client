package it.pagopa.pn.http.executor;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.common.http.HttpCallExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpCallExecutorProducerSteps {
    private final HttpCallExecutor executor;
    private final HttpCallExecutorLifecycleProbe lifecycleProbe;

    public HttpCallExecutorProducerSteps(@Qualifier("sendHttpCallExecutor") HttpCallExecutor executor,
                                        HttpCallExecutorLifecycleProbe lifecycleProbe) {
        this.executor = executor;
        this.lifecycleProbe = lifecycleProbe;
    }

    @Given("lo scenario locale {string} non ha ancora effettuato chiamate")
    public void verifyInitialState(String scenarioName) {
        assertFalse(executor.hasResult(), "Risultato residuo nello scenario " + scenarioName);
        assertNull(executor.getStatusCode());
        assertNull(executor.getResponse());
        lifecycleProbe.registerScenario(scenarioName, executor);
    }

    @When("lo scenario locale {string} riceve una risposta con status {int}")
    public void receiveLocalResponse(String scenarioName, int statusCode) {
        executor.callForEntity(() -> ResponseEntity.status(statusCode).body(scenarioName));
    }

    @And("i due scenari locali sono attivi contemporaneamente")
    public void awaitBothScenarioStarts() {
        lifecycleProbe.awaitParallelScenarios();
    }

    @And("i due scenari locali hanno completato la chiamata")
    public void awaitBothResponses() {
        lifecycleProbe.awaitParallelScenarios();
    }
}
