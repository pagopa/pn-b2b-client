package it.pagopa.pn.http.executor;

import io.cucumber.java.en.Then;
import it.pagopa.common.http.HttpCallExecutor;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpCallExecutorAssertionSteps {
    private final HttpCallExecutor executor;
    private final HttpCallExecutorLifecycleProbe lifecycleProbe;

    public HttpCallExecutorAssertionSteps(@Qualifier("sendHttpCallExecutor") HttpCallExecutor executor,
                                         HttpCallExecutorLifecycleProbe lifecycleProbe) {
        this.executor = executor;
        this.lifecycleProbe = lifecycleProbe;
    }

    @Then("un altro passo dello scenario locale {string} legge status {int} e lo stesso body")
    public void verifySharedResult(String scenarioName, int statusCode) {
        lifecycleProbe.verifySameScenarioTarget(scenarioName, executor);
        assertTrue(executor.hasResult());
        assertTrue(executor.isSuccessful());
        assertFalse(executor.isHttpError());
        assertEquals(Integer.valueOf(statusCode), executor.getStatusCode());
        assertEquals(scenarioName, executor.getResponseBody(String.class));
    }
}
