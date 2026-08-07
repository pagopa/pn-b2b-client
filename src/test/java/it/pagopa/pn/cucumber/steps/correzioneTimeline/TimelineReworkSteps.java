package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItemsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.UpdateReworkRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.*;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.AnalogUpdateCostPhase;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PaperCostToInvalidate;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PaymentsInfo;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryInvalidatedElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalRegistryPrivateUserApi;
import it.pagopa.pn.client.b2b.pa.service.IPnNotificationCostClient;
import it.pagopa.pn.client.b2b.pa.service.impl.ReworkTimelineClientImpl;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.utilitySteps.Costanti;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils.getDataTableParams;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@RequiredArgsConstructor
public class TimelineReworkSteps {

    @Before("@timelineReworkF2 or @timelineRework")
    public void beforeMethod() {
        this.resetTimestamp();
    }

    private final SharedSteps sharedSteps;
    private final ReworkTimelineClientImpl reworkTimelineClient;
    private final DynamoDbService dynamoDbService;
    private final IPnNotificationCostClient notificationCostClient;
    private final IPnExternalRegistryPrivateUserApi externalRegistryPrivateUserApi;
    private ReworkResponse reworkResponse;
    private RestartAttemptResponse restartAttemptResponse;
    private InvalidateTimelineElementsResponse removeElementsResponse;
    private InvalidateTimelineElementsRequest punctualCorrectionRequest;
    private QueryResponse reworkedTimelinesForInvoicingResponse;
    private HttpStatus httpStatusCode;
    private String timestampString;
    private List<String> attempt1ElementIds = new ArrayList<>();
    private final Map<ReworkItem.StatusEnum, Integer> reworkStatusValueMap = Map.of(
            ReworkItem.StatusEnum.CREATED, 1,
            ReworkItem.StatusEnum.READY, 2,
            ReworkItem.StatusEnum.IN_PROGRESS, 3,
            ReworkItem.StatusEnum.DONE, 4,
            ReworkItem.StatusEnum.ERROR, 5
    );

    @ParameterType("rework|restart|remove")
    public static String timelineInvalidation(String value) {
        if (value.equals("remove")) {
            return "INVALIDATE_ELEMENTS";
        }
        return value.toUpperCase();
    }

    @ParameterType("INVALIDATED|NEW")
    public static String invoicingType(String value) {
        return value;
    }

    @ParameterType("ERROR|CREATED|READY|IN_PROGRESS|DONE")
    public static ReworkItem.StatusEnum reworkItemStatus(String value) {
        return ReworkItem.StatusEnum.fromValue(value);
    }

    @And("viene resettato il timestamp")
    public void resetTimestamp() {
        this.timestampString = null;
    }

    @And("viene aggiornata la richiesta di {timelineInvalidation} con i seguenti dati:")
    public void updateRequestReworkWithError(String requestType, DataTable params) {
        ResolvedUpdate update = resolveUpdateRework(requestType, params);
        try {
            reworkTimelineClient.updateNotificationRework(update.iun(), update.reworkId(), update.request());
        } catch (HttpStatusCodeException exception) {
            httpStatusCode = exception.getStatusCode();
        }
    }

    @And("viene correttamente aggiornata la richiesta di {timelineInvalidation} con i seguenti dati:")
    public void updateRequestRework(String requestType, DataTable params) {
        ResolvedUpdate update = resolveUpdateRework(requestType, params);
        Assertions.assertDoesNotThrow(
                () -> reworkTimelineClient.updateNotificationRework(update.iun(), update.reworkId(), update.request()),
                () -> String.format(
                        "Errore aggiornamento rework | IUN=%s | reworkId=%s | request=%s",
                        update.iun(),
                        update.reworkId(),
                        update.request()
                )
        );
    }

    private ResolvedUpdate resolveUpdateRework(String requestType, DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String iun = getDataTableParams(inputData, "iun", sharedSteps.getNotificationIun());
        String reworkId = getDataTableParams(inputData, "reworkId", resolveReworkId(requestType));
        if (reworkId == null) {
            throw new AssertionError("reworkId nullo | IUN=" + iun);
        }
        UpdateReworkRequest request = ReworkRequestFactory.updateReworkRequest(
                getDataTableParams(inputData, "expectedStatusCode", null),
                getDataTableParams(inputData, "expectedDeliveryFailureCause", null)
        );
        return new ResolvedUpdate(iun, reworkId, request);
    }

    private record ResolvedUpdate(String iun, String reworkId, UpdateReworkRequest request) {
    }

    /**
     * ReworkId dell'ultima richiesta di rework / restart / remove effettuata nello scenario, null se assente.
     */
    private String resolveReworkId(String requestType) {
        return switch (requestType) {
            case "REWORK" -> reworkResponse != null ? reworkResponse.getReworkId() : null;
            case "RESTART" -> restartAttemptResponse != null ? restartAttemptResponse.getReworkId() : null;
            case "INVALIDATE_ELEMENTS" -> removeElementsResponse != null ? removeElementsResponse.getReworkId() : null;
            default ->
                    throw new IllegalArgumentException("Invalid requestType for timeline correction: " + requestType);
        };
    }

    @And("viene invocata una richiesta di rework per la notifica appena creata")
    public void callReworkTimeline() {
        try {
            reworkResponse = reworkTimelineClient.notificationRework(sharedSteps.getNotificationIun(), ReworkRequestFactory.defaultReworkRequest());
            log.info("Successfully reworked. Rework response: {}", reworkResponse);
        } catch (HttpStatusCodeException e) {
            httpStatusCode = e.getStatusCode();
        }
    }

    @And("viene invocata una richiesta di restart per la notifica appena creata")
    public void callRestartTimeline() {
        try {
            restartAttemptResponse = reworkTimelineClient.restartAttempt(sharedSteps.getNotificationIun(), ReworkRequestFactory.defaultRestartRequest());
            log.info("Successfully restarted. Restart attempt response: {}", restartAttemptResponse);
        } catch (HttpStatusCodeException e) {
            httpStatusCode = e.getStatusCode();
        }
    }

    @And("viene invocata una richiesta di correzione puntuale per la notifica appena creata con i seguenti parametri")
    public void callInvalidateTimelineElementsFromData(Map<String, String> inputData) {
        try {
            String iun = getDataTableParams(inputData, "iun", sharedSteps.getNotificationIun());
            String recIndex = getDataTableParams(inputData, "recIndex", "RECINDEX_0");
            FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
            punctualCorrectionRequest = ReworkRequestFactory.invalidationRequest(inputData, recIndex, fsn);
            removeElementsResponse = reworkTimelineClient.invalidateTimelineElements(iun, punctualCorrectionRequest);
            log.info("Successfully invalidated. Invalidation response: {}", removeElementsResponse);
        } catch (HttpStatusCodeException e) {
            httpStatusCode = e.getStatusCode();
        }
    }

    @And("viene ripetuta la richiesta di invalidazione precedente")
    public void repeatInvalidateTimelineElements() {
        assertThat(punctualCorrectionRequest).as("La richiesta di invalidazione precedente non è stata inizializzata").isNotNull();
        try {
            removeElementsResponse = reworkTimelineClient.invalidateTimelineElements(sharedSteps.getNotificationIun(), punctualCorrectionRequest);
            log.info("Successfully invalidated. Invalidation response: {}", removeElementsResponse);
        } catch (HttpStatusCodeException e) {
            httpStatusCode = e.getStatusCode();
        }
    }

    @And("si verifica che in fase di rework non ci sono richieste appese in stato diverso da DONE o ERROR")
    public void verifyReworkStatusNotInDoneOrErrorStatus() {
        ReworkItemsResponse reworkItemsResponse = reworkTimelineClient.retrieveNotificationReworkById(sharedSteps.getNotificationIun(), null);
        reworkItemsResponse.getItems().stream()
                .filter(reworkItem -> !List.of(ReworkItem.StatusEnum.DONE, ReworkItem.StatusEnum.ERROR).contains(reworkItem.getStatus()))
                .findAny()
                .ifPresent(value -> {
                    throw new RuntimeException("Errore: ci sono richieste in sospeso con stato diverso da DONE o ERROR");
                });
    }

    @And("si verifica che la richiesta di {timelineInvalidation} effettuata sia in stato {string}")
    public void verifyReworkStatusById(String requestType, String status) {
        String reworkId = resolveReworkId(requestType);
        ReworkItemsResponse reworkItemsResponse = reworkTimelineClient.retrieveNotificationReworkById(sharedSteps.getNotificationIun(), reworkId);
        checkRequestType(requestType);
        reworkItemsResponse.getItems().stream()
                .filter(reworkItem -> reworkItem.getStatus() == ReworkItem.StatusEnum.fromValue(status))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Errore la richiesta creata non è nello stato desiderato!"));
    }

    @And("viene invocata una richiesta di rework con eccezione per la notifica appena creata con i seguenti parametri:")
    public void callReworkWithParamsWithError(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String iun = getDataTableParams(inputData, "iun", sharedSteps.getNotificationIun());
        try {
            reworkResponse = reworkTimelineClient.notificationRework(iun, reworkRequestFromDataTable(inputData));
        } catch (HttpStatusCodeException exception) {
            log.info("Error caught: {}", exception.getMessage());
            httpStatusCode = exception.getStatusCode();
        }
    }

    @And("viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:")
    public void callReworkWithParams(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String iun = getDataTableParams(inputData, "iun", sharedSteps.getNotificationIun());
        try {
            reworkResponse = reworkTimelineClient.notificationRework(iun, reworkRequestFromDataTable(inputData));
        } catch (HttpStatusCodeException exception) {
            throw new AssertionError(
                    String.format(
                            "Errore chiamata rework | IUN=%s | HTTP=%s | body=%s",
                            sharedSteps.getNotificationIun(),
                            exception.getStatusCode(),
                            exception.getResponseBodyAsString()
                    ),
                    exception
            );
        }
    }

    @And("viene invocata una richiesta di restart per la notifica appena creata con i seguenti parametri:")
    public void callRestartWithParams(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String iun = getDataTableParams(inputData, "iun", sharedSteps.getNotificationIun());
        RestartAttemptRequest.AttemptIdEnum attemptId = inputData.get("attemptId") != null ? RestartAttemptRequest.AttemptIdEnum.fromValue(inputData.get("attemptId")) : null;
        RestartAttemptRequest restartAttemptRequest = ReworkRequestFactory.restartRequest(
                attemptId, inputData.get("recIndex"), inputData.get("reason"), inputData.get("task"), inputData.get("canInvalidateViewed"));
        try {
            restartAttemptResponse = reworkTimelineClient.restartAttempt(iun, restartAttemptRequest);
        } catch (HttpStatusCodeException exception) {
            log.info("Error caught: {}", exception.getMessage());
            httpStatusCode = exception.getStatusCode();
        }
    }

    private ReworkRequest reworkRequestFromDataTable(Map<String, String> inputData) {
        String attemptId = getDataTableParams(inputData, "attemptId", "ATTEMPT_0");
        return ReworkRequestFactory.reworkRequest(
                attemptId != null ? ReworkRequest.AttemptIdEnum.fromValue(attemptId) : null,
                getDataTableParams(inputData, "reason", "reason"),
                getDataTableParams(inputData, "pcRetry", "PCRETRY_0"),
                getDataTableParams(inputData, "recIndex", "RECINDEX_0"),
                getDataTableParams(inputData, "expectedStatusCode", "RECRI003C"),
                getDataTableParams(inputData, "expectedDeliveryFailureCause", null)
        );
    }

    @And("si verifica che la chiamata sia andata in errore con il seguente status code: {int}")
    public void verifyErrorResponseStatusCode(int statusCode) {
        Assertions.assertEquals(HttpStatus.resolve(statusCode), httpStatusCode);
        httpStatusCode = null;
    }

    @Then("vengono effettuati i controlli sugli elementi invalidati usando la lista {string}")
    public void verifyInvalidatedTimelineElementsFailFast(String listName) {
        List<String> elementsToCheck = switch (listName) {
            case "BASE" -> Costanti.REWORK_ELEMENTS_BASE_LIST;
            case "ESTESA" -> Costanti.REWORK_ELEMENTS_EXTENDED_LIST;
            default -> throw new IllegalArgumentException(
                    "Lista non supportata: " + listName
            );
        };
        verifyInvalidatedTimelineElementsFailFast(elementsToCheck);
    }

    public void verifyInvalidatedTimelineElementsFailFast(List<String> elementsToCheck) {

        List<NotificationStatusHistoryInvalidatedElement> invalidatedHistory = getInvalidatedHistoryFailFast();

        List<String> invalidElementIds = invalidatedHistory.stream()
                .flatMap(h -> h.getRelatedTimelineElements().stream())
                .map(TimelineElementV28::getElementId)
                .filter(Objects::nonNull)
                .filter(elementId -> elementsToCheck.stream().noneMatch(elementId::contains))
                .toList();

        if (!invalidElementIds.isEmpty()) {
            log.error("Trovati elementId non validi in relatedTimelineElements:");
            invalidElementIds.forEach(id -> log.error(" - {}", id));
        }
        assertTrue(
                invalidElementIds.isEmpty(),
                "Trovati elementId non compatibili con elementsToCheck: " + invalidElementIds
        );
    }

    @Then("raccolgo gli elementId della timeline contenenti {string}")
    public void collectAttempt1ElementIdsFromTimeline(String element) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<TimelineElementV28> timeline = fullSentNotification.getTimeline();
        attempt1ElementIds.clear();
        attempt1ElementIds = timeline.stream()
                .map(TimelineElementV28::getElementId)
                .filter(Objects::nonNull)
                .filter(id -> id.contains(element))
                .toList();

        if (!attempt1ElementIds.isEmpty()) {
            log.warn("Trovati {} elementId contenenti " + element + ":", attempt1ElementIds.size());
            attempt1ElementIds.forEach(id -> log.warn(" - {}", id));
        } else {
            log.warn("Nessun elementId contenente " + element + " trovato nella timeline");
        }
    }

    @Then("verifica che gli elementi appena raccolti siano nella lista di quelli invalidati")
    public void verifyInvalidatedTimelineElements() {

        List<NotificationStatusHistoryInvalidatedElement> invalidatedHistory = getInvalidatedHistoryFailFast();

        Set<String> invalidatedElementIds = invalidatedHistory.stream()
                .flatMap(h -> h.getRelatedTimelineElements().stream())
                .map(TimelineElementV28::getElementId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<String> missingElementIds = attempt1ElementIds.stream()
                .filter(attemptId -> !invalidatedElementIds.contains(attemptId))
                .toList();

        if (!missingElementIds.isEmpty()) {
            log.error("I seguenti elementId non sono presenti in invalidatedHistory:");
            missingElementIds.forEach(id -> log.error(" - {}", id));
        }
        assertTrue(
                missingElementIds.isEmpty(),
                "Alcuni elementId non sono presenti in invalidatedHistory: " + missingElementIds
        );
    }

    private List<NotificationStatusHistoryInvalidatedElement> getInvalidatedHistoryFailFast() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<TimelineElementV28> timeline = fullSentNotification.getTimeline();
        TimelineElementV28 reworkedElement = timeline.stream()
                .filter(e -> e.getCategory() != null)
                .filter(e -> NOTIFICATION_TIMELINE_REWORKED.equals(e.getCategory().getValue()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Elemento NOTIFICATION_TIMELINE_REWORKED non trovato"));

        List<NotificationStatusHistoryInvalidatedElement> invalidatedHistory = reworkedElement.getDetails().getInvalidatedTimelineAndStatusHistory();

        if (invalidatedHistory.isEmpty()) {
            throw new AssertionError("invalidatedTimelineAndStatusHistory vuota o null");
        }
        return invalidatedHistory;
    }

    @And("si verifica che la richiesta di {timelineInvalidation} effettuata sia in stato {string} entro {int} secondi controllando ogni {int} secondi")
    public void verifyReworkStatusById(String requestType, String expectedStatus, int timeoutSeconds, int pollIntervalSeconds) {
        String iun = sharedSteps.getNotificationIun();
        String reworkId = resolveReworkId(requestType);
        if (reworkId == null) {
            throw new AssertionError("ReworkId nullo | IUN=" + iun);
        }
        AtomicReference<List<String>> lastFoundStatuses = new AtomicReference<>(List.of());
        try {
            await()
                    .atMost(timeoutSeconds, SECONDS)
                    .pollInterval(pollIntervalSeconds, SECONDS)
                    .until(() -> {
                        ReworkItemsResponse response = reworkTimelineClient.retrieveNotificationReworkById(iun, reworkId);
                        List<String> statuses = response.getItems().stream()
                                .map(item -> item.getStatus().getValue())
                                .toList();
                        lastFoundStatuses.set(statuses);
                        log.info("Polling rework | IUN={} | reworkId={} | stati trovati={}", iun, reworkId, statuses);

                        int expectedStatusValue = reworkStatusValueMap.get(ReworkItem.StatusEnum.valueOf(expectedStatus));
                        int errorStatusValue = reworkStatusValueMap.get(ReworkItem.StatusEnum.ERROR);
                        List<Integer> actualStatusValues = statuses.stream().map(x -> reworkStatusValueMap.get(ReworkItem.StatusEnum.valueOf(x))).toList();
                        Integer actualStatusValue;
                        if (expectedStatus.equals("ERROR")) {
                            actualStatusValue = actualStatusValues.stream().filter(s -> s == errorStatusValue).findFirst().orElse(null);
                        } else {
                            actualStatusValue = actualStatusValues.stream().filter(s -> s >= expectedStatusValue && s < errorStatusValue).findFirst().orElse(null);
                        }
                        return actualStatusValue != null;
                    });
            checkRequestType(requestType);
        } catch (ConditionTimeoutException e) {
            throw new RuntimeException(
                    String.format(
                            "Timeout in attesa dello stato rework | IUN=%s | reworkId=%s | atteso=%s | ultimi stati trovati=%s",
                            iun,
                            reworkId,
                            expectedStatus,
                            lastFoundStatuses.get()
                    ),
                    e
            );
        }
    }

    @Then("viene invocato il consolidatore con i seguenti dati:")
    public void invokeConsolidatorCustomFromDataTable(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        invokeConsolidatorCustomFromMap(inputData);
    }

    private void invokeConsolidatorCustomFromMap(Map<String, String> map) {
        Map<String, Object> mapInfo = populateConsolidatoreMapCustom(map);
        String body = Assertions.assertDoesNotThrow(
                () -> sharedSteps.getPnExternalServiceClient()
                        .pushConsolidatoreNotificationAttach(mapInfo),
                () -> String.format(
                        "Chiamata al consolidatore fallita | IUN=%s",
                        mapInfo.get("iun")
                )
        );
        Assertions.assertFalse(
                body.contains("\"resultCode\":\"500") || body.contains("\"resultCode\":\"403"),
                () -> String.format(
                        "Errore applicativo dal consolidatore | IUN=%s | body=%s",
                        mapInfo.get("iun"),
                        body
                )
        );
    }

    private String getOrInitNow() {
        if (timestampString == null) {
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).withNano(0);
            timestampString = now.format(DateTimeFormatter.ISO_INSTANT);
        }
        return timestampString;
    }

    private Map<String, Object> populateConsolidatoreMapCustom(Map<String, String> inputData) {
        String rawTimestamp = inputData.getOrDefault("timestamp", getOrInitNow());
        String validateTimestamp = rawTimestamp.equals("<null>") ? null : rawTimestamp;
        String attachmentUri = ConsolidatoreRequestBuilder.attachmentUriFor(B2bUtils.getEnvironment(sharedSteps.getContext()));
        return ConsolidatoreRequestBuilder.buildConsolidatoreMap(
                sharedSteps.getNotificationIun(), inputData, validateTimestamp, attachmentUri);
    }

    private void checkRequestType(String requestType) {
        QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.NOTIFICATION_REWORKS, Map.of(
                ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build()
        ));
        try {
            assertThat(queryResponse.items().size()).as(B2bUtils.assertWithIun(sharedSteps.getNotificationIun(), "Nessun record trovato in NOTIFICATION_REWORKS per lo IUN")).isGreaterThan(0);
            assertThat(queryResponse.items().get(0).get("requestType").s())
                    .as(B2bUtils.assertWithIun(sharedSteps.getNotificationIun(), "Il requestType non coincide con quanto atteso"))
                    .isEqualTo(requestType);
            log.info("NOTIFICATION_REWORKS RESPONSE -> {}", queryResponse);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("controllo che su pn-ReworkedTimelinesForInvoicing i seguenti elementi di timeline risultino in stato {invoicingType}")
    public void checkReworkedTimelinesForInvoicing(String invoicingType, Map<String, String> expectedElements) {
        FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
        boolean isViewed = fsn.getNotificationStatus().getValue().equals(NOTIFICATION_STATUS_VIEWED);
        String paId = fsn.getSenderPaId();
        String sentAt = fsn.getSentAt().toLocalDate().toString();
        String iun = fsn.getIun();
        String pk = paId + "_" + sentAt;

        try {
            await().atMost(15, TimeUnit.MINUTES).pollInterval(30, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
                reworkedTimelinesForInvoicingResponse = dynamoDbService.call(DynamoTableName.REWORKED_TIMELINES_FOR_INVOICING, Map.of(
                        ":v_paId_invoicingDay", AttributeValue.builder().s(pk).build(),
                        ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build()
                ));
                log.info("REWORKED_TIMELINES_FOR_INVOICING RESPONSE -> {}", reworkedTimelinesForInvoicingResponse);
                assertThat(reworkedTimelinesForInvoicingResponse).as("Il risultato della query su pn-ReworkedTimelinesForInvoicing non dev'essere null. IUN = %s", iun).isNotNull();
                List<Map<String, AttributeValue>> records = reworkedTimelinesForInvoicingResponse.items().stream().filter(
                        e -> e.containsKey("invoicingType") && e.get("invoicingType").s().equals(invoicingType)).toList();
                log.info("Filtered records -> {}", records);

                if (isViewed && invoicingType.equals("NEW")) {
                    assertThat(records.size()).as("In caso di notifica visualizzata, non dovrebbero esserci elementi con invoicingType NEW. IUN = %s", iun).isEqualTo(0);
                } else {
                    expectedElements.forEach((key, value) -> {
                        String[] requirements = value.split(";");
                        Map<String, AttributeValue> expectedFound = records.stream()
                                .filter(r -> r.containsKey("invoincingTimestamp_timelineElementId")
                                        && Arrays.stream(requirements).allMatch(r.get("invoincingTimestamp_timelineElementId").s()::contains))
                                .findFirst().orElse(null);
                        assertThat(expectedFound)
                                .as("Non è stato trovato nessun record che nel timelineElement id abbia tutte le sottostringhe attese: %s . IUN = %s", Arrays.toString(requirements), iun)
                                .isNotNull();
                    });
                }
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("verifico la che il reworkId del {timelineInvalidation} generato sia corretto, con rework {int} try {int} e recIndex {int}")
    public void checkReworkId(String requestType, int reworkIndex, int tryIndex, int recIndex) {
        String reworkId = resolveReworkId(requestType);
        try {
            assertSoftly(softly -> {
                softly.assertThat(reworkId).as("Il rework id dovrebbe avere rework index pari a %s", reworkIndex).contains("REWORK_" + reworkIndex);
                softly.assertThat(reworkId).as("Il rework id dovrebbe avere try index pari a %s", tryIndex).contains("TRY_" + tryIndex);
                softly.assertThat(reworkId).as("Il rework id dovrebbe avere rec index pari a %s", recIndex).contains("RECINDEX_" + recIndex);
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @When("invoco l'api di external-registry per l'invalidazione dei costi con {string}")
    public void testExternalRegistryApi(String inputParams) {
        FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
        String iun = fsn.getIun();
        String creditorTaxId = fsn.getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId();
        String noticeCode = fsn.getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode();
        PaymentsInfo paymentsInfo = new PaymentsInfo();
        paymentsInfo.setCreditorTaxId(creditorTaxId);
        paymentsInfo.setNoticeCode(noticeCode);
        paymentsInfo.setRecIndex(0);

        PaperCostToInvalidate request = new PaperCostToInvalidate();
        request.setVat(20);
        request.setCostPhases(List.of(AnalogUpdateCostPhase._0));
        request.setPaymentsInfo(List.of(paymentsInfo));
        switch (inputParams) {
            case "iun null" -> iun = null;
            case "iun non valido" -> iun = INVALID_IUN;
            case "iun inesistente" -> iun = INEXISTENT_IUN;
            case "vat null" -> request.setVat(null);
            case "vat non valido" -> request.setVat(-20);
            case "costPhases null" -> request.setCostPhases(null);
            case "paymentsInfo null" -> request.setPaymentsInfo(null);
        }
        log.info("external-registry invalidatePaperCost request: IUN: {}, request-body: {}", iun, request);
        try {
            externalRegistryPrivateUserApi.invalidatePaperCost(iun, request);
        } catch (HttpStatusCodeException exception) {
            httpStatusCode = exception.getStatusCode();
        }
    }

    @When("invoco l'api di notification-cost per l'invalidazione dei costi con {string}")
    public void testNotificationCostApi(String inputParams) {
        String iun = sharedSteps.getNotificationIun();
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.PaperCostToInvalidate request =
                new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.PaperCostToInvalidate();
        request.setRecIndex("RECINDEX_0");
        request.setCostPhases(List.of(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.AnalogUpdateCostPhase._0));
        switch (inputParams) {
            case "iun null" -> iun = null;
            case "iun non valido" -> iun = INVALID_IUN;
            case "iun inesistente" -> iun = INEXISTENT_IUN;
            case "recIndex null" -> request.setRecIndex(null);
            case "recIndex non presente" -> request.setRecIndex("RECINDEX_1");
            case "recIndex non valido" -> request.setRecIndex("1");
            case "costPhases null" -> request.setCostPhases(null);
        }
        log.info("notification-cost invalidatePaperCost request: IUN: {}, request-body: {}", iun, request);
        try {
            notificationCostClient.invalidatePaperCost(iun, request);
        } catch (HttpStatusCodeException exception) {
            httpStatusCode = exception.getStatusCode();
        }
    }

    @Then("si controlla che il timestamp dell'elemento NOTIFICATION_TIMELINE_REWORKED coincida con quello presente su DynamoDb, basato sulla SEND_ANALOG_DOMICILE all'attempt {int}")
    public void checkNotificationTimelineReworkTimestamp(int attempt) {
        try {
            FullSentNotificationV29 fsn = sharedSteps.getSentNotificationLastVersion();
            TimelineElementV28 notificationTimelineReworked = fsn.getTimeline().stream().filter(
                    e -> e.getCategory().getValue().equals(NOTIFICATION_TIMELINE_REWORKED)).findFirst().orElse(null);
            assertThat(notificationTimelineReworked).as("Element NOTIFICATION_TIMELINE_REWORKED can't be null").isNotNull();
            NotificationStatusHistoryInvalidatedElement invalidatedDeliveringElements = notificationTimelineReworked.getDetails().getInvalidatedTimelineAndStatusHistory().stream().filter(
                    inv -> inv.getStatus().equals(NotificationStatusV26.DELIVERING)).findFirst().orElse(null);
            TimelineElementV28 sendAnalogDomicileAttemptX = invalidatedDeliveringElements.getRelatedTimelineElements().stream().filter(
                    e -> e.getCategory().getValue().equals(SEND_ANALOG_DOMICILE) && e.getElementId().contains("ATTEMPT_" + attempt)).findFirst().orElse(null);
            assertThat(notificationTimelineReworked).as("Element SEND_ANALOG_DOMICILE with attempt %s can't be null", attempt).isNotNull();
            assertThat(notificationTimelineReworked.getEventTimestamp())
                    .as("The event timestamp of NOTIFICATION_TIMELINE_REWORKED does not match with the one of the invalidated SEND_ANALOG_DOMICILE at attempt %s", attempt)
                    .isEqualTo(sendAnalogDomicileAttemptX.getEventTimestamp());

            QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                    ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                    ":v_category", AttributeValue.builder().s(NOTIFICATION_TIMELINE_REWORKED).build()
            ));
            assertThat(queryResponse.items().size()).as("Query on pn-timelines did not find any NOTIFICATION_TIMELINE_REWORKED element").isGreaterThan(0);
            Map<String, AttributeValue> dynamoNotificationTimelineReworked = queryResponse.items().get(0);
            assertThat(dynamoNotificationTimelineReworked.containsKey("businessTimestamp")).isTrue();
            assertThat(dynamoNotificationTimelineReworked.get("businessTimestamp").s()).isNotNull();
            OffsetDateTime dynamoEventTimestamp = OffsetDateTime.parse(dynamoNotificationTimelineReworked.get("businessTimestamp").s());
            assertThat(notificationTimelineReworked.getEventTimestamp())
                    .as("The event timestamp of NOTIFICATION_TIMELINE_REWORKED returned by fullSentNotification does not match with the one on DynamoDb")
                    .isEqualTo(dynamoEventTimestamp);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }
}