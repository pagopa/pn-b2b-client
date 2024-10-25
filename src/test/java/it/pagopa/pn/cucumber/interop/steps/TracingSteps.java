package it.pagopa.pn.cucumber.interop.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.*;
import it.pagopa.pn.client.b2b.pa.interop.IInteropTracingClient;
import it.pagopa.pn.client.b2b.pa.interop.polling.dto.PnPollingInterop;
import it.pagopa.pn.client.b2b.pa.interop.polling.dto.PnTracingResponse;
import it.pagopa.pn.client.b2b.pa.interop.service.impl.PnPollingInteropTracing;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.cucumber.interop.domain.TracingCsvFile;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TracingSteps {
    private final PnPollingFactory pnPollingFactory;
    private final IInteropTracingClient interopTracingClient;
    private TracingCsvFile tracingCsvFile;
    private SubmitTracingResponse submitTracingResponse;
    private GetTracingsResponse getTracingsResponse;
    private GetTracingErrorsResponse getTracingErrorsResponse;
    private RecoverTracingResponse recoverTracingResponse;
    private ReplaceTracingResponse replaceTracingResponse;
    private ResourceLoader resourceLoader;
    private Resource resource;
    private HttpStatusCodeException httpStatusCodeException;

    public TracingSteps(PnPollingFactory pnPollingFactory, IInteropTracingClient interopTracingClient, ResourceLoader resourceLoader) {
        this.pnPollingFactory = pnPollingFactory;
        this.interopTracingClient = interopTracingClient;
        this.resourceLoader = resourceLoader;
    }

    @Given("l'ente {string} prepara il file CSV {string}")
    public void createCsv(String operator, String file) {
//        selectOperator(operator);
        resource = resourceLoader.getResource(selectCsvFile(file));
    }

    @When("viene sottomesso il file CSV in data {string}")
    public void uploadCsv(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            submitTracingResponse = interopTracingClient.submitTracing(resource, LocalDate.parse(date, formatter));
        } catch (HttpStatusCodeException statusCodeException) {
            httpStatusCodeException = statusCodeException;
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while submitting the tracing csv: " + ex);
        }
    }

    @When("viene recuperata la lista di tracing con uno stato tra i seguenti$")
    public void retrieveTracingByStatusList(List<String> statusList) {
        List<TracingState> tracingStates = statusList.stream().map(TracingState::fromValue).toList();
        retrieveTracing(tracingStates);
    }

    @When("viene recuperata la lista di tracing con stato {string}")
    public void retrieveTracingByStatus(String status) {
        retrieveTracing(List.of(TracingState.fromValue(status)));
    }

    public void retrieveTracing(List<TracingState> statusList) {
        try {
            getTracingsResponse = interopTracingClient.getTracings(0, 30, statusList);
        } catch (HttpStatusCodeException statusCodeException) {
            httpStatusCodeException = statusCodeException;
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while retrieving the tracings: " + ex);
        }
    }

    @Then("la risposta contiene soltanto i tracing con stato {string}")
    public void verififyGetTracingResponse(String status) {
        Assertions.assertNotNull(getTracingsResponse, "There was an error while retrieving the getTracing response!");
        Assertions.assertFalse(getTracingsResponse.getResults().stream().anyMatch(x -> !Objects.equals(x.getState(), status)));
    }

    @Then("non viene trovato nessun tracing caricato")
    public void verifyGetTracingResponse() {
        Assertions.assertNotNull(getTracingsResponse, "There was an error while retrieving the getTracing response!");
        Assertions.assertTrue(getTracingsResponse.getResults().isEmpty());
    }

    @Then("^il caricamento del csv viene effettuato con errori")
    public void verifyUploadWithErrors() {
        verifyUpload(true);
    }

    @Then("^il caricamento del csv viene effettuato senza errori")
    public void verifyUploadWithoutErrors() {
        verifyUpload(false);
    }

    private void verifyUpload(boolean hasErrors) {
        Assertions.assertNotNull(submitTracingResponse, "There was an error while retrieving the tracing response!");
        Assertions.assertNotNull(submitTracingResponse.getErrors());
        Assertions.assertEquals(hasErrors, submitTracingResponse.getErrors());
    }

    @Then("la chiamata fallisce con status code: {int}")
    public void checkStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, httpStatusCodeException.getStatusCode().value());
    }

    @When("viene recuperato il dettaglio del tracing con errori")
    public void retrieveTracingError() {
        getTracingErrors(submitTracingResponse.getTracingId());
    }

    @Then("viene recuperato il dettaglio degli errori per il tracing {string}")
    public void retrieveSpecificTracingError(String tracingId) {
        getTracingErrors(UUID.fromString(tracingId));
    }

    private void getTracingErrors(UUID tracingId) {
        try {
            getTracingErrorsResponse = interopTracingClient.getTracingErrors(tracingId, 0, 30);
        } catch (HttpStatusCodeException statusCodeException) {
            httpStatusCodeException = statusCodeException;
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while retrieving the tracing error: " + ex);
        }
    }

    @Then("il dettaglio ritorna gli errori aspettati")
    public void verifyGetTracingErrorResponse() {
        Assertions.assertNotNull(getTracingErrorsResponse, "There was an error while retrieving the tracing error!");
        Assertions.assertNotNull(getTracingErrorsResponse.getResults());
        List<GetTracingErrorsResponseResults> expectedResult = List.of(
                createExpectedResponse("errorCode", "message", "purposeId", 10),
                createExpectedResponse("errorCode", "message", "purposeId", 10)
        );
        Assertions.assertEquals(expectedResult, getTracingErrorsResponse.getResults());
    }

    @When("gli errori riscontrati vengono corretti passando il csv {string}")
    public void sanitizeErrors(String file) {
        Assertions.assertNotNull(submitTracingResponse, "There was an error while retrieving the tracing response!");
        Assertions.assertNotNull(submitTracingResponse.getTracingId());
        recoverError(submitTracingResponse.getTracingId().toString(), resourceLoader.getResource(selectCsvFile(file)));
    }

    @When("vengono corretti gli errori riscontrati per il tracingId {string}")
    public void sanitizeErrorsForSpecificTracingId(String tracingId) {
        recoverError(tracingId, resourceLoader.getResource(selectCsvFile("corretto")));
    }

    private void recoverError(String tracingId, Resource resource) {
        try {
            recoverTracingResponse = interopTracingClient.recoverTracing(tracingId, resource);
        } catch (HttpStatusCodeException statusCodeException) {
            httpStatusCodeException = statusCodeException;
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while recovering the tracing: " + ex);
        }
    }

    @And("si verifica che il tracing sia presente tra quelli ritornati")
    public void checkReturnedTracingId() {
        Assertions.assertTrue(getTracingsResponse.getResults()
                .stream()
                .map(GetTracingsResponseResults::getTracingId)
                .anyMatch(tracingId -> tracingId.equals(submitTracingResponse.getTracingId().toString())));
    }

    @Given("viene sovrascritto il tracing aggiunto in precedenza con il csv: {string}")
    public void replaceTracing(String file) {
        replaceTracing(submitTracingResponse.getTracingId().toString(), resourceLoader.getResource(selectCsvFile(file)));
    }


    private void replaceTracing(String tracingId, Resource resource) {
        try {
            replaceTracingResponse = interopTracingClient.replaceTracing(tracingId, resource);
        } catch (HttpStatusCodeException statusCodeException) {
            httpStatusCodeException = statusCodeException;
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while replacing the tracing data: " + ex);
        }
    }

    @Then("viene verificato che la sovrascrittura viene effettuata con errori")
    public void verifyReplaceWithErrors() {
        verifyReplaceTracingResponse(true);
    }

    @Then("viene verificato che la sovrascrittura viene effettuata senza errori")
    public void verifyReplaceWithoutErrors() {
        verifyReplaceTracingResponse(false);
    }

    private void verifyReplaceTracingResponse(boolean hasErrors) {
        Assertions.assertNotNull(replaceTracingResponse, "There was an error while retrieving the replace tracing response!");
        Assertions.assertEquals(hasErrors, replaceTracingResponse.getErrors());
    }

    @When("viene sovrascritto il tracing con id: {string}")
    public void replaceTracingById(String tracingId) {
        replaceTracing(tracingId, resourceLoader.getResource("corretto"));
    }

    @When("viene invocato l'endpoint di health con successo")
    public void getHealthStatus() {
        Assertions.assertDoesNotThrow(interopTracingClient::getHealthStatus);
    }

    @When("viene inviato il csv per la data mancante")
    public void recoverMissingCsvForDate() {
        Assertions.assertNotNull(getTracingsResponse, "There was an error while retrieving the tracing with MISSING status!");
        Assertions.assertFalse(getTracingsResponse.getResults().isEmpty(), "No tracing with MISSING status found!");
        GetTracingsResponseResults tracingsResponseResults = getTracingsResponse.getResults().get(0);
        createCsv("", "CORRETTO");
        uploadCsv(tracingsResponseResults.getDate().toString());
    }

    @And("si attende che il file di tracing caricato passi in stato {string}")
    public void waitForStatus(String status) {
        PnPollingInteropTracing interopTracing = (PnPollingInteropTracing) pnPollingFactory.getPollingService(PnPollingStrategy.INTEROP_TRACING);
        PnTracingResponse pnTracingResponse = interopTracing.waitForEvent(null,
                PnPollingParameter.builder()
                        .pnPollingInterop(new PnPollingInterop(submitTracingResponse.getTracingId().toString(), TracingState.fromValue(status)))
                        .build());
        Assertions.assertTrue(pnTracingResponse.getResult());
    }

    private void selectOperator(String operator) {
        switch (operator.trim().toLowerCase()) {
            case "operator1" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            case "operator2" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            default -> throw new IllegalStateException("Unexpected value: " + operator.trim().toLowerCase());
        }
    }

    private String selectCsvFile(String file) {
        return switch (file.trim().toLowerCase()) {
            case "corretto" -> "classpath:interop/2024_10_22_OK.csv";
            case "errato" -> "classpath:interop/2024_10_22_ERROR.csv";
            default -> throw new IllegalStateException("Unexpected value: " + file.trim().toLowerCase());
        };
    }

    private GetTracingErrorsResponseResults createExpectedResponse(String errorCode, String message, String purposeId, Integer rowNumber) {
        GetTracingErrorsResponseResults tracingErrorsResponse = new GetTracingErrorsResponseResults();
        tracingErrorsResponse.setErrorCode(errorCode);
        tracingErrorsResponse.setMessage(message);
        tracingErrorsResponse.setPurposeId(purposeId);
        tracingErrorsResponse.setRowNumber(rowNumber);
        return tracingErrorsResponse;
    }
}
