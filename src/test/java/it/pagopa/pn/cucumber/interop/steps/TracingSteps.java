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
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.cucumber.interop.utility.TracingFileUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.core.io.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.util.*;

import static it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy.INTEROP_TRACING;
import static org.assertj.core.api.Assertions.assertThat;

public class TracingSteps {
    private final PnPollingFactory pnPollingFactory;
    private final IInteropTracingClient interopTracingClient;
    private final TracingFileUtils tracingFileUtils;

    private SubmitTracingResponse submitTracingResponse;
    private GetTracingsResponse getTracingsResponse;
    private GetTracingErrorsResponse getTracingErrorsResponse;
    private HttpStatusCodeException httpStatusCodeException;
    private LocalDate submissionDate;

    public TracingSteps(PnPollingFactory pnPollingFactory, IInteropTracingClient interopTracingClient, TracingFileUtils tracingFileUtils) {
        this.pnPollingFactory = pnPollingFactory;
        this.interopTracingClient = interopTracingClient;
        this.tracingFileUtils = tracingFileUtils;
    }

    @Given("viene aggiornato il file CSV con la prima data disponibile")
    public void updateCsv() {
        submissionDate = interopTracingClient.getTracings(0, 50, null).getResults().stream()
                .map(GetTracingsResponseResults::getDate)
                .min(LocalDate::compareTo)
                .get().minusDays(1);
        tracingFileUtils.updateCsv(submissionDate.toString());
    }

    @When("viene sottomesso il file CSV {string}")
    public void uploadCsv(String fileType) {
        try {
            submitTracingResponse = interopTracingClient.submitTracing(tracingFileUtils.getCsvFile(fileType), submissionDate.toString());
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
                createExpectedResponse("INVALID_STATUS_CODE", "status: Invalid HTTP status code", "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("INVALID_DATE", String.format("date: Date field (2024-10-26) in csv is different from tracing date (%s).", submissionDate.toString()), "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("PURPOSE_NOT_FOUND", "purpose_id: Invalid purpose id 0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8.", "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("INVALID_PURPOSE", "purpose_id: Invalid uuid", "", 2),
                createExpectedResponse("INVALID_DATE", String.format("date: Date field (2024-10-26) in csv is different from tracing date (%s).", submissionDate.toString()), "", 2)
        );
        assertThat(getTracingErrorsResponse.getResults()).hasSameElementsAs(expectedResult);
    }

    @When("gli errori riscontrati vengono corretti passando il csv {string}")
    public void sanitizeErrors(String file) {
        Assertions.assertNotNull(submitTracingResponse, "There was an error while retrieving the tracing response!");
        Assertions.assertNotNull(submitTracingResponse.getTracingId());
        recoverError(submitTracingResponse.getTracingId().toString(), tracingFileUtils.getCsvFile(file));
    }

    @When("vengono corretti gli errori riscontrati per il tracingId {string}")
    public void sanitizeErrorsForSpecificTracingId(String tracingId) {
        recoverError(tracingId, tracingFileUtils.getCsvFile("corretto"));
    }

    private void recoverError(String tracingId, Resource resource) {
        try {
            interopTracingClient.recoverTracing(UUID.fromString(tracingId), resource);
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
        replaceTracing(submitTracingResponse.getTracingId(), tracingFileUtils.getCsvFile(file));
    }

    private void replaceTracing(UUID tracingId, Resource resource) {
        try {
            interopTracingClient.replaceTracing(tracingId, resource);
        } catch (HttpStatusCodeException statusCodeException) {
            httpStatusCodeException = statusCodeException;
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while replacing the tracing data: " + ex);
        }
    }

    @When("viene sovrascritto il tracing con id: {string}")
    public void replaceTracingById(String tracingId) {
        replaceTracing(UUID.fromString(tracingId), tracingFileUtils.getCsvFile("corretto"));
    }

    @When("viene invocato l'endpoint di health con successo")
    public void getHealthStatus() {
        Assertions.assertDoesNotThrow(interopTracingClient::getHealthStatus);
    }

    @When("viene inviato il csv {string} per la data mancante")
    public void recoverMissingCsvForDate(String fileType) {
        Assertions.assertNotNull(getTracingsResponse, "There was an error while retrieving the tracing with MISSING status!");
        Assertions.assertFalse(getTracingsResponse.getResults().isEmpty(), "No tracing with MISSING status found!");
        GetTracingsResponseResults tracingsResponseResults = getTracingsResponse.getResults().get(0);
        tracingFileUtils.updateCsv(tracingsResponseResults.getDate().toString());
        submissionDate = tracingsResponseResults.getDate();
        uploadCsv(fileType);
    }

    @And("si attende che il file di tracing caricato passi in stato {string}")
    public void waitForStatus(String status) {
        PnPollingInteropTracing interopTracing = (PnPollingInteropTracing) pnPollingFactory.getPollingService(INTEROP_TRACING);
        PnTracingResponse pnTracingResponse = interopTracing.waitForEvent(null,
                PnPollingParameter.builder()
                        .value(INTEROP_TRACING)
                        .pollingType(PnPollingParameter.PollingType.RAPID)
                        .pnPollingInterop(new PnPollingInterop(submitTracingResponse.getTracingId().toString(), TracingState.fromValue(status)))
                        .build());
        Assertions.assertTrue(pnTracingResponse.getResult());
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
