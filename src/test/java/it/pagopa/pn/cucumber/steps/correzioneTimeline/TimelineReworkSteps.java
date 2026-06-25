package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItemsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.UpdateReworkRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryInvalidatedElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.service.impl.ReworkTimelineClientImpl;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.utilitySteps.Costanti;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class TimelineReworkSteps {

    @Before("@timelineReworkF2 or @timelineRework")
    public void beforeMethod() {
        this.resetTimestamp();
    }

    private final ReworkTimelineClientImpl reworkTimelineClient;
    private final SharedSteps sharedSteps;

    private ReworkResponse reworkResponse;
    private HttpStatus httpStatusCode;

    private String timestampString;
    private List<String> attempt1ElementIds = new ArrayList<>();

    public TimelineReworkSteps(ReworkTimelineClientImpl reworkTimelineClient, SharedSteps sharedSteps) {
        this.reworkTimelineClient = reworkTimelineClient;
        this.sharedSteps = sharedSteps;
    }

    @And("viene resettato il timestamp")
    public void resetTimestamp() {
        this.timestampString = null;
    }

    @And("viene aggiornata la richiesta di rework con i seguenti dati:")
    public void updateRequestReworkWithError(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String iun = getParams(inputData, "iun", sharedSteps.getNotificationIun());
        String reworkId = getParams(inputData, "reworkId", reworkResponse != null ? reworkResponse.getReworkId() : "REWORK_0.TRY_0.RECINDEX_0");
        UpdateReworkRequest updateReworkRequest =
                createUpdateReworkRequest(
                        getParams(inputData, "expectedStatusCode", null),
                        getParams(inputData, "expectedDeliveryFailureCause", null)
                );
        try {
            reworkTimelineClient.updateNotificationRework(iun, reworkId, updateReworkRequest);
        } catch (HttpStatusCodeException exception) {
            httpStatusCode = exception.getStatusCode();
        }
    }

    @And("viene correttamente aggiornata la richiesta di rework con i seguenti dati:")
    public void updateRequestRework(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String iun = getParams(inputData, "iun", sharedSteps.getNotificationIun());
        String reworkId = getParams(inputData, "reworkId", reworkResponse != null ? reworkResponse.getReworkId() : null);
        if (reworkId == null) {
            throw new AssertionError("reworkId nullo | IUN=" + iun);
        }

        UpdateReworkRequest updateReworkRequest =
                createUpdateReworkRequest(
                        getParams(inputData, "expectedStatusCode", null),
                        getParams(inputData, "expectedDeliveryFailureCause", null)
                );
        Assertions.assertDoesNotThrow(
                () -> reworkTimelineClient.updateNotificationRework(iun, reworkId, updateReworkRequest),
                () -> String.format(
                        "Errore aggiornamento rework | IUN=%s | reworkId=%s | request=%s",
                        iun,
                        reworkId,
                        updateReworkRequest
                )
        );
    }

    private UpdateReworkRequest createUpdateReworkRequest(String expectedStatusCode, String expectedDeliveryFailureCause) {
        UpdateReworkRequest request = new UpdateReworkRequest();
        if (expectedStatusCode != null) {
            request.setExpectedStatusCode(expectedStatusCode);
        }
        if (expectedDeliveryFailureCause != null) {
            request.setExpectedDeliveryFailureCause(expectedDeliveryFailureCause);
        }
        return request;
    }

    @And("viene invocata una richiesta di rework per la notifica appena creata")
    public void callReworkTimeline() {
        try {
            reworkResponse = reworkTimelineClient.notificationRework(sharedSteps.getNotificationIun(), createRequestRework());
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
                    throw new RuntimeException("Errore ci sono richieste in sospeso con stato diverso da DONE o ERROR");
                });
    }

    @And("si verifica che la richiesta di rework effettuata sia in stato {string}")
    public void verifyReworkStatusById(String status) {
        ReworkItemsResponse reworkItemsResponse = reworkTimelineClient.retrieveNotificationReworkById(sharedSteps.getNotificationIun(), reworkResponse.getReworkId());
        reworkItemsResponse.getItems().stream()
                .filter(reworkItem -> reworkItem.getStatus() == ReworkItem.StatusEnum.fromValue(status))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Errore la richiesta creata non è nello stato desiderato!"));
    }

    @And("viene invocata una richiesta di rework con eccezione per la notifica appena creata con i seguenti parametri:")
    public void callReworkWithParamsWithError(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String attemptId = getParams(inputData, "attemptId", "ATTEMPT_0");
        try {
            reworkResponse =
                    reworkTimelineClient.notificationRework(
                            getParams(inputData, "iun", sharedSteps.getNotificationIun()),
                            createRequestRework(
                                    attemptId != null
                                            ? ReworkRequest.AttemptIdEnum.fromValue(attemptId)
                                            : null,
                                    getParams(inputData, "reason", "reason"),
                                    getParams(inputData, "pcRetry", "PCRETRY_0"),
                                    getParams(inputData, "recIndex", "RECINDEX_0"),
                                    getParams(inputData, "expectedStatusCode", "RECRI003C"),
                                    getParams(inputData, "expectedDeliveryFailureCause", null)
                            )
                    );
        } catch (HttpStatusCodeException exception) {
            httpStatusCode = exception.getStatusCode();
        }
    }

    @And("viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:")
    public void callReworkWithParams(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String attemptId = getParams(inputData, "attemptId", "ATTEMPT_0");
        try {
            reworkResponse =
                    reworkTimelineClient.notificationRework(
                            getParams(inputData, "iun", sharedSteps.getNotificationIun()),
                            createRequestRework(
                                    attemptId != null
                                            ? ReworkRequest.AttemptIdEnum.fromValue(attemptId)
                                            : null,
                                    getParams(inputData, "reason", "reason"),
                                    getParams(inputData, "pcRetry", "PCRETRY_0"),
                                    getParams(inputData, "recIndex", "RECINDEX_0"),
                                    getParams(inputData, "expectedStatusCode", "RECRI003C"),
                                    getParams(inputData, "expectedDeliveryFailureCause", null)
                            )
                    );
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

    private String getParams(Map<String, String> inputData, String key, String defaultValue) {
        String value = inputData.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.equalsIgnoreCase("EMPTY_STRING") ? null : value;
    }

    private ReworkRequest createRequestRework() {
        return createRequestRework(ReworkRequest.AttemptIdEnum._0, "reason", "PCRETRY_0", "RECINDEX_0", "RECRI003C", null);
    }

    private ReworkRequest createRequestRework(ReworkRequest.AttemptIdEnum attemptId, String reason, String pcRetry,
                                              String recIndex, String expectedStatusCode, String expectedDeliveryFailureCause) {
        ReworkRequest reworkRequest = new ReworkRequest();
        reworkRequest.setAttemptId(attemptId);
        reworkRequest.setExpectedDeliveryFailureCause(expectedDeliveryFailureCause);
        reworkRequest.setReason(reason);
        reworkRequest.setPcRetry(pcRetry);
        reworkRequest.setRecIndex(recIndex);
        reworkRequest.setExpectedStatusCode(expectedStatusCode);
        return reworkRequest;
    }

    @And("si verifica che la chiamata sia andata in errore con il seguente status code: {int}")
    public void verifyErrorResponseStatusCode(int statusCode) {
        Assertions.assertEquals(HttpStatus.resolve(statusCode), httpStatusCode);
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

        List<NotificationStatusHistoryInvalidatedElement> invalidatedHistory =
                getInvalidatedHistoryFailFast();

        List<String> invalidElementIds = invalidatedHistory.stream()
                .flatMap(h -> h.getRelatedTimelineElements().stream())
                .map(TimelineElementV28::getElementId)
                .filter(Objects::nonNull)
                .filter(elementId ->
                        elementsToCheck.stream()
                                .noneMatch(elementId::contains)
                )
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

        List<NotificationStatusHistoryInvalidatedElement> invalidatedHistory =
                getInvalidatedHistoryFailFast();

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
                .filter(e -> "NOTIFICATION_TIMELINE_REWORKED"
                        .equals(e.getCategory().getValue()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Elemento NOTIFICATION_TIMELINE_REWORKED non trovato"));

        List<NotificationStatusHistoryInvalidatedElement> invalidatedHistory =
                reworkedElement.getDetails().getInvalidatedTimelineAndStatusHistory();

        if (invalidatedHistory == null || invalidatedHistory.isEmpty()) {
            throw new AssertionError("invalidatedTimelineAndStatusHistory vuota o null");
        }
        return invalidatedHistory;
    }

    @And("si verifica che la richiesta di rework effettuata sia in stato {string} entro {int} secondi controllando ogni {int} secondi")
    public void verifyReworkStatusById(String expectedStatus, int timeoutSeconds, int pollIntervalSeconds) {

        String iun = sharedSteps.getNotificationIun();
        String reworkId = reworkResponse.getReworkId();
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
                        return statuses.contains(expectedStatus);
                    });
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
    public void vieneInvocatoIlConsolidatoreCustom(DataTable params) {

        Map<String, String> inputData = params.asMaps().get(0);
        Map<String, Object> mapInfo = populateConsolidatoreMapCustom(inputData);
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

        String iun = sharedSteps.getNotificationIun();
        String timestampStringMethod = inputData.getOrDefault("timestamp", getOrInitNow());
        String validateTimestamp = timestampStringMethod.equals("<null>") ? null : timestampStringMethod;
        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put("requestId", buildRequestId(
                iun,
                inputData.get("productType"),
                inputData.get("recIndex"),
                inputData.get("attemptId"),
                inputData.get("pcRetry")
        ));
        if (inputData.get("attachment_1") != null) {
            Map<String, Object> attachment = new HashMap<>();
            attachment.put("id", "1");
            attachment.put("documentType", inputData.get("attachment_1"));
            attachment.put("uri", this.getAttachmentEnvironmentBased());
            attachment.put("sha256", "UaMdYj7cAVO6EZTC9ddUBD7pbkG6zdEZ0LaL/3cmphU=");
            attachment.put("date", validateTimestamp);
            mapInfo.put("attachments", Collections.singletonList(attachment));
        } else {
            mapInfo.put("attachments", null);
        }
        mapInfo.put("clientRequestTimeStamp", validateTimestamp);
        mapInfo.put("deliveryFailureCause", inputData.getOrDefault("deliveryFailureCause", null));
        mapInfo.put("discoveredAddress", null);
        mapInfo.put("iun", iun);
        mapInfo.put("productType", inputData.getOrDefault("productType", null));
        String registeredLetterCode = inputData.getOrDefault("registeredLetterCode", "QATEST");
        mapInfo.put("registeredLetterCode", registeredLetterCode.equals("<null>") ? null : registeredLetterCode);
        mapInfo.put("statusCode", inputData.getOrDefault("statusCode", null));
        mapInfo.put("statusDateTime", validateTimestamp);
        mapInfo.put("statusDescription", "Quality assurance");
        return mapInfo;
    }

    private String getAttachmentEnvironmentBased() {
        String environment = sharedSteps.getContext().getEnvironment().getActiveProfiles()[0];
        return switch (environment) {
            case "dev" -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-970c9a266a3e44fa88ff66f4c3f4e5ae.pdf";
            case "test" -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-243648ce692946f987b86fb72b33d98a.pdf";
            case "uat" -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-dd7dc6811b024202ac66044671f3e2ad.pdf";
            case "hotfix" -> "safestorage://PN_EXTERNAL_LEGAL_FACTS-31ea166ced054f63952e736f04647f0a.pdf";
            default -> throw new IllegalArgumentException("Invalid environment name: " + environment);
        };
    }

    private String buildRequestId(String iun, String productType, String recindex, String attempt, String pcRetry) {
        if (productType.equals("RS") || productType.equals("RIS"))
            return String.format("PREPARE_SIMPLE_REGISTERED_LETTER.IUN_%s.%s.%s", iun, recindex, pcRetry);
        else
            return String.format("PREPARE_ANALOG_DOMICILE.IUN_%s.%s.%s.%s",
                iun, recindex, attempt, pcRetry
        );
    }
}


