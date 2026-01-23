package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItemsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryInvalidatedElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.service.impl.ReworkTimelineClientImpl;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.utilitySteps.Costanti;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class TimelineReworkSteps {

    @Value("${pn.external.allowed.future.offset.duration}")
    private String pnEcConsAllowedFutureOffsetDuration;

    private final ReworkTimelineClientImpl reworkTimelineClient;
    private final SharedSteps sharedSteps;

    private ReworkResponse reworkResponse;
    private HttpStatus httpStatusCode;

    public TimelineReworkSteps(ReworkTimelineClientImpl reworkTimelineClient, SharedSteps sharedSteps) {
        this.reworkTimelineClient = reworkTimelineClient;
        this.sharedSteps = sharedSteps;
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

    @And("viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:")
    public void callReworkWithParams(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String attemptId = getParams(inputData, "attemptId", "ATTEMPT_0");
        try {
            reworkTimelineClient.notificationRework(getParams(inputData, "iun", sharedSteps.getNotificationIun()),
                    createRequestRework(
                            attemptId != null ? ReworkRequest.AttemptIdEnum.fromValue(attemptId) : null,
                            getParams(inputData, "reason", "reason"),
                            getParams(inputData, "pcRetry", "PCRETRY_0"),
                            getParams(inputData, "recIndex", "RECINDEX_0"),
                            getParams(inputData, "expectedStatusCode", "RECRI003C"),
                            getParams(inputData, "expectedDeliveryFailureCause", null)
                    ));
        } catch (HttpStatusCodeException exception) {
            httpStatusCode = exception.getStatusCode();
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
        FullSentNotificationV28 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
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

        // Stream flat + raccolta elementId NON validi
        List<String> invalidElementIds = invalidatedHistory.stream()
                .flatMap(h -> h.getRelatedTimelineElements().stream())
                .map(TimelineElementV28::getElementId)
                .filter(Objects::nonNull)
                .filter(elementId ->
                        elementsToCheck.stream()
                                .noneMatch(elementId::contains)
                )
                .toList();

        // Log di TUTTI i non validi
        if (!invalidElementIds.isEmpty()) {
            log.error("Trovati elementId non validi in relatedTimelineElements:");
            invalidElementIds.forEach(id ->
                    log.error(" - {}", id)
            );
        }

        // Fail-fast finale
        Assertions.assertTrue(
                invalidElementIds.isEmpty(),
                "Trovati elementId non compatibili con elementsToCheck: " + invalidElementIds
        );
    }

    @And("si verifica che la richiesta di rework effettuata sia in stato {string} entro {int} secondi controllando ogni {int} secondi")
    public void verifyReworkStatusById(String status, int timeoutSeconds, int pollIntervalSeconds) {

        await()
                .atMost(timeoutSeconds, SECONDS)
                .pollInterval(pollIntervalSeconds, SECONDS)
                .until(() -> {
                    ReworkItemsResponse reworkItemsResponse = reworkTimelineClient
                            .retrieveNotificationReworkById(sharedSteps.getNotificationIun(), reworkResponse.getReworkId());

                    return reworkItemsResponse.getItems().stream()
                            .anyMatch(reworkItem -> reworkItem.getStatus() == ReworkItem.StatusEnum.fromValue(status));
                });
    }

    @Then("viene invocato il consolidatore con i seguenti dati:")
    public void vieneInvocatoIlConsolidatoreCustom(DataTable params) {

        Map<String, String> inputData = params.asMaps().get(0);
        Map<String, String> mapInfo = populateConsolidatoreMapCustom(inputData);
        try {
            sharedSteps
                    .getPnExternalServiceClient()
                    .pushConsolidatoreNotification(mapInfo);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    private Map<String, String> populateConsolidatoreMapCustom(
            Map<String, String> inputData) {

        String iun = sharedSteps.getNotificationIun();

        Instant now = Instant.now().plusSeconds(3600);

        Map<String, String> mapInfo = new HashMap<>();

        mapInfo.put("requestId", buildRequestId(
                        iun,
                        inputData.get("recIndex"),
                        inputData.get("attemptId"),
                        inputData.get("pcRetry")
                )
        );

//        if (inputData.get("attachment") != null) {
//            mapInfo.put("attachments", buildSingleAttachment(inputData.get("attachment"), now));
//        } else {
//            mapInfo.put("attachments", null);
//        }

        // attachments (multi)
        String attachmentsJson = buildAttachmentsFromInput(inputData, now);
        mapInfo.put("attachments", attachmentsJson);

        mapInfo.put("clientRequestTimeStamp", B2bUtils.getOffsetDateTimeFromDate(now));
        mapInfo.put("deliveryFailureCause", inputData.getOrDefault("deliveryFailureCause", null));
        mapInfo.put("discoveredAddress", null);
        mapInfo.put("iun", iun);
        mapInfo.put("productType", inputData.getOrDefault("productType", null));
        mapInfo.put("registeredLetterCode", null);
        mapInfo.put("statusCode", inputData.getOrDefault("statusCode", null));
        mapInfo.put("statusDateTime", B2bUtils.getOffsetDateTimeFromDate(now));
        mapInfo.put("statusDescription", "QA");

        return mapInfo;
    }


    private String buildAttachmentsFromInput(
            Map<String, String> inputData,
            Instant date) {

        List<String> documentTypes = inputData.entrySet().stream()
                .filter(e -> e.getKey().startsWith("attachment_"))
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .filter(v -> !v.isBlank())
                .toList();

        if (documentTypes.isEmpty()) {
            return null;
        }
        String attachmentsBody = documentTypes.stream()
                .map(dt -> buildSingleAttachment(dt, date))
                .collect(Collectors.joining(","));

        return "[ " + attachmentsBody + " ]";
    }

    private String buildRequestId(String iun, String recindex, String attempt, String pcRetry) {
        return String.format(
                "PREPARE_ANALOG_DOMICILE.IUN_%s.%s.%s.%s",
                iun, recindex, attempt, pcRetry
        );
    }
    private String buildSingleAttachment(String documentType, Instant date) {

        return """
      {
        "id": "1",
        "documentType": "%s",
        "uri": "safestorage://PN_EXTERNAL_LEGAL_FACTS-243648ce692946f987b86fb72b33d98a.pdf",
        "sha256": "UaMdYj7cAVO6EZTC9ddUBD7pbkG6zdEZ0LaL/3cmphU=",
        "date": "%s"
      }
      """.formatted(
                documentType,
                B2bUtils.getOffsetDateTimeFromDate(date)
        );
    }
}
