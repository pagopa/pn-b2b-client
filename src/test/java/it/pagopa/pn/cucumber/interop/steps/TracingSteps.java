package it.pagopa.pn.cucumber.interop.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.SubmitTracingResponse;
import it.pagopa.pn.client.b2b.pa.service.interop.IInteropTracingClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.cucumber.interop.domain.TracingCsvFile;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.core.io.ResourceLoader;

import java.time.LocalDate;

public class TracingSteps {
    private final IInteropTracingClient interopTracingClient;
    private TracingCsvFile tracingCsvFile;
    private SubmitTracingResponse submitTracingResponse;
    private ResourceLoader resourceLoader;

    public TracingSteps(IInteropTracingClient interopTracingClient, ResourceLoader resourceLoader) {
        this.interopTracingClient = interopTracingClient;
        this.resourceLoader = resourceLoader;
    }

    @Given("l'ente {string} prepara un nuovo file CSV")
    public void createCsv(String operator) {
        selectOperator(operator);
        //TODO create csv at runtime or fixed csv?
//        tracingCsv =

    }

    @When("viene sottomesso il file CSV")
    public void uploadCsv() {
        try {
            submitTracingResponse = interopTracingClient.submitTracing(resourceLoader.getResource(""), LocalDate.now());
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while submitting the tracing csv: " + ex);
        }
    }

    @Then("il caricamento del csv viene effettuato con successo")
    public void verifyUploadSuccessfully() {
        Assertions.assertNotNull(submitTracingResponse, "There was an error while retrieving the tracing response!");
        Assertions.assertFalse(submitTracingResponse.getErrors());
    }

    private void selectOperator(String operator) {
        switch (operator.trim().toLowerCase()) {
            case "operator1" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            case "operator2" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            default -> throw new IllegalStateException("Unexpected value: " + operator.trim().toLowerCase());
        }
    }
}
