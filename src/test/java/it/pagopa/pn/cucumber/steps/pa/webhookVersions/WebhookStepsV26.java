package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV26;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.*;
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
public class WebhookStepsV26 implements WebhookStepsInterface {

    private List<StreamCreationRequestV26> streamCreationRequestListV26;
    private List<StreamMetadataResponseV26> eventStreamListV26;
    private StreamRequestV26 streamRequestV26;
    private List<ProgressResponseElementV26> progressResponseElementsV26;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV26(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        progressResponseElementsV26 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV26 = new StreamRequestV26();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV26.stream().findFirst().map(StreamMetadataResponseV26::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV26.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestListV26 = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV26 streamRequest = new StreamCreationRequestV26();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV26.EventTypeEnum.STATUS : StreamCreationRequestV26.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestListV26.add(streamRequest);
        }
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamListV26 != null) {
            for (StreamMetadataResponseV26 eventStream : eventStreamListV26) {
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
        List<StreamListElement> streamListElementsV26 = webhookSteps.getWebhookB2bClient().listEventStreamsV26();
        for (StreamListElement elem : streamListElementsV26) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStreamV26(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookSteps.getWebhookB2bClient().retrieveEventStreamV26(streamID);
            webhookSteps.setNotificationError(e);
            webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion V26 pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequestV26 = new StreamRequestV26();
        streamRequestV26.setTitle("Update Stream V26");
        streamRequestV26.setEventType(StreamRequestV26.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV26(idStream, streamRequestV26);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV26(idStream, streamRequestV26);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV26 == null) {
            streamRequestV26 = new StreamRequestV26();
            streamRequestV26.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV26.setTitle("Update Stream V26");
        streamRequestV26.setEventType(StreamRequestV26.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV26 eventStreamV26 : eventStreamListV26) {
            StreamMetadataResponseV26 result = webhookSteps.getWebhookB2bClient().updateEventStreamV26(eventStreamV26.getStreamId(), streamRequestV26);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequestV26.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementsV26.stream().map(ProgressResponseElementV26::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequestV26 = new StreamRequestV26();
        streamRequestV26.setGroups(groupIdByPa);
    }

    @Override
    public UUID getStreamId() {
        return eventStreamListV26.get(0).getStreamId();
    }

    @Override
    public Object getStreamRequest() {
        return streamRequestV26;
    }

    @Override
    public Object initStreamRequest(Object streamRequest) {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV26();
        }
        streamRequestV26 = (StreamRequestV26) streamRequest;
        streamRequestV26.setTitle("Update Stream V26");
        streamRequestV26.setEventType(StreamRequestV26.EventTypeEnum.TIMELINE);
        return streamRequestV26;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV26 = webhookSteps.getWebhookB2bClient().listEventStreamsV26();
        for (StreamMetadataResponseV26 eventStream : eventStreamListV26) {
            StreamListElement streamListElementV26 = streamListElementsV26.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV26);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV26 eventStreamV26 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV26(streamId));
        webhookSteps.getSharedSteps().setEventStreamV26(eventStreamV26);
        Assertions.assertNotNull(eventStreamV26);
        Assertions.assertNotNull(eventStreamV26.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV26);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementsV26 = webhookSteps.getWebhookB2bClient().consumeEventStreamV26(streamId, null);
        log.info("progressResponseElementsV26: " + progressResponseElementsV26);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamListV26.get(0).getStreamId();
        progressResponseElementsV26 = webhookSteps.getWebhookB2bClient().consumeEventStreamV26(streamId, null);
        log.info("progressResponseElementsV26: " + progressResponseElementsV26);
        Assertions.assertEquals(progressResponseElementsV26.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV26.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV26(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamListV26 == null) eventStreamListV26 = new LinkedList<>();
        for (StreamCreationRequestV26 request : streamCreationRequestListV26) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (streamIdToReplace != null) {
                request.setReplacedStreamId(streamIdToReplace);
            }

            StreamMetadataResponseV26 eventStream = webhookSteps.getWebhookB2bClient().createEventStreamV26(request);
            if (streamIdToReplace != null) {
                StreamMetadataResponseV26 eventStreamV26 =
                        webhookSteps.getWebhookB2bClient().retrieveEventStreamV26(streamIdToReplace);
                webhookSteps.getSharedSteps().setEventStreamV26(eventStreamV26);
                Assertions.assertNotNull(eventStreamV26);
                Assertions.assertNotNull(eventStreamV26.getStreamId());
                Assertions.assertNotNull(eventStreamV26.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV26);
                eventStreamListV26 = new LinkedList<>();
            }
            eventStreamListV26.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        eventStreamListV26.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV26 response = webhookSteps.getWebhookB2bClient().disableEventStreamV26(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV26 timeLineOrStatus = ((TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV26 webhookV26 = (PnPollingServiceWebhookV26) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V26);
        PnPollingResponseV26 pnPollingResponseV26 = webhookV26.waitForEvent(webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV26.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V26: " + pnPollingResponseV26.getProgressResponseElementV26());
        if (pnPollingResponseV26.getProgressResponseElementListV26() != null) {
            webhookSteps.getSharedSteps().setProgressResponseElementsV26(pnPollingResponseV26.getProgressResponseElementListV26());
            return pnPollingResponseV26.getProgressResponseElementV26();
        }
        return null;
    }

    private PnPollingWebhook getPnPollingWebhook(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        pnPollingWebhook.setTimelineElementCategoryV26(timeLineOrStatus);
        progressResponseElementsV26.clear();
        pnPollingWebhook.setProgressResponseElementListV26((LinkedList<ProgressResponseElementV26>) progressResponseElementsV26);
        return pnPollingWebhook;
    }

    @Override
    public boolean checkInternalTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV26 timelineElementInternalCategory = TimelineElementCategoryV26.valueOf(((TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory()).name());
        boolean finish = false;
        for (int i = 0; i < timelineForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            webhookSteps.getSharedSteps().setSentNotification(webhookSteps.getB2bClient().getSentNotification(webhookSteps.getSharedSteps().getSentNotification().getIun()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElement = webhookSteps.getSharedSteps()
                    .getSentNotification().getTimeline().stream()
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
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementInternalCategory =
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory()).name());

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 elementToCheck = webhookSteps.getSharedSteps().getSentNotification().getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);
            ProgressResponseElementV26 convertedProgressResponseElement = ((ProgressResponseElementV26) progressResponseElement);
            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement().getTimestamp());
            Assertions.assertEquals(convertedProgressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);

        } catch (AssertionFailedError assertionFailedError) {
            String message = String.format("%s {IUN: %s -WEBHOOK %s }", assertionFailedError.getMessage(),
                    webhookSteps.getSharedSteps().getSentNotification().getIun(), eventStreamListV26.get(0).getStreamId());
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotification().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        List<ProgressResponseElementV26> progressResponseElements = webhookSteps.getSharedSteps().getProgressResponseElementsV26();
        Assertions.assertNotNull(progressResponseElements);
        int lastEventID = 0;
        for (ProgressResponseElementV26 elem : progressResponseElements) {
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
        result.setNotificationStatus(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26.valueOf(notificationStatusName));
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
