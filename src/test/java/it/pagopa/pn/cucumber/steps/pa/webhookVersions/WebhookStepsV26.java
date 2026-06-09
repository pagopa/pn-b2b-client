package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV26;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceWebhookV26;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Data
@Slf4j
public class WebhookStepsV26 implements WebhookStepsInterface {

    private ResponseEntity<List<ProgressResponseElementV26>> consumeResponseWithHttpInfo;
    private ProgressResponseElementV26 progressResponseElement;
    private List<ProgressResponseElementV26> progressResponseElementList;
    private List<StreamCreationRequestV26> streamCreationRequestList;
    private List<StreamMetadataResponseV26> eventStreamList;
    private StreamRequestV26 streamRequest;
    private final AvanzamentoNotificheWebhookB2bSteps webhookSteps;
    private final IPnWebhookB2bClient webhookClient;
    private final SharedSteps sharedSteps;
    private final IPnPaB2bClient b2bClient;
    private final StreamVersion streamVersion;

    public WebhookStepsV26(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        webhookClient = webhookSteps.getWebhookB2bClient();
        sharedSteps = webhookSteps.getSharedSteps();
        b2bClient = webhookSteps.getB2bClient();
        streamVersion = StreamVersion.V26;
        progressResponseElementList = new LinkedList<>();
    }

    @Override
    public Object getFullSentNotification() {
        return b2bClient.getSentNotificationV26(sharedSteps.getNotificationIun());
    }

    private FullSentNotificationV26 getFullSentNotificationVersioned() {
        return (FullSentNotificationV26) getFullSentNotification();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequest = new StreamRequestV26();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (sharedSteps.getRequestNewApiKey() != null
                    && sharedSteps.getRequestNewApiKey().getGroups().size() >= 2) ?
                    sharedSteps.getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> sharedSteps.getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamList.stream().findFirst().map(StreamMetadataResponseV26::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequest.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestList = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV26 streamRequest = new StreamCreationRequestV26();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV26.EventTypeEnum.STATUS : StreamCreationRequestV26.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @Override
    public Object retrieveStreamEvent(UUID streamId) {
        return webhookClient.retrieveEventStreamV26(streamId);
    }

    @Override
    public void deleteStream(UUID streamId) {
        webhookClient.deleteEventStreamV26(streamId);
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamList != null) {
            for (StreamMetadataResponseV26 eventStream : eventStreamList) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void cleanWebHookDelete() {
        List<StreamListElement> streamList = AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().listEventStreamsV26();
        for (StreamListElement stream : streamList) {
            try {
                AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().deleteEventStreamV26(stream.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElements = webhookClient.listEventStreamsV26();
        for (StreamListElement elem : streamListElements) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookClient.deleteEventStreamV26(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookClient.retrieveEventStreamV26(streamID);
            webhookSteps.setNotificationError(e);
            sharedSteps.setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion " + streamVersion + " pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequest = new StreamRequestV26();
        streamRequest.setTitle("Update Stream " + streamVersion);
        streamRequest.setEventType(StreamRequestV26.EventTypeEnum.TIMELINE);
        webhookClient.updateEventStreamV26(idStream, streamRequest);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookClient.updateEventStreamV26(idStream, streamRequest);
    }

    @Override
    public void updateStreams() {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV26();
            streamRequest.setGroups(sharedSteps.getRequestNewApiKey().getGroups());
        }
        streamRequest.setTitle("Update Stream " + streamVersion);
        streamRequest.setEventType(StreamRequestV26.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV26 eventStream : eventStreamList) {
            StreamMetadataResponseV26 result = webhookClient.updateEventStreamV26(eventStream.getStreamId(), streamRequest);
            assertThat(result).as("Il risultato dell'operazione di update stream con id " + eventStream.getStreamId() + " non dev'essere null").isNotNull();
            assertThat(result.getTitle()).as("Il titolo dello stream non coincide con quanto atteso").isEqualToIgnoringCase(streamRequest.getTitle());
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void checkIfStreamContains(String type, String timelineCategoryOrStatus, boolean contains) {
        assertThat(progressResponseElementList).as("La lista di progressResponseElement non dev'essere null").isNotNull();
        assertThat(progressResponseElementList).as("La lista di progressResponseElement non dev'essere vuota").isNotEmpty();
        boolean isStatus = type.equals(STREAM_EVENT_TYPE_STATUS);
        progressResponseElement = isStatus ?
                progressResponseElementList.stream().filter(x -> x.getNewStatus().getValue().equals(timelineCategoryOrStatus)).findFirst().orElse(null)
                : progressResponseElementList.stream().filter(x -> x.getElement().getCategory().getValue().equals(timelineCategoryOrStatus)).findFirst().orElse(null);
        String filter = isStatus ? "status" : "category";
        if (contains) {
            assertThat(progressResponseElement)
                    .as("La lista di progressResponseElement dovrebbe contenere un elemento con " + filter + ":" + timelineCategoryOrStatus + logTimelineWebhook()).
                    isNotNull();
        } else {
            assertThat(progressResponseElement)
                    .as("La lista di progressResponseElement NON dovrebbe contenere nessun elemento con " + filter + ":" + timelineCategoryOrStatus + logTimelineWebhook())
                    .isNull();
        }
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequest = new StreamRequestV26();
        streamRequest.setGroups(groupIdByPa);
    }

    @Override
    public UUID getStreamId() {
        return eventStreamList.get(0).getStreamId();
    }

    @Override
    public Object getStreamRequest() {
        return streamRequest;
    }

    @Override
    public Object initStreamRequest(Object streamRequest) {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV26();
        }
        this.streamRequest = (StreamRequestV26) streamRequest;
        this.streamRequest.setTitle("Update Stream " + streamVersion);
        this.streamRequest.setEventType(StreamRequestV26.EventTypeEnum.TIMELINE);
        return this.streamRequest;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamElementList = webhookClient.listEventStreamsV26();
        for (StreamMetadataResponseV26 eventStream : eventStreamList) {
            StreamListElement streamElement = streamElementList.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            assertThat(streamElement).as("Cancellazione stream non andata a buon fine con id " + eventStream.getStreamId()).isNull();
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV26 eventStream = Assertions.assertDoesNotThrow(() -> webhookClient.retrieveEventStreamV26(streamId));
        assertThat(eventStream).as("Nessuno stream trovato con streamId " + streamId).isNotNull();
        assertThat(eventStream.getStreamId())
                .as("Lo streamId dello stream recuperato tramite id " + streamId + " non dev'essere null")
                .isNotNull();
        log.info("EVENTSTREAM: {}", eventStream);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementList = webhookClient.consumeEventStreamV26(streamId, null);
        log.info("progressResponseElements" + streamVersion + " size: " + progressResponseElementList.size());
        log.info("progressResponseElements" + streamVersion + ": " + progressResponseElementList);
    }

    @Override
    public void consumeEventStreamWithHttpInfo(UUID streamId) {
        consumeResponseWithHttpInfo = webhookClient.consumeEventStreamHttpV26(streamId, null);
        assertThat(consumeResponseWithHttpInfo).as("La response http della consume stream non dev'essere null").isNotNull();
        progressResponseElementList = consumeResponseWithHttpInfo.getBody();
        log.info("progressResponseElements" + streamVersion + " size: " + progressResponseElementList.size());
        log.info("progressResponseElements" + streamVersion + ": " + progressResponseElementList);
    }

    @Override
    public void checkHeader(boolean contains, String headerParameterName, String headerParameterValue) {
        assertThat(consumeResponseWithHttpInfo.getHeaders()).as("Gli headers della response non devono essere null").isNotNull();
        List<String> headerParameter = consumeResponseWithHttpInfo.getHeaders().get(headerParameterName);
        if (contains) {
            assertThat(headerParameter).as("L'header dovrebbe contenere il campo " + headerParameterName).isNotNull();
            /*
            Tranne l'header "Vary", tutti gli altri headers sono chiavi aventi come valore una lista di stringhe da un solo elemento.
            Poichè non sono previsti controlli su di esso, prendiamo per buono che l'elemento 0 sia sempre il valore che ci interessa.
            */
            String value = headerParameter.get(0);
            if (headerParameterValue.equalsIgnoreCase("NULL")) {
                assertThat(value).as("Il valore dell'header " + headerParameterName + " dovrebbe essere null").isNull();
            } else if (headerParameterValue.equalsIgnoreCase("NOT-NULL")) {
                assertThat(value).as("Il valore dell'header " + headerParameterName + " dovrebbe essere diverso da null").isNotNull();
            } else {
                assertThat(value).as("Il valore dell'header " + headerParameterName + " non coincide con quanto atteso").isEqualTo(headerParameterValue);
            }
        } else {
            assertThat(headerParameter).as("L'header non dovrebbe contenere il campo " + headerParameterName).isNull();
        }
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamList.get(0).getStreamId();
        progressResponseElementList = webhookClient.consumeEventStreamV26(streamId, null);
        assertThat(progressResponseElementList.size()).as("Il numero di eventi non coincide con quanto atteso").isEqualTo(numEvents);
        log.info("progressResponseElements: " + progressResponseElementList);
        log.info("ELEMENTI NEL WEBHOOK: " + progressResponseElementList.size());
    }

    @Override
    public void verifyNoEventsInStream() {
        UUID streamId = getStreamId();
        assertThat(webhookClient.consumeEventStreamV26(streamId, null)).as("La lista di eventi restituiti dalla consume dovrebbe essere vuota").isEmpty();
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamList == null) eventStreamList = new LinkedList<>();
        for (StreamCreationRequestV26 request : streamCreationRequestList) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (streamIdToReplace != null) {
                request.setReplacedStreamId(streamIdToReplace);
            }
            StreamMetadataResponseV26 eventStream = webhookClient.createEventStreamV26(request);
            if (streamIdToReplace != null) {
                eventStreamList = new LinkedList<>();
            }
            eventStreamList.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStream(UUID streamId) {
        webhookClient.disableEventStreamV26(streamId);
    }

    @Override
    public void disableStreams() {
        eventStreamList.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV26 response = webhookClient.disableEventStreamV26(streamId);
            assertThat(response).as("La response dell'operazione di disabilitazione non dev'essere null").isNotNull();
        });
    }

    @Override
    public Object searchTimelineElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV26 timeLineOrStatus = ((TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV26 webhook = (PnPollingServiceWebhookV26) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V26);
        PnPollingResponseV26 pnPollingResponse = webhook.waitForEvent(sharedSteps.getNotificationIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT " + streamVersion + ": " + pnPollingResponse.getProgressResponseElement());
        if (pnPollingResponse.getProgressResponseElement() != null) {
            progressResponseElement = pnPollingResponse.getProgressResponseElement();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementList();
            return progressResponseElement;
        }
        return null;
    }

    @Override
    public Object searchStatusElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream) {
        NotificationStatusV26 status = ((NotificationStatusV26) statusForStream.getNotificationStatus());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(status);
        PnPollingServiceWebhookV26 webhook = (PnPollingServiceWebhookV26) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V26);
        PnPollingResponseV26 pnPollingResponse = webhook.waitForEvent(sharedSteps.getNotificationIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT " + streamVersion + ": " + pnPollingResponse.getProgressResponseElement());
        if (pnPollingResponse.getProgressResponseElementList() != null) {
            progressResponseElement = pnPollingResponse.getProgressResponseElement();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementList();
            return progressResponseElement;
        }
        return null;
    }

    @Override
    public boolean checkTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV26 timelineElementInternalCategory = TimelineElementCategoryV26.valueOf(((TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory()).name());
        boolean finish = false;
        for (int i = 0; i < timelineForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElement = fullSentNotification.getTimeline().stream().filter(
                            elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);
            if (timelineElement != null) {
                finish = true;
                break;
            }
        }
        return finish;
    }

    @Override
    public boolean checkStatus(AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream) {
        NotificationStatusV26 notificationInternalStatus = NotificationStatusV26.valueOf(((NotificationStatusV26) statusForStream.getNotificationStatus()).name());
        boolean found = false;
        for (int i = 0; i < statusForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(statusForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = fullSentNotification.getNotificationStatusHistory().stream().filter(
                    elem -> elem.getStatus().getValue().equals(notificationInternalStatus.getValue())).findAny().orElse(null);
            if (notificationStatusHistoryElement != null) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public <T> void verifyAssertionsTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream, T progressResponseElement) {
        try {
            assertThat(progressResponseElement).as(NOT_NULL_P_R_E).isNotNull();
            TimelineElementCategoryV26 timelineElementInternalCategory = TimelineElementCategoryV26.valueOf(((TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory()).name());

            FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 elementToCheck = fullSentNotification.getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);
            ProgressResponseElementV26 convertedProgressResponseElement = ((ProgressResponseElementV26) progressResponseElement);
            assertThat(elementToCheck)
                    .as("La ricerca sulla fullSentNotification di elementi con category = " + timelineElementInternalCategory + " deve restituire almeno un elemento")
                    .isNotNull();
            assertThat(elementToCheck.getTimestamp()).as("Il timestamp dell'elemento restituito da b2b non dev'essere null").isNotNull();
            assertThat(convertedProgressResponseElement.getElement()).as(NOT_NULL_P_R_E).isNotNull();
            assertThat(convertedProgressResponseElement.getElement().getTimestamp()).as("Il timestamp del progressResponseElement non dev'essere null").isNotNull();
            assertThat(convertedProgressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS))
                    .as("Il timestamp del progress response element (actual) non coincide con quello dell'elemento restituito da b2b (expected)")
                    .isEqualTo(elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //waitForAccepted introdotto a partire dalla V27
    }

    @Override
    public void verifyIncrementalEventId() {
        assertThat(progressResponseElementList).as("La progressResponseElementList non dev'essere null").isNotNull();
        int lastEventID = 0;
        for (ProgressResponseElementV26 elem : progressResponseElementList) {
            int currentEventId = Integer.parseInt(elem.getEventId());
            if (lastEventID != 0 && currentEventId <= lastEventID) {
                Assertions.fail(String.format("EventId is not incremental: %d <= %d", currentEventId, lastEventID));
            }
            lastEventID = currentEventId;
        }
    }

    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T> getTimelineEventForStream(String timelineEventCategory, TimingForPolling.TimingResult timingForElement) {
        AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<TimelineElementCategoryV26> result = new AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<>();
        result.setTimelineElementCategory(TimelineElementCategoryV26.valueOf(timelineEventCategory));
        result.setWaiting(timingForElement.waiting());
        result.setNumCheck(timingForElement.numCheck());
        return (AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T>) result;
    }

    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T> getStatusEventForStream(String notificationStatusName, TimingForPolling.TimingResult timingForElement) {
        AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<NotificationStatusV26> result = new AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<>();
        result.setNotificationStatus(NotificationStatusV26.valueOf(notificationStatusName));
        result.setWaiting(timingForElement.waiting());
        result.setNumCheck(timingForElement.numCheck());
        return (AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T>) result;
    }

    @Override
    public <T> PnPollingWebhook getPnPollingWebhook(T timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        if (timeLineOrStatus instanceof TimelineElementCategoryV26) {
            pnPollingWebhook.setTimelineElementCategoryV26((TimelineElementCategoryV26) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV26(progressResponseElementList);
        } else if (timeLineOrStatus instanceof NotificationStatusV26) {
            pnPollingWebhook.setNotificationStatusV26((NotificationStatusV26) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV26(progressResponseElementList);
        }
        return pnPollingWebhook;
    }

    @Override
    public void compareTimestampWebhook(String timelineElementCategory, String webhookElementCategory, boolean mustBeEqual) {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        assertThat(progressResponseElementList).as("La lista di progressResponseElements non dev'essere null").isNotNull();
        OffsetDateTime eventTimestamp = progressResponseElementList.stream().filter(
                elem -> elem.getElement().getCategory().getValue().equals(webhookElementCategory)).findAny().get().getElement().getTimestamp();
        OffsetDateTime notificationTimestamp = fullSentNotification.getTimeline().stream().filter(
                elem -> elem.getCategory().getValue().equals(timelineElementCategory)).findAny().get().getDetails().getSchedulingDate();
        log.info("event timestamp : {}", eventTimestamp);
        log.info("notification timestamp : {}", notificationTimestamp);
        if (mustBeEqual) {
            assertThat(notificationTimestamp).as("Il timestamp della notifica non coincide con quello dell'evento").isEqualTo(eventTimestamp);
        } else {
            assertThat(notificationTimestamp).as("Il timestamp della notifica non differisce da quello dell'evento").isNotEqualTo(eventTimestamp);
        }
    }

    @Override
    public void getStreamEventListForStressTest() {
        for (StreamMetadataResponseV26 stream : eventStreamList) {
            UUID streamId = stream.getStreamId();
            List<ProgressResponseElementV26> progressResponseElements = webhookClient.consumeEventStreamV26(streamId, null);
            log.info("progressResponseElements " + streamVersion + " size: " + progressResponseElements.size());
            webhookSteps.sleepTest(50L);
        }
    }

    @Override
    public void verificaDeanonimizzazioneEventiTimeline(boolean isDigitale, boolean withDelega) {
        String root = "DE-ANONIMIZZAZIONE: ";
        assertThat(progressResponseElement).as(root + NOT_NULL_P_R_E).isNotNull();
        TimelineElementDetailsV26 timelineElementWebhookDetails = progressResponseElement.getElement().getDetails();
        assertThat(timelineElementWebhookDetails).as(root + "I details del progressResponseElement non devono essere null").isNotNull();
        if (isDigitale) {
            assertThat(timelineElementWebhookDetails.getDigitalAddress()).as(root + "Il digitalAddress non dev'essere null").isNotNull();
        } else {
            assertThat(timelineElementWebhookDetails.getPhysicalAddress()).as(root + "Il physicalAddress non dev'essere null").isNotNull();
            assertSoftly(softly -> {
                assertThat(timelineElementWebhookDetails.getPhysicalAddress().getAddress()).as(root + "ADDRESS NULL").isNotNull();
                assertThat(timelineElementWebhookDetails.getPhysicalAddress().getMunicipality()).as(root + "MUNICIPALITY NULL").isNotNull();
                assertThat(timelineElementWebhookDetails.getPhysicalAddress().getProvince()).as(root + "PROVINCE NULL").isNotNull();
                assertThat(timelineElementWebhookDetails.getPhysicalAddress().getZip()).as(root + "ZIP NULL").isNotNull();
            });
        }
        if (withDelega) {
            assertSoftly(softly -> {
                assertThat(timelineElementWebhookDetails.getDelegateInfo()).as(root + "DELEGATE INFO NULL").isNotNull();
                assertThat(timelineElementWebhookDetails.getDelegateInfo().getTaxId()).as(root + "DELEGATE INFO TAX_ID NULL").isNotNull();
                assertThat(timelineElementWebhookDetails.getDelegateInfo().getDenomination()).as(root + "DELEGATE INFO DENOMINATION NULL").isNotNull();
            });
        }
    }

    @Override
    public void setProgressResponseElement(Object progressResponseElement) {
        this.progressResponseElement = (ProgressResponseElementV26) progressResponseElement;
    }

    @Override
    public List<Object> verificaCorrispondenzaElementiTimelineWebhookAndB2B() {
        List<Object> resultList = new LinkedList<>();

        TimelineElementV26 teWebhook = progressResponseElement.getElement();
        assertThat(teWebhook).as("L'elemento di timeline recuperato dal webhook non dev'essere null").isNotNull();
        assertThat(teWebhook.getCategory()).as("La category dell'elemento di timeline recuperato dal webhook non dev'essere null").isNotNull();

        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 teB2b = fullSentNotification.getTimeline().
                stream()
                .filter(data -> data.getCategory() != null)
                .filter(data -> data.getCategory().getValue().equalsIgnoreCase(teWebhook.getCategory().getValue()))
                .findFirst()
                .orElse(null);
        assertThat(teB2b).as("Dalla timeline b2b non è stato trovato alcun elemento che corrisponda a quello recuperato dal webhook").isNotNull();

        assertThat(teB2b.getDetails()).as("I details dell'elemento recuperato dalla timeline b2b non devono essere null").isNotNull();
        resultList.add(teB2b.getDetails());

        assertThat(teWebhook.getDetails()).as("I details dell'elemento recuperato dal webhook non devono essere null").isNotNull();
        resultList.add(teWebhook.getDetails());

        return resultList;
    }

    @Override
    public void checkLegalFactId() {
        assertThat(progressResponseElement).as(NOT_NULL_P_R_E).isNotNull();
        assertSoftly(softly -> {
            assertThat(progressResponseElement.getElement().getLegalFactsIds()).as("I legalFactsIds non devono essere null").isNotNull();
            assertThat(progressResponseElement.getElement().getLegalFactsIds()).as("I legalFactsIds non devono essere null").isNotEmpty();
        });
    }

    @Override
    public void checkCorrectDisabling(UUID streamId) {
        StreamMetadataResponseV26 eventStream = webhookClient.retrieveEventStreamV26(streamId);
        assertThat(eventStream).as("Lo stream recuperato tramite id " + streamId + " non dev'essere null").isNotNull();
        assertSoftly(softly -> {
            assertThat(eventStream.getStreamId()).as("L'id dello stream recuperato non dev'essere null").isNotNull();
            assertThat(eventStream.getDisabledDate()).as("La data di disabilitazione dello stream non dev'essere null").isNotNull();
        });
        log.info("EVENTSTREAM REPLACED: {}", eventStream);
    }

    @Override
    public void verificaPresenzaSercQ(boolean isPresent) {
        String channel = isPresent ? "SERCQ" : "PEC";
        Assertions.assertTrue(progressResponseElementList.stream()
                .filter(data -> data.getElement().getElementId() != null)
                .filter(timelineElement -> timelineElement.getElement().getElementId().contains(SEND_DIGITAL_FEEDBACK))
                .allMatch(elementDetails -> "OK".equals(elementDetails.getElement().getDetails().getResponseStatus().toString())
                        && channel.equals(elementDetails.getElement().getDetails().getDigitalAddress().getType())
                ));
    }

    @Override
    public void checkLegalFactCategory(String timelineCategory, String legalFactCategory, boolean arePresent) {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElementWithTargetCategory =
                fullSentNotification.getTimeline().stream().filter(
                        x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);

        assertThat(timelineElementWithTargetCategory)
                .as("La timeline b2b dovrebbe restituire almeno un elemento con categoria " + timelineCategory)
                .isNotNull();
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactsIdV20> elementsWithLegalFactCategory =
                timelineElementWithTargetCategory.getLegalFactsIds().stream().filter(
                        x -> x.getCategory().equals(legalFactCategory)).toList();
        if (arePresent) {
            assertThat(elementsWithLegalFactCategory)
                    .as("La ricerca dovrebbe restituire almeno un elemento di timeline con legalFactCategory " + legalFactCategory)
                    .isNotEmpty();
        } else {
            assertThat(elementsWithLegalFactCategory)
                    .as("La ricerca non dovrebbe restituire nessun elemento di timeline con legalFactCategory " + legalFactCategory)
                    .isEmpty();
        }
    }

    private String logTimelineWebhook() {
        StringBuilder sb = new StringBuilder("\n");
        progressResponseElementList.forEach(x -> {
            sb.append("elementId: ");
            sb.append(x.getElement().getElementId());
            sb.append(" status:");
            sb.append(x.getNewStatus());
            sb.append("\n");
        });
        return sb.toString();
    }
}