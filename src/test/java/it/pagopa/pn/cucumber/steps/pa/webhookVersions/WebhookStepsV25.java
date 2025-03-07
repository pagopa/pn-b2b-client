package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV25;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV25;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
public class WebhookStepsV25 implements WebhookStepsInterface {

    private List<StreamCreationRequestV25> streamCreationRequestListV25;
    private List<StreamMetadataResponseV25> eventStreamListV25;
    private StreamRequestV25 streamRequestV25;
    private List<ProgressResponseElementV25> progressResponseElementsV25;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV25(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        progressResponseElementsV25 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV25 = new StreamRequestV25();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV25.stream().findFirst().map(StreamMetadataResponseV25::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV25.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestListV25 = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV25 streamRequest = new StreamCreationRequestV25();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV25.EventTypeEnum.STATUS : StreamCreationRequestV25.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestListV25.add(streamRequest);
        }
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamListV25 != null) {
            for (StreamMetadataResponseV25 eventStream : eventStreamListV25) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void cleanWebHookDelete() {
        List<StreamListElement> streamList = AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().listEventStreamsV25();
        for (StreamListElement stream : streamList) {
            try {
                AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().deleteEventStreamV25(stream.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElementsV25 = webhookSteps.getWebhookB2bClient().listEventStreamsV25();
        for (StreamListElement elem : streamListElementsV25) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStreamV25(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookSteps.getWebhookB2bClient().retrieveEventStreamV25(streamID);
            webhookSteps.setNotificationError(e);
            webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion V25 pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequestV25 = new StreamRequestV25();
        streamRequestV25.setTitle("Update Stream V25");
        streamRequestV25.setEventType(StreamRequestV25.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV25(idStream, streamRequestV25);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV25(idStream, streamRequestV25);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV25 == null) {
            streamRequestV25 = new StreamRequestV25();
            streamRequestV25.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV25.setTitle("Update Stream V25");
        streamRequestV25.setEventType(StreamRequestV25.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV25 eventStreamV25 : eventStreamListV25) {
            StreamMetadataResponseV25 result = webhookSteps.getWebhookB2bClient().updateEventStreamV25(eventStreamV25.getStreamId(), streamRequestV25);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequestV25.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementsV25.stream().map(ProgressResponseElementV25::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequestV25 = new StreamRequestV25();
        streamRequestV25.setGroups(groupIdByPa);
    }

    @Override
    public UUID getStreamId() {
        return eventStreamListV25.get(0).getStreamId();
    }

    @Override
    public Object getStreamRequest() {
        return streamRequestV25;
    }

    @Override
    public Object initStreamRequest(Object streamRequest) {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV25();
        }
        streamRequestV25 = (StreamRequestV25) streamRequest;
        streamRequestV25.setTitle("Update Stream V25");
        streamRequestV25.setEventType(StreamRequestV25.EventTypeEnum.TIMELINE);
        return streamRequestV25;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV25 = webhookSteps.getWebhookB2bClient().listEventStreamsV25();
        for (StreamMetadataResponseV25 eventStream : eventStreamListV25) {
            StreamListElement streamListElementV25 = streamListElementsV25.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV25);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV25 eventStreamV25 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV25(streamId));
        webhookSteps.getSharedSteps().setEventStreamV25(eventStreamV25);
        Assertions.assertNotNull(eventStreamV25);
        Assertions.assertNotNull(eventStreamV25.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV25);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementsV25 = webhookSteps.getWebhookB2bClient().consumeEventStreamV25(streamId, null);
        log.info("progressResponseElementsV25: " + progressResponseElementsV25);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamListV25.get(0).getStreamId();
        progressResponseElementsV25 = webhookSteps.getWebhookB2bClient().consumeEventStreamV25(streamId, null);
        log.info("progressResponseElementsV25: " + progressResponseElementsV25);
        Assertions.assertEquals(progressResponseElementsV25.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV25.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV25(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamListV25 == null) eventStreamListV25 = new LinkedList<>();
        for (StreamCreationRequestV25 request : streamCreationRequestListV25) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (streamIdToReplace != null) {
                request.setReplacedStreamId(streamIdToReplace);
            }
            StreamMetadataResponseV25 eventStream = webhookSteps.getWebhookB2bClient().createEventStreamV25(request);
            if (streamIdToReplace != null) {
                StreamMetadataResponseV25 eventStreamV25 =
                        webhookSteps.getWebhookB2bClient().retrieveEventStreamV25(streamIdToReplace);
                webhookSteps.getSharedSteps().setEventStreamV25(eventStreamV25);
                Assertions.assertNotNull(eventStreamV25);
                Assertions.assertNotNull(eventStreamV25.getStreamId());
                Assertions.assertNotNull(eventStreamV25.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV25);
                eventStreamListV25 = new LinkedList<>();
            }
            eventStreamListV25.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        eventStreamListV25.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV25 response = webhookSteps.getWebhookB2bClient().disableEventStreamV25(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timeLineOrStatus = ((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV25 webhookV25 = (PnPollingServiceWebhookV25) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V25);
        PnPollingResponseV25 pnPollingResponseV25 = webhookV25.waitForEvent(webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV25.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V25: " + pnPollingResponseV25.getProgressResponseElementV25());
        if (pnPollingResponseV25.getProgressResponseElementListV25() != null) {
            webhookSteps.getSharedSteps().setProgressResponseElementsV25(pnPollingResponseV25.getProgressResponseElementListV25());
            return pnPollingResponseV25.getProgressResponseElementV25();
        }
        return null;
    }

    private PnPollingWebhook getPnPollingWebhook(TimelineElementCategoryV23 timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        pnPollingWebhook.setTimelineElementCategoryV25(timeLineOrStatus);
        progressResponseElementsV25.clear();
        pnPollingWebhook.setProgressResponseElementListV25((LinkedList<ProgressResponseElementV25>) progressResponseElementsV25);
        return pnPollingWebhook;
    }

    @Override
    public boolean checkInternalTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());
        boolean finish = false;
        for (int i = 0; i < timelineForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            webhookSteps.getSharedSteps().setSentNotificationV25(webhookSteps.getB2bClient().getSentNotificationV25(webhookSteps.getSharedSteps().getSentNotification().getIun()));
            TimelineElementV25 timelineElement = webhookSteps.getSharedSteps()
                    .getSentNotificationV25().getTimeline().stream()
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
    public <T> void verifyAssertions(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream, T progressResponseElement) {
        try {
            Assertions.assertNotNull(progressResponseElement);
            TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());

            TimelineElementV25 elementToCheck = webhookSteps.getSharedSteps().getSentNotificationV25().getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);
            ProgressResponseElementV25 convertedProgressResponseElement = ((ProgressResponseElementV25) progressResponseElement);
            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement().getTimestamp());
            Assertions.assertEquals(convertedProgressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            String message = String.format("%s {IUN: %s -WEBHOOK %s }", assertionFailedError.getMessage(),
                    webhookSteps.getSharedSteps().getSentNotification().getIun(), eventStreamListV25.get(0).getStreamId());
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotificationV25().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        List<ProgressResponseElementV25> progressResponseElements = webhookSteps.getSharedSteps().getProgressResponseElementsV25();
        Assertions.assertNotNull(progressResponseElements);
        int lastEventID = 0;
        for (ProgressResponseElementV25 elem : progressResponseElements) {
            int currentEventId = Integer.parseInt(elem.getEventId());
            if (lastEventID != 0 && currentEventId <= lastEventID) {
                Assertions.fail(String.format("EventId is not incremental: %d <= %d", currentEventId, lastEventID));
            }
            lastEventID = currentEventId;
        }
    }

    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T> getTimelineEventForStream(String timelineEventCategory, TimingForPolling.TimingResult timingForElement) {
        AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<TimelineElementCategoryV23> result = new AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<>();
        result.setTimelineElementCategory(TimelineElementCategoryV23.valueOf(timelineEventCategory));
        result.setWaiting(timingForElement.waiting());
        result.setNumCheck(timingForElement.numCheck());
        return (AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T>) result;
    }

    //TODO MATTEO CHECK, inizialmente non era previsto per la V25, immagino vada bene l'ultima versione di NotificationStatus pre V26
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
        //TODO MATTEO
        return pnPollingWebhook;
    }
}
