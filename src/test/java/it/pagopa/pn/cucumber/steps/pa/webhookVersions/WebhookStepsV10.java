package it.pagopa.pn.cucumber.steps.pa.webhookVersions;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementV20;
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

    private ProgressResponseElement progressResponseElement;
    private List<ProgressResponseElement> progressResponseElementList;
    private List<StreamCreationRequest> streamCreationRequestList;
    private List<StreamMetadataResponse> eventStreamList;
    private StreamCreationRequest streamRequest;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;
    private final AvanzamentoNotificheWebhookB2bSteps.StreamVersion streamVersion;

    public WebhookStepsV10(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        this.streamVersion = AvanzamentoNotificheWebhookB2bSteps.StreamVersion.V10;
        progressResponseElementList = new LinkedList<>();
    }

    //TODO MATTEO: controllare per possibili metodi non implementati

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequest = new StreamCreationRequest();
        //GRUPPI INTRODOTTI DALLA V23
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
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        //GRUPPI INTRODOTTI DALLA V23
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
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null);
        log.info("progressResponseElements size: " + progressResponseElementList.size());
        log.info("progressResponseElements: " + progressResponseElementList);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamList.get(0).getStreamId();
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null);
        log.info("progressResponseElements: " + progressResponseElementList);
        Assertions.assertEquals(progressResponseElementList.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementList.size());
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
        //DISABILITAZIONE PREVISTA DALLA V23
    }

    @Override
    public void disableStreams() {
        //DISABILITAZIONE PREVISTA DALLA V23
    }

    @Override
    public Object searchTimelineElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV20 timeLineOrStatus = ((TimelineElementCategoryV20) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
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
            progressResponseElement = pnPollingResponse.getProgressResponseElementV20();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementListV20();
            return progressResponseElement;
        }
        return null;
    }

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
            progressResponseElement = pnPollingResponse.getProgressResponseElementV20();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementListV20();
            return progressResponseElement;
        }
        return null;
    }

    @Override
    public boolean checkTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV20 timelineElementInternalCategory = TimelineElementCategoryV20.valueOf(((TimelineElementCategoryV20) timelineForStream.getTimelineElementCategory()).name());
        boolean finish = false;
        for (int i = 0; i < timelineForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            webhookSteps.getSharedSteps().setSentNotificationV2(webhookSteps.getB2bClient().getSentNotificationV2(webhookSteps.getSharedSteps().getSentNotification().getIun()));
            TimelineElementV20 timelineElement = webhookSteps.getSharedSteps()
                    .getSentNotificationV2().getTimeline().stream()
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
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
        NotificationStatus notificationInternalStatus = NotificationStatus.valueOf(((NotificationStatus) statusForStream.getNotificationStatus()).name());
        boolean found = false;
        for (int i = 0; i < statusForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(statusForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            webhookSteps.getSharedSteps().setSentNotificationV1(webhookSteps.getB2bClient().getSentNotificationV1(webhookSteps.getSharedSteps().getSentNotification().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = webhookSteps.getSharedSteps().getSentNotificationV1().getNotificationStatusHistory().
                    stream().filter(elem -> elem.getStatus().getValue().equals(notificationInternalStatus.getValue())).findAny().orElse(null);
            if (notificationStatusHistoryElement != null) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public <T> void verifyAssertionsTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream, T progressResponseElement) {
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //WAIT FOR ACCEPTED PREVISTO DALLA V27
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotificationV1().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        Assertions.assertNotNull(progressResponseElementList);
        int lastEventID = 0;
        for (ProgressResponseElement elem : progressResponseElementList) {
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
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV20(progressResponseElementList);

        } else if (timeLineOrStatus instanceof NotificationStatus) {
            pnPollingWebhook.setNotificationStatusV20((NotificationStatus) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV20(progressResponseElementList);

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
        Assertions.assertNotNull(progressResponseElementList);
        OffsetDateTime eventTimestamp = progressResponseElementList.stream().filter(
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

    @Override
    public void verificaDeanonimizzazioneEventiTimelineAnalogica(boolean delega) {
        //non previsto per la V10
    }

    @Override
    public void verificaDeanonimizzazioneEventiTimelineDigitale(boolean delega) {
        //non previsto per la V10
    }

    @Override
    public void setProgressResponseElement(Object progressResponseElement) {
        this.progressResponseElement = (ProgressResponseElement) progressResponseElement;
    }

    @Override
    public List<Object> verificaCorrispondenzaElementiTimelineWebhookAndB2B() {
        // non previsto per la V10
        return null;
    }

    @Override
    public void checkLegalFactId() {
        Assertions.assertNotNull(progressResponseElement);
        Assertions.assertNotNull(progressResponseElement.getLegalfactIds());
        Assertions.assertFalse(progressResponseElement.getLegalfactIds().isEmpty());
    }
}
