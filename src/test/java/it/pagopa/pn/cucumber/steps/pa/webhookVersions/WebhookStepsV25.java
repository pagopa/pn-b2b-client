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

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
public class WebhookStepsV25 implements WebhookStepsInterface {

    private ProgressResponseElementV25 progressResponseElement;
    private List<ProgressResponseElementV25> progressResponseElementList;
    private List<StreamCreationRequestV25> streamCreationRequestList;
    private List<StreamMetadataResponseV25> eventStreamList;
    private StreamRequestV25 streamRequest;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;
    private final AvanzamentoNotificheWebhookB2bSteps.StreamVersion streamVersion;

    public WebhookStepsV25(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        streamVersion = AvanzamentoNotificheWebhookB2bSteps.StreamVersion.V25;
        progressResponseElementList = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequest = new StreamRequestV25();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamList.stream().findFirst().map(StreamMetadataResponseV25::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequest.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestList = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV25 streamRequest = new StreamCreationRequestV25();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV25.EventTypeEnum.STATUS : StreamCreationRequestV25.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @Override
    public Object retrieveStreamEvent(UUID streamId) {
        return webhookSteps.getWebhookB2bClient().retrieveEventStreamV25(streamId);
    }

    @Override
    public void deleteStream(UUID streamId) {
        webhookSteps.getWebhookB2bClient().deleteEventStreamV25(streamId);
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamList != null) {
            for (StreamMetadataResponseV25 eventStream : eventStreamList) {
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
        streamRequest = new StreamRequestV25();
        streamRequest.setTitle("Update Stream V25");
        streamRequest.setEventType(StreamRequestV25.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV25(idStream, streamRequest);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV25(idStream, streamRequest);
    }

    @Override
    public void updateStreams() {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV25();
            streamRequest.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequest.setTitle("Update Stream V25");
        streamRequest.setEventType(StreamRequestV25.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV25 eventStreamV25 : eventStreamList) {
            StreamMetadataResponseV25 result = webhookSteps.getWebhookB2bClient().updateEventStreamV25(eventStreamV25.getStreamId(), streamRequest);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequest.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementList.stream().map(ProgressResponseElementV25::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequest = new StreamRequestV25();
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
            streamRequest = new StreamRequestV25();
        }
        this.streamRequest = (StreamRequestV25) streamRequest;
        this.streamRequest.setTitle("Update Stream V25");
        this.streamRequest.setEventType(StreamRequestV25.EventTypeEnum.TIMELINE);
        return this.streamRequest;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV25 = webhookSteps.getWebhookB2bClient().listEventStreamsV25();
        for (StreamMetadataResponseV25 eventStream : eventStreamList) {
            StreamListElement streamListElementV25 = streamListElementsV25.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV25);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV25 eventStreamV25 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV25(streamId));
        Assertions.assertNotNull(eventStreamV25);
        Assertions.assertNotNull(eventStreamV25.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV25);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStreamV25(streamId, null);
        log.info("progressResponseElementsV25 size: " + progressResponseElementList.size());
        log.info("progressResponseElementsV25: " + progressResponseElementList);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamList.get(0).getStreamId();
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStreamV25(streamId, null);
        log.info("progressResponseElementsV25: " + progressResponseElementList);
        Assertions.assertEquals(progressResponseElementList.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementList.size());
    }

    @Override
    public void verifyNoEventsInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV25(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamList == null) eventStreamList = new LinkedList<>();
        for (StreamCreationRequestV25 request : streamCreationRequestList) {
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
                eventStreamList = new LinkedList<>();
            }
            eventStreamList.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStream(UUID streamId) {
        webhookSteps.getWebhookB2bClient().disableEventStreamV25(streamId);
    }

    @Override
    public void disableStreams() {
        eventStreamList.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV25 response = webhookSteps.getWebhookB2bClient().disableEventStreamV25(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchTimelineElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timeLineOrStatus = ((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV25 webhook = (PnPollingServiceWebhookV25) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V25);
        PnPollingResponseV25 pnPollingResponse = webhook.waitForEvent(webhookSteps.getSharedSteps().getFullSentNotificationV26().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V25: " + pnPollingResponse.getProgressResponseElementV25());
        if (pnPollingResponse.getProgressResponseElementV25() != null) {
            progressResponseElement = pnPollingResponse.getProgressResponseElementV25();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementListV25();
            return progressResponseElement;
        }
        return null;
    }

    @Override
    public Object searchStatusElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream) {
        NotificationStatus status = ((NotificationStatus) statusForStream.getNotificationStatus());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(status);
        PnPollingServiceWebhookV25 webhook = (PnPollingServiceWebhookV25) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V25);
        PnPollingResponseV25 pnPollingResponse = webhook.waitForEvent(webhookSteps.getSharedSteps().getFullSentNotificationV26().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V25: " + pnPollingResponse.getProgressResponseElementV25());
        if (pnPollingResponse.getProgressResponseElementListV25() != null) {
            progressResponseElement = pnPollingResponse.getProgressResponseElementV25();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementListV25();
            return progressResponseElement;
        }
        return null;
    }

    @Override
    public boolean checkTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());
        boolean finish = false;
        for (int i = 0; i < timelineForStream.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineForStream.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
            webhookSteps.getSharedSteps().setFullSentNotificationV25(webhookSteps.getB2bClient().getSentNotificationV25(webhookSteps.getSharedSteps().getFullSentNotificationV26().getIun()));
            TimelineElementV25 timelineElement = webhookSteps.getSharedSteps()
                    .getFullSentNotificationV25().getTimeline().stream()
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
            webhookSteps.getSharedSteps().setFullSentNotificationV25(webhookSteps.getB2bClient().getSentNotificationV25(webhookSteps.getSharedSteps().getFullSentNotificationV26().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = webhookSteps.getSharedSteps().getFullSentNotificationV25().getNotificationStatusHistory().
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
        try {
            Assertions.assertNotNull(progressResponseElement);
            TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());

            TimelineElementV25 elementToCheck = webhookSteps.getSharedSteps().getFullSentNotificationV25().getTimeline().stream()
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
                    webhookSteps.getSharedSteps().getFullSentNotificationV26().getIun(), eventStreamList.get(0).getStreamId());
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //WAIT FOR ACCEPTED PREVISTO DALLA V27
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getFullSentNotificationV25().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        Assertions.assertNotNull(progressResponseElementList);
        int lastEventID = 0;
        for (ProgressResponseElementV25 elem : progressResponseElementList) {
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
        if (timeLineOrStatus instanceof TimelineElementCategoryV23) {
            pnPollingWebhook.setTimelineElementCategoryV25((TimelineElementCategoryV23) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV25(progressResponseElementList);
        } else if (timeLineOrStatus instanceof NotificationStatus) {
            pnPollingWebhook.setNotificationStatusV25((NotificationStatus) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV25(progressResponseElementList);
        }
        return pnPollingWebhook;
    }

    @Override
    public void getTimelineElementVersionB2B(String iun) {
        FullSentNotificationV25 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV25(iun);
        webhookSteps.getSharedSteps().setFullSentNotificationV25(fullSentNotification);
    }

    @Override
    public void compareTimestampWebhook(String timelineElementCategory, String webhookElementCategory, boolean mustBeEqual) {
        Assertions.assertNotNull(progressResponseElementList);
        OffsetDateTime eventTimestamp = progressResponseElementList.stream().filter(
                elem -> elem.getElement().getCategory().getValue().equals(webhookElementCategory)).findAny().get().getElement().getTimestamp();
        OffsetDateTime notificationTimestamp = webhookSteps.getSharedSteps().getFullSentNotificationV26().getTimeline().stream().filter(
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
        for (StreamMetadataResponseV25 stream : eventStreamList) {
            UUID streamId = stream.getStreamId();
            List<ProgressResponseElementV25> progressResponseElements = webhookSteps.getWebhookB2bClient().consumeEventStreamV25(streamId, null);
            System.out.println("progressResponseElements V25 size: " + progressResponseElements.size());
            webhookSteps.sleepTest(50L);
        }
    }

    @Override
    public void verificaDeanonimizzazioneEventiTimelineAnalogica(boolean withDelega) {
        Assertions.assertNotNull(progressResponseElement);
        TimelineElementDetailsV25 timelineElementWebhookDetails = progressResponseElement.getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails);
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getAddress());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getMunicipality());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getProvince());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getZip());
        if (withDelega) {
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getTaxId());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getDenomination());
        }
    }

    @Override
    public void verificaDeanonimizzazioneEventiTimelineDigitale(boolean withDelega) {
        Assertions.assertNotNull(progressResponseElement);
        TimelineElementDetailsV25 timelineElementWebhookDetails = progressResponseElement.getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails.getDigitalAddress());
        if (withDelega) {
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getTaxId());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getDenomination());
        }
    }

    @Override
    public void setProgressResponseElement(Object progressResponseElement) {
        this.progressResponseElement = (ProgressResponseElementV25) progressResponseElement;
    }

    @Override
    public List<Object> verificaCorrispondenzaElementiTimelineWebhookAndB2B() {
        List<Object> resultList = new LinkedList<>();

        TimelineElementV25 timelineElementWebHook = progressResponseElement.getElement();
        Assertions.assertNotNull(timelineElementWebHook);
        Assertions.assertNotNull(timelineElementWebHook.getCategory());

        String elementId = timelineElementWebHook.getCategory().toString();

        TimelineElementV25 timelineElement = webhookSteps.getSharedSteps().getFullSentNotificationV25().getTimeline().
                stream()
                .filter(data -> data.getCategory() != null)
                .filter(data -> data.getCategory().getValue().equalsIgnoreCase(elementId))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(timelineElement);

        TimelineElementDetailsV25 timelineElementDetails = timelineElement.getDetails();
        Assertions.assertNotNull(timelineElementDetails);
        resultList.add(timelineElementDetails);

        TimelineElementDetailsV25 timelineElementWebhookDetails = timelineElementWebHook.getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails);
        resultList.add(timelineElementDetails);

        return resultList;
    }

    @Override
    public void checkLegalFactId() {
        Assertions.assertNotNull(progressResponseElement);
        Assertions.assertNotNull(progressResponseElement.getElement().getLegalFactsIds());
        Assertions.assertFalse(progressResponseElement.getElement().getLegalFactsIds().isEmpty());
    }

    @Override
    public void checkCorrectDisabling(UUID streamId) {
        StreamMetadataResponseV25 eventStream = webhookSteps.getWebhookB2bClient().retrieveEventStreamV25(streamId);
        Assertions.assertNotNull(eventStream);
        Assertions.assertNotNull(eventStream.getStreamId());
        Assertions.assertNotNull(eventStream.getDisabledDate());
        log.info("EVENTSTREAM REPLACED: {}", eventStream);
    }

    @Override
    public void verificaPresenzaSercQ(boolean isPresent) {
        String channel = isPresent ? "SERCQ" : "PEC";
        Assertions.assertTrue(progressResponseElementList.stream()
                .filter(data -> data.getElement().getElementId() != null)
                .filter(timelineElement -> timelineElement.getElement().getElementId().contains("SEND_DIGITAL_FEEDBACK"))
                .allMatch(elementDetails -> "OK".equals(elementDetails.getElement().getDetails().getResponseStatus().toString())
                        && channel.equals(elementDetails.getElement().getDetails().getDigitalAddress().getType())
                ));
    }

}
