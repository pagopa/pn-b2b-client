package it.pagopa.pn.interop.cucumber.steps.tracing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingErrorsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingErrorsResponseResultsInner;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingsResponseResultsInner;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.SubmitTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.TracingState;
import it.pagopa.interop.tracing.client.TracingS3Client;
import it.pagopa.interop.tracing.service.IInteropTracingClient;
import it.pagopa.pn.interop.cucumber.utility.TracingFileUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.stream.Collectors;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class TracingSteps {
    private static final int OFFSET_VALUE = 0;
    private static final int LIMIT_VALUE = 50;
    private final IInteropTracingClient interopTracingClient;
    private final TracingFileUtils tracingFileUtils;
    private final PollingService pollingService;
    private final TracingS3Client s3Client;

    private SubmitTracingResponse submitTracingResponse;
    private GetTracingsResponse getTracingsResponse;
    private GetTracingErrorsResponse getTracingErrorsResponse;
    private HttpStatusCodeException httpStatusCodeException;
    private LocalDate submissionDate;

    /**
     * Dependency injection
     * @param interopTracingClient {@link IInteropTracingClient}
     * @param tracingFileUtils {@link TracingFileUtils}
     * @param pollingService {@link PollingService}
     */
    public TracingSteps(IInteropTracingClient interopTracingClient,
                        TracingFileUtils tracingFileUtils, PollingService pollingService) {
        this.interopTracingClient = interopTracingClient;
        this.tracingFileUtils = tracingFileUtils;
        this.pollingService = pollingService;
        this.s3Client = new TracingS3Client();
    }

    @Given("l'utenza {string} effettua le chiamate")
    public void selectOperator(String operator) {
        switch (operator.trim().toLowerCase()) {
            case "tenant1" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.TENANT_1.toString());
            case "tenant2" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.TENANT_2.toString());
            default -> throw new IllegalStateException("Unexpected value: " + operator.trim().toLowerCase());
        }
    }

    @Given("viene aggiornato il file CSV con la prima data disponibile")
    public void updateCsv() {
        selectOperator("tenant1");
        GetTracingsResponse tracingsResponse = interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, null);
        submissionDate = tracingsResponse.getResults().stream()
                .map(GetTracingsResponseResultsInner::getDate)
                .min(LocalDate::compareTo)
                .map(date -> date.minusDays(1))
                .orElseGet(() -> LocalDate.now().minusDays(1));
        tracingFileUtils.updateCsv(submissionDate);
    }

    @When("viene inviato il file CSV {string}")
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
        Assertions.assertFalse(getTracingsResponse.getResults().isEmpty(), String.format("No Tracings were retrieved for the desired status: %s", status));
    }

    public void retrieveTracing(List<TracingState> statusList) {
        try {
            getTracingsResponse = interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, statusList);
        } catch (HttpStatusCodeException statusCodeException) {
            httpStatusCodeException = statusCodeException;
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while retrieving the tracings: " + ex);
        }
    }

    @Then("viene chiamato tracing con un path contenente un carattere percent-encoded non valido")
    public void callTracingPathWithNotValidPercentEncodedChar() {
        ResponseEntity<Void> response = interopTracingClient.callTracingWithIllegalPercentEncodedCharInPath();
        Assertions.assertEquals(404, response.getStatusCode());
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

    @Then("il file CSV di tracing viene rifiutato perché già esistente")
    public void verifyRejectionOfCsvFileAlreadyPresent() {
        // TODO Non posso lanciare il teste devo capire come arriva e come recuperare errors.code (descrittivo)
        Assertions.assertTrue(submitTracingResponse.toString().contains("TRACING_ALREADY_EXISTS"));
        Assertions.assertEquals(400, httpStatusCodeException.getStatusCode().value());
    }

    @Then("la chiamata fallisce perché la risorsa non viene trovata")
    public void verifyRejectionDueToNotAvailableResource() {
        Assertions.assertEquals(404, httpStatusCodeException.getStatusCode().value());
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
            getTracingErrorsResponse = interopTracingClient.getTracingErrors(tracingId, OFFSET_VALUE, LIMIT_VALUE);
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
        List<GetTracingErrorsResponseResultsInner> expectedResult = List.of(
                createExpectedResponse("INVALID_STATUS_CODE", "status: Invalid HTTP status code", "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("INVALID_DATE", String.format("date: Date field (2024-08-25) in csv is different from tracing date (%s).", submissionDate.toString()), "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("PURPOSE_NOT_FOUND", "purpose_id: Invalid purpose id 0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8.", "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("INVALID_PURPOSE", "purpose_id: Invalid uuid", "", 2),
                createExpectedResponse("INVALID_DATE", String.format("date: Date field (2024-08-25) in csv is different from tracing date (%s).", submissionDate.toString()), "", 2)
        );
        org.assertj.core.api.Assertions.assertThat(getTracingErrorsResponse.getResults()).containsAll(expectedResult);
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
                .map(GetTracingsResponseResultsInner::getTracingId)
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
        GetTracingsResponseResultsInner tracingsResponseResults = getTracingsResponse.getResults().get(0);
        tracingFileUtils.updateCsv(tracingsResponseResults.getDate());
        submissionDate = tracingsResponseResults.getDate();
        uploadCsv(fileType);
    }

    @And("si attende che il file di tracing caricato passi in stato {string}")
    public void waitForStatus(String state) {
        pollingService.makePolling(
                () -> interopTracingClient.getTracings(0, 50, List.of(TracingState.fromValue(state))),
                res -> res.getResults().stream()
                        .filter(x -> x.getTracingId().equals(submitTracingResponse.getTracingId().toString()))
                        .map(GetTracingsResponseResultsInner::getState)
                        .anyMatch(tracingState -> tracingState.equals(state)),
                String.format("The TracingId: %s did not reach the desired status: %s", submitTracingResponse.getTracingId().toString(), state)
        );
    }

    @Then("viene recuperato il file di tracing appena caricato e si verifica che lo stato sia {string}")
    public void retrieveTracingAndVerifyStatus(String state) {
        GetTracingsResponseResultsInner result;
        int attempt = 0;
        int totalPages;
        try  {
            do {
                GetTracingsResponse tracingsResponse = interopTracingClient.getTracings(attempt, LIMIT_VALUE, List.of());
                totalPages = tracingsResponse.getTotalCount().intValue();
                result = tracingsResponse.getResults().stream()
                        .filter(x -> x.getTracingId().equals(submitTracingResponse.getTracingId().toString()))
                        .findFirst()
                        .orElse(null);

                if (result != null) {
                    int finalAttempt = attempt;
                    pollingService.makePolling(
                            () -> interopTracingClient.getTracings(finalAttempt, LIMIT_VALUE, List.of()),
                            res -> res.getResults().stream().anyMatch(x -> x.getTracingId().equals(submitTracingResponse.getTracingId().toString()) && x.getState().equals(state)),
                            String.format("The TracingId: %s did not reach the desired status: %s", submitTracingResponse.getTracingId().toString(), state)
                    );
                    break;
                } else attempt++;

            } while (attempt < totalPages / LIMIT_VALUE + 1);
            if (result == null) {
                throw new RuntimeException("Tracing ID not found after " + attempt + " attempts!");
            }
        } catch (Exception e) {
            throw new RuntimeException("There was an error while retrieving the tracing file!");
        }
    }

    private GetTracingErrorsResponseResultsInner createExpectedResponse(String errorCode, String message, String purposeId, Integer rowNumber) {
        GetTracingErrorsResponseResultsInner tracingErrorsResponse = new GetTracingErrorsResponseResultsInner();
        tracingErrorsResponse.setErrorCode(errorCode);
        tracingErrorsResponse.setMessage(message);
        tracingErrorsResponse.setPurposeId(purposeId);
        tracingErrorsResponse.setRowNumber(rowNumber);
        return tracingErrorsResponse;
    }

    private TracingS3Client.PollingSpecification getS3PollingSpecification() {
        return TracingS3Client.PollingSpecification.builder()
                .centerTimestamp(Instant.now().toString())
                .timeoutMs(600_000)
                .pollIntervalMs(30_000)
                .deltaSeconds(300)
                //.fileInfo(InteropFile.?)
                //.bucketRole(BucketRole.valueOf("?"))
                .build();
    }

    private boolean isCsvTracingFilePresent(TracingS3Client.PollingSpecification pollingSpec, String bucketName, String fileName) {
        return s3Client.isFileExistingInS3Bucket(pollingSpec, bucketName, fileName);
    }

    private String getCurrentUploadedTracingFileName() {
        // TODO: devo conoscere il criterio con cui i file di tracing inviati ricevono un nome
        return "tracing_file_2026_04_02.csv";
    }

    private String getCurrentTracingErrorsFileName() {
        // TODO: devo conoscere il criterio con cui i file che tracciano gli errori dentro un csv di tracing
        // ricevono un nome
        return "tracing_errors_file_2026_04_02.csv";
    }

    private String getCurrentEnrichedFileName() {
        // TODO: devo conoscere il criterio con cui i file di tracing arricchiti ricevono un nome
        return "enriched_file_2026_04_02.csv";
    }

    @Then("nessun file csv di tracing viene memorizzato, arricchito o raccolti i record errati")
    public void verifyNoNewCsvTracingGeneratedAtAll() {
        Assertions.assertFalse(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-store", getCurrentUploadedTracingFileName()
        ));
        Assertions.assertFalse(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-errors", getCurrentTracingErrorsFileName()
        ));
        Assertions.assertFalse(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-enriched-files", getCurrentEnrichedFileName()
        ));
    }

    @Then("si attende che il file di tracing arricchito venga generato")
    public void verifyEnrichedCsvTracingFileIsGenerated() {
        Assertions.assertTrue(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-enriched-files", getCurrentEnrichedFileName()
        ));
    }

    @Then("si attende che il file di tracing venga arricchito con altri dati")
    public void verifyCsvUploadedFileIsEnriched() {
        String bucketName = "tracing-enriched-files";
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(
                pollingSpec, bucketName, getCurrentEnrichedFileName()
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(pollingSpec, bucketName, getCurrentEnrichedFileName());

        List<String[]> rows = csvContent.lines()
                .map(line -> line.split(","))
                .collect(Collectors.toList());

        // TODO non conosco ancora i campi che vengono aggiornati
//        public static final List<String> expectedFields = List.of(
//                "Elemento1", "Elemento2", "Elemento3", "Elemento4", "Elemento5",
//                "Elemento6", "Elemento7", "Elemento8", "Elemento9", "Elemento10",
//                "Elemento11", "Elemento12", "Elemento13", "Elemento14", "Elemento15",
//                "Elemento16", "Elemento17"
//        );
        // tracingId,submitterId,date,purposeId,purposeName,status,token_id,requestsCount,eserviceId,consumerId,consumerOrigin,consumerName,consumerExternalId,producerId,producerName,producerOrigin,producerExternalId
    }

    @Then("si attende che i record errati vengano tracciati negli errori")
    public void verifyWrongCsvRecordsAreTrackedInTracingErrors() {
        String bucketName = "tracing-errors";
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(
                pollingSpec, bucketName, getCurrentEnrichedFileName()
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(pollingSpec, bucketName, getCurrentTracingErrorsFileName());

        List<String[]> rows = csvContent.lines()
                .map(line -> line.split(","))
                .collect(Collectors.toList());

        // TODO non conosco ancora come viene esattamente scritto il file di errore
    }

    @Then("si attende che i record con purpose non conformi vengano tracciati con warning")
    public void verifyWarningCsvRecordsAreTrackedInTracingErrors() {
        String bucketName = "tracing-errors";
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(
                pollingSpec, bucketName, getCurrentEnrichedFileName()
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(pollingSpec, bucketName, getCurrentTracingErrorsFileName());

        List<String[]> rows = csvContent.lines()
                .map(line -> line.split(","))
                .collect(Collectors.toList());

        // TODO non conosco ancora come viene esattamente segnato il WARNING nel file di errore
    }
}
