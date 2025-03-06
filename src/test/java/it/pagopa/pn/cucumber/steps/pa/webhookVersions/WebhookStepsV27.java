package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV27;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV27;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.*;
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
public class WebhookStepsV27 implements WebhookStepsInterface {
    private List<StreamCreationRequestV27> streamCreationRequestListV27;
    private List<StreamMetadataResponseV27> eventStreamListV27;
    private StreamRequestV27 streamRequestV27;
    private List<ProgressResponseElementV27> progressResponseElementsV27;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;
    private boolean waitForAccepted;

    public WebhookStepsV27(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        progressResponseElementsV27 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV27 = new StreamRequestV27();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV27.stream().findFirst().map(StreamMetadataResponseV27::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV27.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestListV27 = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV27 streamRequest = new StreamCreationRequestV27();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV27.EventTypeEnum.STATUS : StreamCreationRequestV27.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestListV27.add(streamRequest);
        }
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamListV27 != null) {
            for (StreamMetadataResponseV27 eventStream : eventStreamListV27) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElementsV27 = webhookSteps.getWebhookB2bClient().listEventStreamsV27();
        for (StreamListElement elem : streamListElementsV27) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStreamV27(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookSteps.getWebhookB2bClient().retrieveEventStreamV27(streamID);
            webhookSteps.setNotificationError(e);
            webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion V27 pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequestV27 = new StreamRequestV27();
        streamRequestV27.setTitle("Update Stream V27");
        streamRequestV27.setEventType(StreamRequestV27.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV27(idStream, streamRequestV27);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV27(idStream, streamRequestV27);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV27 == null) {
            streamRequestV27 = new StreamRequestV27();
            streamRequestV27.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV27.setTitle("Update Stream V27");
        streamRequestV27.setEventType(StreamRequestV27.EventTypeEnum.TIMELINE);
        streamRequestV27.setWaitForAccepted(waitForAccepted);
        for (StreamMetadataResponseV27 eventStreamV27 : eventStreamListV27) {
            StreamMetadataResponseV27 result = webhookSteps.getWebhookB2bClient().updateEventStreamV27(eventStreamV27.getStreamId(), streamRequestV27);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequestV27.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementsV27.stream().map(ProgressResponseElementV27::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequestV27 = new StreamRequestV27();
        streamRequestV27.setGroups(groupIdByPa);
    }

    @Override
    public UUID getStreamId() {
        return eventStreamListV27.get(0).getStreamId();
    }

    @Override
    public Object getStreamRequest() {
        return streamRequestV27;
    }

    @Override
    public Object initStreamRequest(Object streamRequest) {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV27();
        }
        streamRequestV27 = (StreamRequestV27) streamRequest;
        streamRequestV27.setTitle("Update Stream V27");
        streamRequestV27.setEventType(StreamRequestV27.EventTypeEnum.TIMELINE);
        return streamRequestV27;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV27 = webhookSteps.getWebhookB2bClient().listEventStreamsV27();
        for (StreamMetadataResponseV27 eventStream : eventStreamListV27) {
            StreamListElement streamListElementV27 = streamListElementsV27.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV27);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV27 eventStreamV27 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV27(streamId));
        webhookSteps.getSharedSteps().setEventStreamV27(eventStreamV27);
        Assertions.assertNotNull(eventStreamV27);
        Assertions.assertNotNull(eventStreamV27.getStreamId());
        Assertions.assertEquals(waitForAccepted, eventStreamV27.getWaitForAccepted());
        log.info("EVENTSTREAM: {}", eventStreamV27);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementsV27 = webhookSteps.getWebhookB2bClient().consumeEventStreamV27(streamId, null);
        log.info("progressResponseElementsV27: " + progressResponseElementsV27);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamListV27.get(0).getStreamId();
        progressResponseElementsV27 = webhookSteps.getWebhookB2bClient().consumeEventStreamV27(streamId, null);
        log.info("progressResponseElementsV27: " + progressResponseElementsV27);
        Assertions.assertEquals(progressResponseElementsV27.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV27.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV27(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        if (eventStreamListV27 == null) eventStreamListV27 = new LinkedList<>();
        for (StreamCreationRequestV27 request : streamCreationRequestListV27) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (replaceId) {
                request.setReplacedStreamId(webhookSteps.getSharedSteps().getEventStreamV27().getStreamId());
            }
            request.setWaitForAccepted(waitForAccepted);
            StreamMetadataResponseV27 eventStream = webhookSteps.getWebhookB2bClient().createEventStreamV27(request);
            if (replaceId) {
                StreamMetadataResponseV27 eventStreamV27 =
                        webhookSteps.getWebhookB2bClient().retrieveEventStreamV27(eventStreamListV27.get(0).getStreamId());
                webhookSteps.getSharedSteps().setEventStreamV27(eventStreamV27);
                Assertions.assertNotNull(eventStreamV27);
                Assertions.assertNotNull(eventStreamV27.getStreamId());
                Assertions.assertNotNull(eventStreamV27.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV27);
                eventStreamListV27 = new LinkedList<>();
            }
            eventStreamListV27.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        eventStreamListV27.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV27 response = webhookSteps.getWebhookB2bClient().disableEventStreamV27(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timeLineOrStatus = ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV27 webhookV27 = (PnPollingServiceWebhookV27) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V27);
        PnPollingResponseV27 pnPollingResponseV27 = webhookV27.waitForEvent(webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV27.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V26: " + pnPollingResponseV27.getProgressResponseElementV27());
        if (pnPollingResponseV27.getProgressResponseElementListV27() != null) {
            webhookSteps.getSharedSteps().setProgressResponseElementsV27(pnPollingResponseV27.getProgressResponseElementListV27());
            return pnPollingResponseV27.getProgressResponseElementV27();
        }
        return null;
    }

    private PnPollingWebhook getPnPollingWebhook(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        pnPollingWebhook.setTimelineElementCategoryV27(timeLineOrStatus);
        progressResponseElementsV27.clear();
        pnPollingWebhook.setProgressResponseElementListV27((LinkedList<ProgressResponseElementV27>) progressResponseElementsV27);
        return pnPollingWebhook;
    }

    @Override
    public boolean checkInternalTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementInternalCategory = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory()).name());
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
                    this.webhookSteps.getSharedSteps().getSentNotification().getIun(), this.eventStreamListV27.get(0).getStreamId());
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void setValueForWaitForAccepted(boolean bool) {
        waitForAccepted = bool;
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotification().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        List<ProgressResponseElementV27> progressResponseElements = webhookSteps.getSharedSteps().getProgressResponseElementsV27();
        Assertions.assertNotNull(progressResponseElements);
        int lastEventID = 0;
        for (ProgressResponseElementV27 elem : progressResponseElements) {
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
//        if (timeLineOrStatus instanceof TimelineElementCategoryV26) {
//            pnPollingWebhook.setTimelineElementCategoryV27((TimelineElementCategoryV26) timeLineOrStatus);
//            progressResponseElementsV27.clear();
//            pnPollingWebhook.setProgressResponseElementListV27((LinkedList<ProgressResponseElementV27>) progressResponseElementsV27);
//        } else if (timeLineOrStatus instanceof NotificationStatus) {
//            pnPollingWebhook.setNotificationStatus_noVersionV27((NotificationStatus) timeLineOrStatus);
//            progressResponseElementsV27.clear();
//            pnPollingWebhook.setProgressResponseElementListV27((LinkedList<ProgressResponseElementV27>) progressResponseElementsV27);
//        } else if (timeLineOrStatus instanceof NotificationStatusV26) {
//            pnPollingWebhook.setNotificationStatusV27((NotificationStatusV26) timeLineOrStatus);
//            progressResponseElementsV27.clear();
//            pnPollingWebhook.setProgressResponseElementListV27((LinkedList<ProgressResponseElementV27>) progressResponseElementsV27);
//        } else {
//            throw new IllegalArgumentException();
//        }
        return pnPollingWebhook;
    }
}
