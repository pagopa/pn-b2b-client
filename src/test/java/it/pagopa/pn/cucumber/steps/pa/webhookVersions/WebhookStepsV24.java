package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV24;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV24;
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
public class WebhookStepsV24 implements WebhookStepsInterface {

    private List<StreamCreationRequestV24> streamCreationRequestListV24;
    private List<StreamMetadataResponseV24> eventStreamListV24;
    private StreamRequestV24 streamRequestV24;
    private List<ProgressResponseElementV24> progressResponseElementsV24;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV24(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        progressResponseElementsV24 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV24 = new StreamRequestV24();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV24.stream().findFirst().map(StreamMetadataResponseV24::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV24.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestListV24 = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV24 streamRequest = new StreamCreationRequestV24();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV24.EventTypeEnum.STATUS : StreamCreationRequestV24.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestListV24.add(streamRequest);
        }
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamListV24 != null) {
            for (StreamMetadataResponseV24 eventStream : eventStreamListV24) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void cleanWebHookDelete() {
        List<StreamListElement> streamList = AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().listEventStreamsV24();
        for (StreamListElement stream : streamList) {
            try {
                AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().deleteEventStreamV24(stream.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElementsV24 = webhookSteps.getWebhookB2bClient().listEventStreamsV24();
        for (StreamListElement elem : streamListElementsV24) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStreamV24(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamID);
            webhookSteps.setNotificationError(e);
            webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion V24 pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequestV24 = new StreamRequestV24();
        streamRequestV24.setTitle("Update Stream V24");
        streamRequestV24.setEventType(StreamRequestV24.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV24(idStream, streamRequestV24);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV24(idStream, streamRequestV24);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV24 == null) {
            streamRequestV24 = new StreamRequestV24();
            streamRequestV24.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV24.setTitle("Update Stream V24");
        streamRequestV24.setEventType(StreamRequestV24.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV24 eventStreamV24 : eventStreamListV24) {
            StreamMetadataResponseV24 result = webhookSteps.getWebhookB2bClient().updateEventStreamV24(eventStreamV24.getStreamId(), streamRequestV24);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequestV24.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementsV24.stream().map(ProgressResponseElementV24::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequestV24 = new StreamRequestV24();
        streamRequestV24.setGroups(groupIdByPa);
    }

    @Override
    public UUID getStreamId() {
        return eventStreamListV24.get(0).getStreamId();
    }

    @Override
    public Object getStreamRequest() {
        return streamRequestV24;
    }

    @Override
    public Object initStreamRequest(Object streamRequest) {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV24();
        }
        streamRequestV24 = (StreamRequestV24) streamRequest;
        streamRequestV24.setTitle("Update Stream V24");
        streamRequestV24.setEventType(StreamRequestV24.EventTypeEnum.TIMELINE);
        return streamRequestV24;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV24 = webhookSteps.getWebhookB2bClient().listEventStreamsV24();
        for (StreamMetadataResponseV24 eventStream : eventStreamListV24) {
            StreamListElement streamListElementV24 = streamListElementsV24.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV24);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV24 eventStreamV24 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamId));
        webhookSteps.getSharedSteps().setEventStreamV24(eventStreamV24);
        Assertions.assertNotNull(eventStreamV24);
        Assertions.assertNotNull(eventStreamV24.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV24);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementsV24 = webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null);
        log.info("progressResponseElementsV24: " + progressResponseElementsV24);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamListV24.get(0).getStreamId();
        progressResponseElementsV24 = webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null);
        log.info("progressResponseElementsV24: " + progressResponseElementsV24);
        Assertions.assertEquals(progressResponseElementsV24.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV24.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamListV24 == null) eventStreamListV24 = new LinkedList<>();
        for (StreamCreationRequestV24 request : streamCreationRequestListV24) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (streamIdToReplace != null) {
                request.setReplacedStreamId(streamIdToReplace);
            }
            StreamMetadataResponseV24 eventStream = webhookSteps.getWebhookB2bClient().createEventStreamV24(request);
            if (streamIdToReplace != null) {
                StreamMetadataResponseV24 eventStreamV24 =
                        webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamIdToReplace);
                webhookSteps.getSharedSteps().setEventStreamV24(eventStreamV24);
                Assertions.assertNotNull(eventStreamV24);
                Assertions.assertNotNull(eventStreamV24.getStreamId());
                Assertions.assertNotNull(eventStreamV24.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV24);
                eventStreamListV24 = new LinkedList<>();
            }
            eventStreamListV24.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        eventStreamListV24.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV24 response = webhookSteps.getWebhookB2bClient().disableEventStreamV24(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timeLineOrStatus = ((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV24 webhookV24 = (PnPollingServiceWebhookV24) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V24);
        PnPollingResponseV24 pnPollingResponseV24 = webhookV24.waitForEvent(webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV24.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V24: " + pnPollingResponseV24.getProgressResponseElementV24());
        if (pnPollingResponseV24.getProgressResponseElementListV24() != null) {
            webhookSteps.getSharedSteps().setProgressResponseElementsV24(pnPollingResponseV24.getProgressResponseElementListV24());
            return pnPollingResponseV24.getProgressResponseElementV24();
        }
        return null;
    }

    private PnPollingWebhook getPnPollingWebhook(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        pnPollingWebhook.setTimelineElementCategoryV24(timeLineOrStatus);
        progressResponseElementsV24.clear();
        pnPollingWebhook.setProgressResponseElementListV24((LinkedList<ProgressResponseElementV24>) progressResponseElementsV24);
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
            webhookSteps.getSharedSteps().setSentNotificationV24(webhookSteps.getB2bClient().getSentNotificationV24(webhookSteps.getSharedSteps().getSentNotification().getIun()));
            TimelineElementV24 timelineElement = webhookSteps.getSharedSteps()
                    .getSentNotificationV24().getTimeline().stream()
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

            TimelineElementV24 elementToCheck = webhookSteps.getSharedSteps().getSentNotificationV24().getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);
            ProgressResponseElementV24 convertedProgressResponseElement = ((ProgressResponseElementV24) progressResponseElement);
            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement().getTimestamp());
            Assertions.assertEquals(convertedProgressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            String message = String.format("%s {IUN: %s -WEBHOOK %s }", assertionFailedError.getMessage(),
                    webhookSteps.getSharedSteps().getSentNotification().getIun(), eventStreamListV24.get(0).getStreamId());
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotificationV24().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        List<ProgressResponseElementV24> progressResponseElements = webhookSteps.getSharedSteps().getProgressResponseElementsV24();
        Assertions.assertNotNull(progressResponseElements);
        int lastEventID = 0;
        for (ProgressResponseElementV24 elem : progressResponseElements) {
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

    //TODO MATTEO CHECK, inizialmente non era previsto per la V24, immagino vada bene l'ultima versione di NotificationStatus pre V26
    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T> getStatusEventForStream(String notificationStatusName, TimingForPolling.TimingResult timingForElement) {
        AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus> result = new AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<>();
        result.setNotificationStatus(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus.valueOf(notificationStatusName));
        result.setWaiting(timingForElement.waiting());
        result.setNumCheck(timingForElement.numCheck());
        return (AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T>) result;
    }

    @Override
    public <T> PnPollingWebhook getPnPollingWebhook(T timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        if (timeLineOrStatus instanceof TimelineElementCategoryV23) {
            pnPollingWebhook.setTimelineElementCategoryV24((TimelineElementCategoryV23) timeLineOrStatus);
            progressResponseElementsV24.clear();
            pnPollingWebhook.setProgressResponseElementListV24((LinkedList<ProgressResponseElementV24>) progressResponseElementsV24);
        }
//        else if (timeLineOrStatus instanceof NotificationStatus) {
//            pnPollingWebhook.setNotificationStatusV2((NotificationStatus) timeLineOrStatus);
//            progressResponseElementsV24.clear();
//            pnPollingWebhook.setProgressResponseElementListV23((LinkedList<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23>) progressResponseElementsV23);
//        }
        return pnPollingWebhook;
    }
}
