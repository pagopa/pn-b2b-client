package it.pagopa.pn.interop.cucumber.steps.tracing;

import io.cucumber.java.AfterAll;
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
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.tracing.client.TracingS3Client;
import it.pagopa.interop.tracing.service.IInteropTracingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.TracingFileUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

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

        String getFormattedDate() {
            return referenceDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
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
            case "tenant1" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.TENANT_1.toString());
            case "tenant2" -> interopTracingClient.setBearerToken(SettableBearerToken.BearerTokenType.TENANT_2.toString());
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

    @When("viene preparato un file CSV valido e minimale per una data disponibile")
    public void generateValidAndMinimalCsv() {
        tracingFileUtils.generateValidAndMinimalTemporaryCsv(getFirstAvailableDate());
    }

    @When("viene preparato un file CSV valido da {int} MB per una data disponibile")
    public void generateValidCsvOfSize(int megabyte) {
        tracingFileUtils.generateValidTemporaryCsvOfSize(getFirstAvailableDate(), megabyte);
    }

    @When("viene preparato un file CSV con un purpose ID vuoto per una data disponibile")
    public void generateCsvWithEmptyPurposeId() {
        tracingFileUtils.generateTemporaryCsvWithEmptyPurposeId(getFirstAvailableDate());
    }

    @When("viene preparato un file CSV valido con un purpose ID non conforme per una data disponibile")
    public void generateValidCsvWithNotCompliantPurposeId() {
        tracingFileUtils.generateValidTemporaryCsvWithNotCompliantPurposeId(getFirstAvailableDate());
    }

    @When("viene preparato un file CSV valido con qualche record errato per una data disponibile")
    public void generateValidCsvWithSomeWrongRecords() {
        tracingFileUtils.generateValidTemporaryCsvWithSomeWrongRecords(getFirstAvailableDate());
    }

    @When("viene preparato un file CSV valido e minimale per un giorno in stato {string}")
    public void generateValidAndMinimalCsvForADayWithMissingState(String status) {
        retrieveTracing(List.of(TracingState.fromValue(status)));
        Assertions.assertNotNull(httpCallExecutor.getResponse(), "There was an error while retrieving the tracing with MISSING status!");
        Assertions.assertFalse(((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().isEmpty(), "No tracing with MISSING status found!");
        GetTracingsResponseResultsInner tracingsResponseResults = ((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().get(0);
        tracingFileUtils.generateValidAndMinimalTemporaryCsv(tracingsResponseResults.getDate());
        currentTracing.setReferenceDate(tracingsResponseResults.getDate());
    }

    @When("viene svuotato il purpose ID del primo record del file CSV preparato")
    public void emptyFirstPurposeIdFieldOfThePreparedCsv() {
        tracingFileUtils.emptyFirstPurposeIdFieldOfTheTemporaryCsv();
    }

    @When("viene inviato il file CSV {string}")
    public void uploadCsv(String fileType) {
        httpCallExecutor.performCall(() -> {
            SubmitTracingResponse response = interopTracingClient.submitTracing(tracingFileUtils.getCsvFile(fileType), currentTracing.getFormattedDate());
            currentTracing.setTracingId(response.getTracingId().toString());
        });
    }

    @When("viene recuperata la lista di tracing con uno stato tra i seguenti")
    public void retrieveTracingByStatusList(List<String> statusList) {
        List<TracingState> tracingStates = statusList.stream().map(TracingState::fromValue).toList();
        retrieveTracing(tracingStates);
    }

    @When("viene recuperata la lista di tracing con stato {string}")
    public void retrieveTracingByStatus(String status) {
        retrieveTracing(List.of(TracingState.fromValue(status)));
        Assertions.assertFalse(((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().isEmpty(), String.format("No Tracings were retrieved for the desired status: %s", status));
    }

    public void retrieveTracing(List<TracingState> statusList) {
        httpCallExecutor.performCall(() -> interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, statusList));
    }

    @Then("viene chiamato tracing con {method} e {subpath} contenente un carattere percent-encoded non valido")
    public void callTracingPathWithNotValidPercentEncodedChar(String method, String subpath) {
        httpCallExecutor.performCall(() -> {
            interopTracingClient.callTracingWithIllegalPercentEncodedCharInPath(method, subpath);
            Assertions.assertEquals(404, httpCallExecutor.getResponseStatus().value());
        });
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
        Assertions.assertTrue(httpCallExecutor.getResponse().toString().contains("TRACING_ALREADY_EXISTS"));
        Assertions.assertEquals(400, httpCallExecutor.getResponseStatus().value());
    }

    @Then("la chiamata fallisce perché la risorsa non viene trovata")
    public void verifyRejectionDueToNotAvailableResource() {
        Assertions.assertEquals(404, httpCallExecutor.getResponseStatus().value());
    }

    @When("viene recuperato il dettaglio del tracing con errori")
    public void retrieveTracingError() {
        getTracingErrors(((SubmitTracingResponse)httpCallExecutor.getResponse()).getTracingId());
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
                createExpectedResponse("INVALID_STATUS_CODE", "status: Invalid HTTP status code", "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("INVALID_DATE", String.format("date: Date field (2024-08-25) in csv is different from tracing date (%s).", currentTracing.getFormattedDate()), "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("PURPOSE_NOT_FOUND", "purpose_id: Invalid purpose id 0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8.", "0e1e4c98-6f2e-4f55-90e3-45f7d3f1dbf8", 1),
                createExpectedResponse("INVALID_PURPOSE", "purpose_id: Invalid uuid", "", 2),
                createExpectedResponse("INVALID_DATE", String.format("date: Date field (2024-08-25) in csv is different from tracing date (%s).", currentTracing.getFormattedDate()), "", 2)
        );
        org.assertj.core.api.Assertions.assertThat(((GetTracingErrorsResponse)httpCallExecutor.getResponse()).getResults()).containsAll(expectedResult);
    }

    @When("gli errori riscontrati vengono corretti passando il csv {string}")
    public void sanitizeErrors(String file) {
        Assertions.assertNotNull(httpCallExecutor.getResponse(), "There was an error while retrieving the tracing response!");
        Assertions.assertNotNull(((SubmitTracingResponse)httpCallExecutor.getResponse()).getTracingId());
        recoverError(((SubmitTracingResponse)httpCallExecutor.getResponse()).getTracingId().toString(), tracingFileUtils.getCsvFile(file));
    }

    @When("vengono corretti gli errori riscontrati per il tracingId {string}")
    public void sanitizeErrorsForSpecificTracingId(String tracingId) {
        recoverError(tracingId, tracingFileUtils.getCsvFile("corretto"));
    }

    private void recoverError(String tracingId, Resource resource) {
        httpCallExecutor.performCall(() -> interopTracingClient.recoverTracing(UUID.fromString(tracingId), resource));
    }

    @And("si verifica che il tracing sia presente tra quelli ritornati")
    public void checkReturnedTracingId() {
        Assertions.assertTrue(((GetTracingsResponse)httpCallExecutor.getResponse()).getResults()
                .stream()
                .map(GetTracingsResponseResultsInner::getTracingId)
                .anyMatch(tracingId -> tracingId.equals(currentTracing.getTracingId())));
    }

    @Given("viene sovrascritto il tracing aggiunto in precedenza con il csv: {string}")
    public void replaceTracing(String file) {
        replaceTracing(((SubmitTracingResponse)httpCallExecutor.getResponse()).getTracingId(), tracingFileUtils.getCsvFile(file));
    }

    private void replaceTracing(UUID tracingId, Resource resource) {
        httpCallExecutor.performCall(() -> interopTracingClient.replaceTracing(tracingId, resource));
    }

    @When("viene sovrascritto il tracing con id: {string}")
    public void replaceTracingById(String tracingId) {
        replaceTracing(UUID.fromString(tracingId), tracingFileUtils.getCsvFile("preparato"));
    }

    @When("viene invocato l'endpoint di health con successo")
    public void getHealthStatus() {
        Assertions.assertDoesNotThrow(interopTracingClient::getHealthStatus);
    }

    @And("si attende che il file di tracing caricato passi in stato {string}")
    public void waitForStatus(String state) {
        pollingService.makePolling(
                () -> (httpCallExecutor.performCall(() -> interopTracingClient.getTracings(OFFSET_VALUE, LIMIT_VALUE, List.of(TracingState.fromValue(state))))),
                res -> ((GetTracingsResponse)httpCallExecutor.getResponse()).getResults().stream()
                        .filter(x -> x.getTracingId().equals(currentTracing.getTracingId()))
                        .map(GetTracingsResponseResultsInner::getState)
                        .anyMatch(tracingState -> tracingState.equals(state)),
                String.format("The TracingId: %s did not reach the desired status: %s", currentTracing.getTracingId(), state)
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
                        .filter(x -> x.getTracingId().equals(((SubmitTracingResponse)httpCallExecutor.getResponse()).getTracingId().toString()))
                        .findFirst()
                        .orElse(null);

                if (result != null) {
                    int finalAttempt = attempt;
                    pollingService.makePolling(
                            () -> interopTracingClient.getTracings(finalAttempt, LIMIT_VALUE, List.of()),
                            res -> res.getResults().stream().anyMatch(x -> x.getTracingId().equals(((SubmitTracingResponse)httpCallExecutor.getResponse()).getTracingId().toString()) && x.getState().equals(state)),
                            String.format("The TracingId: %s did not reach the desired status: %s", ((SubmitTracingResponse)httpCallExecutor.getResponse()).getTracingId().toString(), state)
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
                .build();
    }

    private boolean isCsvTracingFilePresent(TracingS3Client.PollingSpecification pollingSpec, String bucketName, String s3PathKey) {
        return s3Client.isFileExistingInS3Bucket(pollingSpec, bucketName, s3PathKey);
    }

    private String composeS3KeyWithPrefixAndTracing(String prefix, Tracing tracing) {
        prefix += "-" + envProfile;
        return String.format(
                "%s/tenantId=%s/date=%s/tracingId=%s/version=%s/correlationId=%s/%s.csv",
                prefix,
                tracing.getTenantId(),
                tracing.getFormattedDate(),
                tracing.getTracingId(),
                tracing.getVersion(),
                tracing.getCorrelationId(),
                tracing.getTracingId()
        );
    }

    private String getCurrentUploadedTracingS3Key(Tracing tracing) {
        return composeS3KeyWithPrefixAndTracing("tracing-files" + envProfile, tracing);
    }

    private String getCurrentTracingErrorS3Key(Tracing tracing) {
        return composeS3KeyWithPrefixAndTracing("tracing-errors-files", tracing);
    }

    private String getCurrentEnrichedTracingS3Key(Tracing tracing) {
        return composeS3KeyWithPrefixAndTracing("tracing-enriched-files", tracing);
    }

    @Then("nessun file csv di tracing viene memorizzato, arricchito o raccolti i record errati")
    public void verifyNoNewCsvTracingGeneratedAtAll() {
        Assertions.assertFalse(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-store", getCurrentUploadedTracingS3Key(currentTracing)
        ));
        Assertions.assertFalse(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-errors", getCurrentTracingErrorS3Key(currentTracing)
        ));
        Assertions.assertFalse(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-enriched-files", getCurrentEnrichedTracingS3Key(currentTracing)
        ));
    }

    @Then("si attende che il file di tracing arricchito venga generato")
    public void verifyEnrichedCsvTracingFileIsGenerated() {
        Assertions.assertTrue(isCsvTracingFilePresent(
                getS3PollingSpecification(), "tracing-enriched-files", getCurrentEnrichedTracingS3Key(currentTracing)
        ));
    }

    @Then("si attende che il file di tracing venga arricchito con altri dati")
    public void verifyCsvUploadedFileIsEnriched() {
        String bucketName = "tracing-enriched-files";
        String s3Key = getCurrentEnrichedTracingS3Key(currentTracing);
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

    @Then("si attende che i record errati vengano tracciati negli errori")
    public void verifyWrongCsvRecordsAreTrackedInTracingErrors() {
        String bucketName = "tracing-errors";
        TracingS3Client.PollingSpecification pollingSpec = getS3PollingSpecification();

        Assertions.assertTrue(isCsvTracingFilePresent(
                pollingSpec, bucketName, getCurrentEnrichedTracingS3Key(currentTracing)
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(
                pollingSpec, bucketName, getCurrentTracingErrorS3Key(currentTracing)
        );

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
                pollingSpec, bucketName, getCurrentEnrichedTracingS3Key(currentTracing)
        ));
        String csvContent = s3Client.getTextualFileContentFromS3Bucket(
                pollingSpec, bucketName, getCurrentTracingErrorS3Key(currentTracing)
        );

        List<String[]> rows = csvContent.lines()
                .map(line -> line.split(","))
                .collect(Collectors.toList());

        // TODO non conosco ancora come viene esattamente segnato il WARNING nel file di errore
    }

    @AfterAll
    public static void removeTracingTemporaryCsvFolder() {
        TracingFileUtils.removeTemporaryFolder();
    }
}
