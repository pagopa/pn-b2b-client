package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV23;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.*;
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
public class WebhookStepsV23 implements WebhookStepsInterface {

    private List<StreamCreationRequestV23> streamCreationRequestListV23;
    private List<StreamMetadataResponseV23> eventStreamListV23;
    private StreamRequestV23 streamRequestV23;
    private List<ProgressResponseElementV23> progressResponseElementsV23;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV23(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        progressResponseElementsV23 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV23 = new StreamRequestV23();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV23.stream().findFirst().map(StreamMetadataResponseV23::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV23.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestListV23 = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV23 streamRequest = new StreamCreationRequestV23();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV23.EventTypeEnum.STATUS : StreamCreationRequestV23.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestListV23.add(streamRequest);
        }
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamListV23 != null) {
            for (StreamMetadataResponseV23 eventStream : eventStreamListV23) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void cleanWebHookDelete() {
        List<StreamListElement> streamList = AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().listEventStreamsV23();
        for (StreamListElement stream : streamList) {
            try {
                AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().deleteEventStreamV23(stream.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElementsV23 = webhookSteps.getWebhookB2bClient().listEventStreamsV23();
        for (StreamListElement elem : streamListElementsV23) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStreamV23(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookSteps.getWebhookB2bClient().getEventStreamV23(streamID);
            webhookSteps.setNotificationError(e);
            webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion V23 pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequestV23 = new StreamRequestV23();
        streamRequestV23.setTitle("Update Stream V23");
        streamRequestV23.setEventType(StreamRequestV23.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV23(idStream, streamRequestV23);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV23(idStream, streamRequestV23);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV23 == null) {
            streamRequestV23 = new StreamRequestV23();
            streamRequestV23.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV23.setTitle("Update Stream V23");
        streamRequestV23.setEventType(StreamRequestV23.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV23 eventStreamV23 : eventStreamListV23) {
            StreamMetadataResponseV23 result = webhookSteps.getWebhookB2bClient().updateEventStreamV23(eventStreamV23.getStreamId(), streamRequestV23);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequestV23.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementsV23.stream().map(ProgressResponseElementV23::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequestV23 = new StreamRequestV23();
        streamRequestV23.setGroups(groupIdByPa);
    }

    @Override
    public UUID getStreamId() {
        return eventStreamListV23.get(0).getStreamId();
    }

    @Override
    public Object getStreamRequest() {
        return streamRequestV23;
    }

    @Override
    public Object initStreamRequest(Object streamRequest) {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV23();
        }
        streamRequestV23 = (StreamRequestV23) streamRequest;
        streamRequestV23.setTitle("Update Stream V23");
        streamRequestV23.setEventType(StreamRequestV23.EventTypeEnum.TIMELINE);
        return streamRequestV23;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV23 = webhookSteps.getWebhookB2bClient().listEventStreamsV23();
        for (StreamMetadataResponseV23 eventStream : eventStreamListV23) {
            StreamListElement streamListElementV23 = streamListElementsV23.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV23);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV23 eventStreamV23 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().getEventStreamV23(streamId));
        webhookSteps.getSharedSteps().setEventStreamV23(eventStreamV23);
        Assertions.assertNotNull(eventStreamV23);
        Assertions.assertNotNull(eventStreamV23.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV23);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementsV23 = webhookSteps.getWebhookB2bClient().consumeEventStreamV23(streamId, null);
        log.info("progressResponseElementsV23: " + progressResponseElementsV23);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamListV23.get(0).getStreamId();
        progressResponseElementsV23 = webhookSteps.getWebhookB2bClient().consumeEventStreamV23(streamId, null);
        log.info("progressResponseElementsV23: " + progressResponseElementsV23);
        Assertions.assertEquals(progressResponseElementsV23.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV23.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV23(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        if (eventStreamListV23 == null) eventStreamListV23 = new LinkedList<>();
        for (StreamCreationRequestV23 request : streamCreationRequestListV23) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (replaceId) {
                request.setReplacedStreamId(webhookSteps.getSharedSteps().getEventStreamV23().getStreamId());
            }
            StreamMetadataResponseV23 eventStream = webhookSteps.getWebhookB2bClient().createEventStreamV23(request);
            if (replaceId) {
                StreamMetadataResponseV23 eventStreamV23 =
                        webhookSteps.getWebhookB2bClient().getEventStreamV23(eventStreamListV23.get(0).getStreamId());
                webhookSteps.getSharedSteps().setEventStreamV23(eventStreamV23);
                Assertions.assertNotNull(eventStreamV23);
                Assertions.assertNotNull(eventStreamV23.getStreamId());
                Assertions.assertNotNull(eventStreamV23.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV23);
                eventStreamListV23 = new LinkedList<>();
            }
            eventStreamListV23.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        eventStreamListV23.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV23 response = webhookSteps.getWebhookB2bClient().disableEventStreamV23(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timeLineOrStatus = ((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV23 webhookV23 = (PnPollingServiceWebhookV23) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V23);
        PnPollingResponseV23 pnPollingResponseV23 = webhookV23.waitForEvent(webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV23.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V23: " + pnPollingResponseV23.getProgressResponseElementV23());
        if (pnPollingResponseV23.getProgressResponseElementsV23() != null) {
            webhookSteps.getSharedSteps().setProgressResponseElementsV23(pnPollingResponseV23.getProgressResponseElementsV23());
            return pnPollingResponseV23.getProgressResponseElementV23();
        }
        return null;
    }

    private PnPollingWebhook getPnPollingWebhook(TimelineElementCategoryV23 timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        pnPollingWebhook.setTimelineElementCategoryV23(timeLineOrStatus);
        progressResponseElementsV23.clear();
        pnPollingWebhook.setProgressResponseElementListV23((LinkedList<ProgressResponseElementV23>) progressResponseElementsV23);
        return pnPollingWebhook;
    }

    @Override
    public boolean checkInternalTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());
        boolean finish = false;
        for (int i = 0; i < timelineForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            webhookSteps.getSharedSteps().setSentNotificationV23(webhookSteps.getB2bClient().getSentNotificationV23(webhookSteps.getSharedSteps().getSentNotification().getIun()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 timelineElement = webhookSteps.getSharedSteps()
                    .getSentNotificationV23().getTimeline().stream()
                    .filter(elem -> elem.getCategory().equals(timelineElementInternalCategory))
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
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 elementToCheck = webhookSteps.getSharedSteps().getSentNotificationV23().getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);
            ProgressResponseElementV23 convertedProgressResponseElement = ((ProgressResponseElementV23) progressResponseElement);
            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement().getTimestamp());
            Assertions.assertEquals(convertedProgressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            String message = String.format("%s {IUN: %s -WEBHOOK %s }", assertionFailedError.getMessage(),
                    webhookSteps.getSharedSteps().getSentNotification().getIun(), eventStreamListV23.get(0).getStreamId());
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotificationV23().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        List<ProgressResponseElementV23> progressResponseElements = webhookSteps.getSharedSteps().getProgressResponseElementsV23();
        Assertions.assertNotNull(progressResponseElements);
        int lastEventID = 0;
        for (ProgressResponseElementV23 elem : progressResponseElements) {
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
