package it.pagopa.pn.interop.cucumber.steps.tracing;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.*;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.tracing.client.TracingS3Client;
import it.pagopa.interop.tracing.service.IInteropTracingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.TracingFileUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class TracingSteps {

    @Value("${spring.profiles.active}")
    private String envProfile;

    @Getter
    @Setter
    private class Tracing {
        String tracingId;
        String tenantId;
        String correlationId;
        LocalDate referenceDate;
        int version = 1;
        String state;

        String getFormattedDate() {
            return referenceDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        UUID getTracingUUID() {
            return UUID.fromString(tracingId);
        }
        void incrementVersion() { version++; }
    }

    private static Scenario currentScenario;

    public static String getTemporaryTracingFileName() {
        String pstTestId = currentScenario.getName().split("\\]", 2)[0].substring(1);
        return "/tracing_" + pstTestId + ".csv";
    }

    private static final int OFFSET_VALUE = 0;
    private static final int LIMIT_VALUE = 50;
    private final IInteropTracingClient interopTracingClient;
    private final TracingFileUtils tracingFileUtils;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final TracingS3Client s3Client;

    private String currentTenant;
    private Tracing currentTracing;

    /**
     * Dependency injection
     * @param interopTracingClient {@link IInteropTracingClient}
     * @param tracingFileUtils {@link TracingFileUtils}
     * @param sharedStepsContext {@link SharedStepsContext}
     */
    public TracingSteps(IInteropTracingClient interopTracingClient,
                        TracingFileUtils tracingFileUtils, SharedStepsContext sharedStepsContext) {
        this.interopTracingClient = interopTracingClient;
        this.tracingFileUtils = tracingFileUtils;
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.s3Client = new TracingS3Client();
        this.currentTracing = new Tracing();
    }

    @Given("l'utenza {string} effettua le chiamate")
    public void selectOperator(String operator) {
        currentTenant = operator.trim().toLowerCase();
        currentTracing.setTenantId(currentTenant);
        switch (currentTenant) {
            case "tenant1" ->
                    interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.TENANT_1.toString());
            case "tenant2" ->
                    interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.TENANT_2.toString());
            default -> throw new IllegalStateException("Unexpected value: " + operator.trim().toLowerCase());
        }
    }

    @Given("viene recuperata la prima data disponibile per un invio del file CSV")
    public LocalDate getFirstAvailableDate() {
        // To submit a tracing CSV file, it needs a day with a missing tracing CSV file
        // so it searches the oldest day without an uploaded tracing CSV file

        selectOperator(currentTenant);
        httpCallExecutor.performCall(() -> interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, null));

        LocalDate submissionDate = ((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().stream()
                .map(GetTracingsResponseResultsInner::getDate)
                .min(LocalDate::compareTo)
                .map(date -> date.minusDays(1))
                .orElseGet(() -> LocalDate.now().minusDays(1));

        currentTracing.setReferenceDate(submissionDate);
        return submissionDate;
    }

    public LocalDate getFirstDateWithExistingCsv() {
        selectOperator(currentTenant);
        httpCallExecutor.performCall(() -> interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, null));

        LocalDate submissionDate = ((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().stream()
                .filter(x -> x.getState().equals("COMPLETED"))
                .map(GetTracingsResponseResultsInner::getDate)
                .min(LocalDate::compareTo).get();

        currentTracing.setReferenceDate(submissionDate);
        return submissionDate;
    }

    @Given("viene preparato un file CSV valido e minimale per una data disponibile")
    public void generateValidAndMinimalCsv() {
        tracingFileUtils.generateValidAndMinimalTemporaryCsv(getFirstAvailableDate());
    }

    @Given("viene preparato un file CSV valido e minimale per una data già presente")
    public void generateValidAndMinimalCsvForDateWithExistingCsv() {
        tracingFileUtils.generateValidAndMinimalTemporaryCsv(getFirstDateWithExistingCsv());
    }

    @Given("viene preparato un file CSV valido da {int} MB per una data disponibile")
    public void generateValidCsvOfSize(int megabyte) {
        tracingFileUtils.generateValidTemporaryCsvOfSize(getFirstAvailableDate(), megabyte);
    }

    @Given("viene preparato un file CSV con un purpose ID vuoto per una data disponibile")
    public void generateCsvWithEmptyPurposeId() {
        tracingFileUtils.generateTemporaryCsvWithEmptyPurposeId(getFirstAvailableDate());
    }

    @Given("viene preparato un file CSV con tutti i campi errati per una data disponibile")
    public void generateCsvWithAllWrongFields() {
        getFirstAvailableDate();
        tracingFileUtils.generateTemporaryCsvWithAllWrongFields();
    }

    @Given("vengono corretti tutti i campi del file CSV preparato")
    public void fixAllTheFieldsOfGeneratedCsv() {
        tracingFileUtils.fixAllTheFieldsOfTemporaryCsv(currentTracing.getReferenceDate());
    }

    @Given("viene preparato un file CSV valido con un purpose ID non conforme per una data disponibile")
    public void generateValidCsvWithNotCompliantPurposeId() {
        tracingFileUtils.generateValidTemporaryCsvWithNotCompliantPurposeId(getFirstAvailableDate());
    }

    @Given("viene preparato un file CSV con un codice HTTP non valido per una data disponibile")
    public void generateCsvWithSomeWrongRecordsAndErrorOnHttpCode() {
        tracingFileUtils.generateTemporaryCsvWithSomeRecordsAndErrorOnHttpCode(getFirstAvailableDate());
    }

    @Given("viene preparato un file CSV valido e minimale per un giorno in stato {string}")
    public void generateValidAndMinimalCsvForADayWithMissingState(String status) {
        retrieveTracing(List.of(TracingState.fromValue(status)));
        Assertions.assertNotNull(httpCallExecutor.getResponse(), "There was an error while retrieving the tracing with MISSING status!");
        Assertions.assertFalse(((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().isEmpty(), "No tracing with MISSING status found!");
        GetTracingsResponseResultsInner tracingsResponseResults = ((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().get(0);
        tracingFileUtils.generateValidAndMinimalTemporaryCsv(tracingsResponseResults.getDate());
        currentTracing.setReferenceDate(tracingsResponseResults.getDate());
    }

    @Given("viene svuotato il purpose ID del primo record del file CSV preparato")
    public void emptyFirstPurposeIdFieldOfThePreparedCsv() {
        tracingFileUtils.emptyFirstPurposeIdFieldOfTheTemporaryCsv();
    }

    @When("viene inviato il file CSV {string}")
    public void uploadCsv(String fileType) {
        httpCallExecutor.performCall(() -> interopTracingClient.submitTracingWithHttpInfo(tracingFileUtils.getCsvFile(fileType), currentTracing.getFormattedDate()));
        try {
            ResponseEntity responseEntity = (ResponseEntity)httpCallExecutor.getResponse();
            currentTracing.setCorrelationId(responseEntity.getHeaders().getFirst("x-correlation-id"));
            currentTracing.setTracingId(((SubmitTracingResponse)responseEntity.getBody()).getTracingId().toString());
            log.info(String.format("Tracing ID in response: %s", currentTracing.getTracingId()));

        } catch (ClassCastException e) {
            currentTracing.setTracingId(null);
            log.info("Submit refused. No tracing ID in response.");
        }
    }

    @When("viene recuperata la lista di tracing con uno stato tra i seguenti")
    public void retrieveTracingByStatusList(List<String> statusList) {
        List<TracingState> tracingStates = statusList.stream().map(TracingState::fromValue).toList();
        retrieveTracing(tracingStates);
    }

    @When("viene recuperata la lista di tracing con stato {string}")
    public void retrieveTracingByStatus(String status) {
        httpCallExecutor.performCall(() -> interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, List.of(TracingState.fromValue(status))));
        GetTracingsResponse response = (GetTracingsResponse)httpCallExecutor.getResponse();
        Assertions.assertFalse(response.getResults().isEmpty(), String.format("No Tracings were retrieved for the desired status: %s", status));
        Assertions.assertFalse(response.getResults().stream().anyMatch(x -> !Objects.equals(x.getState(), status)));
    }

    public void retrieveTracing(List<TracingState> statusList) {
        httpCallExecutor.performCall(() -> interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, statusList));
    }

    @When("viene chiamato tracing con {method} e {subpath} contenente un carattere percent-encoded non valido")
    public void callTracingPathWithNotValidPercentEncodedChar(String method, String subpath) {
        httpCallExecutor.performCall(() -> interopTracingClient.callTracingWithIllegalPercentEncodedCharInPath(method, subpath));
    }

    @Then("la risposta contiene soltanto i tracing con stato {string}")
    public void verififyGetTracingResponse(String status) {
        Assertions.assertNotNull(httpCallExecutor.getResponse(), "There was an error while retrieving the getTracing response!");
        Assertions.assertFalse(((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().stream().anyMatch(x -> !Objects.equals(x.getState(), status)));
    }

    @Then("non viene trovato nessun tracing caricato")
    public void verifyGetTracingResponse() {
        Assertions.assertNotNull(httpCallExecutor.getResponse(), "There was an error while retrieving the getTracing response!");
        Assertions.assertTrue(((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().isEmpty());
    }

    @Then("il file CSV di tracing viene rifiutato perché già esistente")
    public void verifyRejectionOfCsvFileAlreadyPresent() {
        Assertions.assertEquals(400, httpCallExecutor.getResponseStatus().value());
        Assertions.assertTrue(httpCallExecutor.getErrorMessage().contains("TRACING_ALREADY_EXISTS"));
    }

    @Then("la richiesta fallisce perché la risorsa non viene trovata")
    public void verifyRejectionDueToNotAvailableResource() {
        Assertions.assertEquals(404, httpCallExecutor.getResponseStatus().value());
    }

    @Then("la richiesta fallisce con {esito}")
    public void verifyRejectionDueToFailedRequest(String outcome) {
        int expectedCode = ("not found".equals(outcome)) ? 404 : 400;
        Assertions.assertEquals(expectedCode, httpCallExecutor.getResponseStatus().value());
    }

    @When("viene recuperato il dettaglio del tracing con errori")
    public void retrieveTracingError() {
        getTracingErrors(currentTracing.getTracingUUID());
    }

    @Then("viene recuperato il dettaglio degli errori per il tracing {string}")
    public void retrieveSpecificTracingError(String tracingId) {
        getTracingErrors(UUID.fromString(tracingId));
    }

    private void getTracingErrors(UUID tracingId) {
        httpCallExecutor.performCall(() -> interopTracingClient.getTracingErrors(tracingId, OFFSET_VALUE, LIMIT_VALUE));
    }

    @Then("il dettaglio ritorna gli errori aspettati")
    public void verifyGetTracingErrorResponse() {
        Assertions.assertNotNull(httpCallExecutor.getResponse(), "There was an error while retrieving the tracing error!");
        Assertions.assertNotNull(((GetTracingErrorsResponse)httpCallExecutor.getResponse()).getResults());
        List<GetTracingErrorsResponseResultsInner> expectedResult = List.of(
                createExpectedResponse("INVALID_DATE", String.format("date: Date field (2020-01-01) in csv is different from tracing date (%s).", currentTracing.getFormattedDate()), "", 1),
                //createExpectedResponse("INVALID_DATE", String.format("date: Date field (2024-08-25) in csv is different from tracing date (%s).", currentTracing.getFormattedDate()), "", 2),
                createExpectedResponse("INVALID_PURPOSE", "purpose_id: Invalid uuid", "", 1),
                //createExpectedResponse("PURPOSE_NOT_FOUND", "purpose_id: Invalid purpose id 0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8.", "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("INVALID_TOKEN", "token_id: Invalid uuid", "", 1),
                createExpectedResponse("INVALID_STATUS_CODE", "status: Invalid HTTP status code", "", 1),
                createExpectedResponse("INVALID_REQUEST_COUNT", "requests_count: Invalid input", "", 1)
        );
        org.assertj.core.api.Assertions.assertThat(((GetTracingErrorsResponse)httpCallExecutor.getResponse()).getResults()).containsAll(expectedResult);
    }

    @When("gli errori riscontrati vengono corretti passando il csv {string}")
    public void sanitizeErrors(String file) {
        recoverError(currentTracing.getTracingId(), tracingFileUtils.getCsvFile(file));
    }

    @When("vengono corretti gli errori riscontrati per il tracingId {string}")
    public void sanitizeErrorsForSpecificTracingId(String tracingId) {
        recoverError(tracingId, tracingFileUtils.getCsvFile("preparato"));
    }

    private void recoverError(String tracingId, Resource resource) {
        httpCallExecutor.performCall(() -> interopTracingClient.recoverTracingWithHttpInfo(UUID.fromString(tracingId), resource));
        try {
            ResponseEntity responseEntity = (ResponseEntity)httpCallExecutor.getResponse();
            currentTracing.setCorrelationId(responseEntity.getHeaders().getFirst("x-correlation-id"));
            RecoverTracingResponse response = (RecoverTracingResponse)responseEntity.getBody();
            currentTracing.setTracingId(response.getTracingId().toString());
            currentTracing.incrementVersion();

        } catch (ClassCastException e) {
            log.info(String.format("Recover refused. No tracing ID %s found.", tracingId));
        }
    }

    @And("si verifica che il tracing sia presente tra quelli ritornati")
    public void checkReturnedTracingId() {
        pollingService.makePolling(
                () -> (httpCallExecutor.performCall(() -> interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, List.of(TracingState.fromValue("COMPLETED"))))),
                res -> ((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().stream()
                        .map(GetTracingsResponseResultsInner::getTracingId)
                        .anyMatch(tracingId -> {
                            log.info(String.format("Tracing ID found: %s", currentTracing.getTracingId()));
                            if (tracingId.equals(currentTracing.getTracingId())) {
                                Assertions.assertTrue(true);
                                return true;
                            }
                            return false;
                        }),
                String.format("TracingId %s not found.", currentTracing.getTracingId())
        );
    }

    @Given("viene sovrascritto il tracing aggiunto in precedenza con il csv {string}")
    public void replaceTracing(String file) {
        replaceTracing(currentTracing.getTracingUUID(), tracingFileUtils.getCsvFile(file));
    }

    private void replaceTracing(UUID tracingId, Resource resource) {
        httpCallExecutor.performCall(() -> interopTracingClient.replaceTracingWithHttpInfo(tracingId, resource));
        try {
            ResponseEntity responseEntity = (ResponseEntity)httpCallExecutor.getResponse();
            currentTracing.setCorrelationId(responseEntity.getHeaders().getFirst("x-correlation-id"));
            currentTracing.incrementVersion();

        } catch (ClassCastException e) {
            log.info(String.format("Replace refused. No tracing ID %s found.", tracingId));
        }
    }

    @When("viene sovrascritto il tracing con id: {string}")
    public void replaceTracingById(String tracingId) {
        replaceTracing(UUID.fromString(tracingId), tracingFileUtils.getCsvFile("preparato"));
    }

    @When("viene invocato endpoint per lo stato health con successo")
    public void getHealthStatus() {
        Assertions.assertDoesNotThrow(interopTracingClient::getHealthStatus);
    }

    @And("si attende che il file di tracing caricato passi in stato {string}")
    public void waitForStatus(String state) {
        int offset = getOffsetToFindTracingId(currentTracing.getTracingId());
        PollingService.makePolling(
                () -> (httpCallExecutor.performCall(() -> interopTracingClient.getTracings(offset, LIMIT_VALUE, List.of()))),
                res -> ((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().stream()
                        .filter(x -> x.getTracingId().equals(currentTracing.getTracingId()))
                        .map(GetTracingsResponseResultsInner::getState)
                        .anyMatch(tracingState -> "PENDING".equals(state) || "MISSING".equals(state) ? tracingState.equals(state) : !tracingState.equals("PENDING") && !tracingState.equals("MISSING")),
                String.format("The TracingId: %s did not reach the desired status: %s", currentTracing.getTracingId(), state),
                15, 10000
        );
        log.info(String.format("Tracing ID in use: %s", currentTracing.getTracingId()));
    }

    public int getOffsetToFindTracingId(String tracingId) {
        GetTracingsResponseResultsInner result;
        int offset = 0;
        int totalCount;
        do {
            GetTracingsResponse tracingsResponse = interopTracingClient.getTracings(offset, LIMIT_VALUE, List.of());
            totalCount = tracingsResponse.getTotalCount().intValue();
            result = tracingsResponse.getResults().stream()
                    .filter(x -> x.getTracingId().equals(tracingId))
                    .findFirst()
                    .orElse(null);
            if (result != null) {
                return offset;
            }
            offset += LIMIT_VALUE;
        } while (offset < totalCount);

        return 0;
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
        return getS3PollingSpecification(20_000, 5_000, 10);
    }

    private TracingS3Client.PollingSpecification getS3PollingSpecification(long timeoutMs, long pollIntervalMs, int deltaSeconds) {
        return TracingS3Client.PollingSpecification.builder()
                .timeoutMs(timeoutMs)
                .pollIntervalMs(pollIntervalMs)
                .build();
    }

    private boolean isCsvTracingFilePresent(TracingS3Client.PollingSpecification pollingSpec, String bucketName, String s3PathKey) {
        return s3Client.isFileExistingInS3Bucket(pollingSpec, bucketName, s3PathKey);
    }

    private String composeS3KeyWithTracing(Tracing tracing) {
        String tenantType = ("TENANT2".equals(currentTenant)) ? "PA2" : "PA1";
        String key = String.format(
                "tenantId=%s/date=%s/tracingId=%s/version=%s/correlationId=%s/%s.csv",
                interopTracingClient.getIdentityService().getOrganizationId(tenantType),
                tracing.getFormattedDate(),
                tracing.getTracingId(),
                tracing.getVersion(),
                tracing.getCorrelationId(),
                tracing.getTracingId()
        );
        log.info("AWS S3 KEY: " + key);
        return key;
    }

    @Then("si attende che l'invio in ERROR sia registrato come header CSV non valido")
    public void verifyWrongCsvHeaderIsTrackedInTracingErrors() {
        String bucketName = "tracing-errors-files-" + envProfile;
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(
                pollingSpec, bucketName, composeS3KeyWithTracing(currentTracing)
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(
                pollingSpec, bucketName, composeS3KeyWithTracing(currentTracing)
        );

        List<String[]> rows = csvContent.lines()
                .map(line -> line.split(","))
                .collect(Collectors.toList());
        List<String> actualFields = Arrays.asList(rows.get(0));

        final List<String> expectedFields = List.of(
                "id", "tracing_id", "version", "purpose_id", "severity",
                "error_code", "message", "row_number"
        );
        Assertions.assertTrue(actualFields.containsAll(expectedFields));

        int severityIndex = expectedFields.indexOf("severity");
        int errorCodeIndex = expectedFields.indexOf("error_code");

        Assertions.assertEquals("INVALID", Arrays.asList(rows.get(1)).get(severityIndex));
        Assertions.assertEquals("INVALID_CSV_HEADERS", Arrays.asList(rows.get(1)).get(errorCodeIndex));
    }

    @Then("nessun file CSV di tracing viene arricchito")
    public void verifyNoNewEnrichedCsvTracingGenerated() {
        Assertions.assertFalse(isCsvTracingFilePresent(
                getS3PollingSpecification(4_000, 1_000, 2),
                "tracing-enriched-files-" + envProfile, composeS3KeyWithTracing(currentTracing)
        ));
    }

    @Then("si attende che il file di tracing venga ricevuto")
    public void verifyCsvTracingFileIsReceived() {
        Assertions.assertTrue(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-files-" + envProfile, composeS3KeyWithTracing(currentTracing)
        ));
    }

    @Then("si attende che il file di tracing arricchito venga generato")
    public void verifyEnrichedCsvTracingFileIsGenerated() {
        Assertions.assertTrue(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-enriched-files-" + envProfile, composeS3KeyWithTracing(currentTracing)
        ));
    }

    @Then("si attende fino a {int} minuti che il file di tracing arricchito venga generato")
    public void verifyEnrichedCsvTracingFileIsGeneratedWithLongWait(int waitInMinutes) {
        long timeoutMs = waitInMinutes * 60 * 1000L;
        long pollIntervalMs = 30_000;
        int deltaSeconds = 60;
        Assertions.assertTrue(isCsvTracingFilePresent(
                getS3PollingSpecification(timeoutMs, pollIntervalMs, deltaSeconds),
                "tracing-enriched-files-" + envProfile, composeS3KeyWithTracing(currentTracing)
        ));
    }

    @Then("si attende che il file di tracing venga arricchito con altri dati")
    public void verifyCsvUploadedFileIsEnriched() {
        String bucketName = "tracing-enriched-files-" + envProfile;
        String s3Key = composeS3KeyWithTracing(currentTracing);
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(pollingSpec, bucketName, s3Key));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(pollingSpec, bucketName, s3Key);

        List<String> actualFields = Arrays.asList(
                csvContent.lines()
                        .findFirst()
                        .orElse("")
                        .split(",")
        );
        final List<String> expectedFields = List.of(
                "tracingId", "submitterId", "date", "purposeId", "purposeName",
                "status", "token_id", "requestsCount", "eserviceId", "consumerId",
                "consumerOrigin", "consumerName", "consumerExternalId", "producerId", "producerName",
                "producerOrigin", "producerExternalId"
        );
        Assertions.assertTrue(actualFields.containsAll(expectedFields));
    }

    @Then("si attende che il record con codice HTTP non valido sia tracciato negli errori")
    public void verifyWrongCsvRecordsAreTrackedInTracingErrors() {
        String bucketName = "tracing-errors-files-" + envProfile;
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(
                pollingSpec, bucketName, composeS3KeyWithTracing(currentTracing)
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(
                pollingSpec, bucketName, composeS3KeyWithTracing(currentTracing)
        );

        List<String[]> rows = csvContent.lines()
                .map(line -> line.split(","))
                .collect(Collectors.toList());
        List<String> actualFields = Arrays.asList(rows.get(0));

        final List<String> expectedFields = List.of(
                "id", "tracing_id", "version", "purpose_id", "severity",
                "error_code", "message", "row_number"
        );
        Assertions.assertTrue(actualFields.containsAll(expectedFields));

        int severityIndex = expectedFields.indexOf("severity");
        int errorCodeIndex = expectedFields.indexOf("error_code");

        Assertions.assertEquals("INVALID", Arrays.asList(rows.get(1)).get(severityIndex));
        Assertions.assertEquals("INVALID_STATUS_CODE", Arrays.asList(rows.get(1)).get(errorCodeIndex));
    }

    @Then("si attende che l'invio in WARNING sia registrato come purpose ID non conforme all'utenza")
    public void verifyWarningCsvRecordsAreTrackedInTracingErrors() {
        String bucketName = "tracing-errors-files-" + envProfile;
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(
                pollingSpec, bucketName, composeS3KeyWithTracing(currentTracing)
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(
                pollingSpec, bucketName, composeS3KeyWithTracing(currentTracing)
        );

        List<String[]> rows = csvContent.lines()
                .map(line -> line.split(","))
                .collect(Collectors.toList());
        List<String> actualFields = Arrays.asList(rows.get(0));

        final List<String> expectedFields = List.of(
                "id", "tracing_id", "version", "purpose_id", "severity",
                "error_code", "message", "row_number"
        );
        Assertions.assertTrue(actualFields.containsAll(expectedFields));

        int severityIndex = expectedFields.indexOf("severity");
        int errorCodeIndex = expectedFields.indexOf("error_code");

        Assertions.assertEquals("WARNING", Arrays.asList(rows.get(1)).get(severityIndex));
        Assertions.assertEquals("TENANT_IS_NOT_PRODUCER_OR_CONSUMER", Arrays.asList(rows.get(1)).get(errorCodeIndex));
    }

    @Before("@interopTracingCsv")
    public static void getScenario(Scenario scenario) {
        currentScenario = scenario;
    }

    @After("@interopTracingCsv")
    public static void removeTracingTemporaryCsvFolder() {
        TracingFileUtils.removeTemporaryFileAndFolder(getTemporaryTracingFileName());
    }
}
