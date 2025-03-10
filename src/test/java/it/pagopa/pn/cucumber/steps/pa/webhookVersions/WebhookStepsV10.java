package it.pagopa.pn.cucumber.steps.pa.webhookVersions;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV20;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.*;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
public class WebhookStepsV10 implements WebhookStepsInterface {

    private List<StreamCreationRequest> streamCreationRequestList;
    private List<StreamMetadataResponse> eventStreamList;
    private StreamCreationRequest streamRequest;
    private List<ProgressResponseElement> progressResponseElements;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;
    private final AvanzamentoNotificheWebhookB2bSteps.StreamVersion streamVersion;

    public WebhookStepsV10(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        this.streamVersion = AvanzamentoNotificheWebhookB2bSteps.StreamVersion.V10;
        progressResponseElements = new LinkedList<>();
    }

    //TODO MATTEO UNA MAREA DI METODI ANCORA DA IMPLEMENTARE

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequest = new StreamCreationRequest();
//        List<String> groups = switch (action.toLowerCase()) {
//            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
//                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
//                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
//            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
//            case "stesso" ->
//                    eventStreamList.stream().findFirst().map(StreamMetadataResponse::getGroups).orElse(null);
//            default -> throw new IllegalArgumentException("Action not supported!: " + action);
//        };
//        streamRequest.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestList = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequest streamRequest = new StreamCreationRequest();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @Override
    public Object retrieveStreamEvent(UUID streamId) {
        return this.webhookSteps.getWebhookB2bClient().retrieveEventStream(streamId);
    }

    @Override
    public void deleteStream(UUID streamId) {
        this.webhookSteps.getWebhookB2bClient().deleteEventStream(streamId);
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamList != null) {
            for (StreamMetadataResponse eventStream : eventStreamList) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void cleanWebHookDelete() {
        List<StreamListElement> streamList = AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().listEventStreams();
        for (StreamListElement stream : streamList) {
            try {
                AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().deleteEventStream(stream.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElements = webhookSteps.getWebhookB2bClient().listEventStreams();
        for (StreamListElement elem : streamListElements) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStream(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookSteps.getWebhookB2bClient().retrieveEventStream(streamID);
            webhookSteps.setNotificationError(e);
            webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion  pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequest = new StreamCreationRequest();
        streamRequest.setTitle("Update Stream ");
        streamRequest.setEventType(StreamCreationRequest.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStream(idStream, streamRequest);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStream(idStream, streamRequest);
    }

    @Override
    public void updateStreams() {
        if (streamRequest == null) {
            streamRequest = new StreamCreationRequest();
        }
        streamRequest.setTitle("Update Stream ");
        streamRequest.setEventType(StreamCreationRequest.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponse eventStream : eventStreamList) {
            StreamMetadataResponse result = webhookSteps.getWebhookB2bClient().updateEventStream(eventStream.getStreamId(), streamRequest);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequest.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
//        Assertions.assertFalse(progressResponseElements.stream().map(ProgressResponseElement::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
//        streamRequest = new StreamCreationRequest();
//        streamRequest.setGroups(groupIdByPa);
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
//        if (streamRequest == null) {
//            streamRequest = new StreamRequest();
//        }
//        streamRequest = (StreamRequest) streamRequest;
//        streamRequest.setTitle("Update Stream ");
//        streamRequest.setEventType(StreamRequest.EventTypeEnum.TIMELINE);
//        return streamRequest;
        return null;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElements = webhookSteps.getWebhookB2bClient().listEventStreams();
        for (StreamMetadataResponse eventStream : eventStreamList) {
            StreamListElement streamListElement = streamListElements.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElement);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponse eventStream = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStream(streamId));
        webhookSteps.getSharedSteps().setEventStream(eventStream);
        Assertions.assertNotNull(eventStream);
        Assertions.assertNotNull(eventStream.getStreamId());
        log.info("EVENTSTREAM: {}", eventStream);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElements = webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null);
        log.info("progressResponseElements size: " + progressResponseElements.size());
        log.info("progressResponseElements: " + progressResponseElements);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamList.get(0).getStreamId();
        progressResponseElements = webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null);
        log.info("progressResponseElements: " + progressResponseElements);
        Assertions.assertEquals(progressResponseElements.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElements.size());
    }

    @Override
    public void verifyNoEventsInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamList == null) eventStreamList = new LinkedList<>();
        for (StreamCreationRequest request : streamCreationRequestList) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            StreamMetadataResponse eventStream = webhookSteps.getWebhookB2bClient().createEventStream(request);
            eventStreamList.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStream(UUID streamId) {
        //disabilitazione non prevista dalla V10
    }

    @Override
    public void disableStreams() {
        //disabilitazione non prevista dalla V10
    }

    //TODO MATTEO IMPLEMENTARE
    @Override
    public Object searchTimelineElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        return null;
    }

    //TODO MATTEO TEST
    @Override
    public Object searchStatusElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream) {
        NotificationStatus status = ((NotificationStatus) statusForStream.getNotificationStatus());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(status);
        PnPollingServiceWebhookV20 webhook = (PnPollingServiceWebhookV20) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V20);
        PnPollingResponseV20 pnPollingResponse = webhook.waitForEvent(webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V20: " + pnPollingResponse.getProgressResponseElementV20());
        if (pnPollingResponse.getProgressResponseElementV20() != null) {
            webhookSteps.getSharedSteps().setProgressResponseElements(pnPollingResponse.getProgressResponseElementListV20());
            return pnPollingResponse.getProgressResponseElementV20();
        }
        return null;
    }

    //TODO MATTEO IMPLEMENTARE
    @Override
    public boolean checkTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        return false;
    }

    @Override
    public boolean checkStatus(AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream) {
        NotificationStatus notificationInternalStatus = NotificationStatus.valueOf(((NotificationStatus) statusForStream.getNotificationStatus()).name());
        boolean found = false;
        for (int i = 0; i < statusForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(statusForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            //TODO CHECK passaggi a rischio
            webhookSteps.getSharedSteps().setSentNotification(webhookSteps.getB2bClient().getSentNotification(webhookSteps.getSharedSteps().getSentNotification().getIun()));
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = webhookSteps.getSharedSteps().getSentNotification().getNotificationStatusHistory().
                    stream().filter(elem -> elem.getStatus().getValue().equals(notificationInternalStatus.getValue())).findAny().orElse(null);
            if (notificationStatusHistoryElement != null) {
                found = true;
                break;
            }
        }
        return found;
    }

    //TODO MATTEO IMPLEMENTARE
    @Override
    public <T> void verifyAssertionsTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream, T progressResponseElement) {
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotificationV1().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        List<ProgressResponseElement> progressResponseElements = webhookSteps.getSharedSteps().getProgressResponseElements();
        Assertions.assertNotNull(progressResponseElements);
        int lastEventID = 0;
        for (ProgressResponseElement elem : progressResponseElements) {
            int currentEventId = Integer.parseInt(elem.getEventId());
            if (lastEventID != 0 && currentEventId <= lastEventID) {
                Assertions.fail(String.format("EventId is not incremental: %d <= %d", currentEventId, lastEventID));
            }
            lastEventID = currentEventId;
        }
    }

    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T> getTimelineEventForStream(String timelineEventCategory, TimingForPolling.TimingResult timingForElement) {
        AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<TimelineElementCategoryV20> result = new AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<>();
        result.setTimelineElementCategory(TimelineElementCategoryV20.valueOf(timelineEventCategory));
        result.setWaiting(timingForElement.waiting());
        result.setNumCheck(timingForElement.numCheck());
        return (AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T>) result;
    }

    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T> getStatusEventForStream(String notificationStatusName, TimingForPolling.TimingResult timingForElement) {
        AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<NotificationStatus> result = new AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<>();
        result.setNotificationStatus(NotificationStatus.valueOf(notificationStatusName));
        result.setWaiting(timingForElement.waiting());
        result.setNumCheck(timingForElement.numCheck());
        return (AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T>) result;
    }

    @Override
    public <T> PnPollingWebhook getPnPollingWebhook(T timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        if (timeLineOrStatus instanceof TimelineElementCategoryV20) {
            pnPollingWebhook.setTimelineElementCategoryV20((TimelineElementCategoryV20) timeLineOrStatus);
            progressResponseElements.clear();
            pnPollingWebhook.setProgressResponseElementListV20((LinkedList<ProgressResponseElement>) progressResponseElements);

        } else if (timeLineOrStatus instanceof NotificationStatus) {
            pnPollingWebhook.setNotificationStatusV20((NotificationStatus) timeLineOrStatus);
            progressResponseElements.clear();
            pnPollingWebhook.setProgressResponseElementListV20((LinkedList<ProgressResponseElement>) progressResponseElements);

        }
        return pnPollingWebhook;
    }

    @Override
    public void getTimelineElementVersionB2B(String iun) {
        FullSentNotificationV20 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV2(iun);
        webhookSteps.getSharedSteps().setNotificationResponseCompleteV20(fullSentNotification);
    }

    @Override
    public void compareTimestampWebhook(String timelineElementCategory, String webhookElementCategory, boolean mustBeEqual) {
        Assertions.assertNotNull(webhookSteps.getSharedSteps().getProgressResponseElements());
        OffsetDateTime eventTimestamp = webhookSteps.getSharedSteps().getProgressResponseElements().stream().filter(
                elem -> elem.getTimelineEventCategory().getValue().equals(webhookElementCategory)).findAny().get().getTimestamp();
        OffsetDateTime notificationTimestamp = webhookSteps.getSharedSteps().getSentNotification().getTimeline().stream().filter(
                elem -> elem.getCategory().getValue().equals(timelineElementCategory)).findAny().get().getDetails().getSchedulingDate();
        log.info("event timestamp : {}", eventTimestamp);
        log.info("notification timestamp : {}", notificationTimestamp);
        if (mustBeEqual) {
            Assertions.assertEquals(eventTimestamp, notificationTimestamp);
        } else {
            Assertions.assertNotEquals(eventTimestamp, notificationTimestamp);
        }
    }

    @Override
    public void getStreamEventListForStressTest() {
        for (StreamMetadataResponse stream : eventStreamList) {
            UUID streamId = stream.getStreamId();
            List<ProgressResponseElement> progressResponseElements = webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null);
            System.out.println("progressResponseElements V10 size: " + progressResponseElements.size());
            webhookSteps.sleepTest(50L);
        }
    }
}
