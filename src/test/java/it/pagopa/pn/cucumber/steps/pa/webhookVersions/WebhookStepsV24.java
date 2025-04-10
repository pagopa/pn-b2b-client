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

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
public class WebhookStepsV24 implements WebhookStepsInterface {

    private ProgressResponseElementV24 progressResponseElement;
    private List<ProgressResponseElementV24> progressResponseElementList;
    private List<StreamCreationRequestV24> streamCreationRequestList;
    private List<StreamMetadataResponseV24> eventStreamList;
    private StreamRequestV24 streamRequest;
    private final AvanzamentoNotificheWebhookB2bSteps webhookSteps;
    private final StreamVersion streamVersion;

    public WebhookStepsV24(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        streamVersion = StreamVersion.V24;
        progressResponseElementList = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequest = new StreamRequestV24();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamList.stream().findFirst().map(StreamMetadataResponseV24::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequest.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestList = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV24 streamRequest = new StreamCreationRequestV24();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV24.EventTypeEnum.STATUS : StreamCreationRequestV24.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @Override
    public Object retrieveStreamEvent(UUID streamId) {
        return webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamId);
    }

    @Override
    public void deleteStream(UUID streamId) {
        webhookSteps.getWebhookB2bClient().deleteEventStreamV24(streamId);
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamList != null) {
            for (StreamMetadataResponseV24 eventStream : eventStreamList) {
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
        streamRequest = new StreamRequestV24();
        streamRequest.setTitle("Update Stream V24");
        streamRequest.setEventType(StreamRequestV24.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV24(idStream, streamRequest);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV24(idStream, streamRequest);
    }

    @Override
    public void updateStreams() {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV24();
            streamRequest.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequest.setTitle("Update Stream V24");
        streamRequest.setEventType(StreamRequestV24.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV24 eventStreamV24 : eventStreamList) {
            StreamMetadataResponseV24 result = webhookSteps.getWebhookB2bClient().updateEventStreamV24(eventStreamV24.getStreamId(), streamRequest);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequest.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementList.stream().map(ProgressResponseElementV24::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequest = new StreamRequestV24();
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
            streamRequest = new StreamRequestV24();
        }
        this.streamRequest = (StreamRequestV24) streamRequest;
        this.streamRequest.setTitle("Update Stream V24");
        this.streamRequest.setEventType(StreamRequestV24.EventTypeEnum.TIMELINE);
        return this.streamRequest;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV24 = webhookSteps.getWebhookB2bClient().listEventStreamsV24();
        for (StreamMetadataResponseV24 eventStream : eventStreamList) {
            StreamListElement streamListElementV24 = streamListElementsV24.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV24);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV24 eventStreamV24 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamId));
        Assertions.assertNotNull(eventStreamV24);
        Assertions.assertNotNull(eventStreamV24.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV24);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null);
        log.info("progressResponseElementsV24 size: " + progressResponseElementList.size());
        log.info("progressResponseElementsV24: " + progressResponseElementList);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamList.get(0).getStreamId();
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null);
        log.info("progressResponseElementsV24: " + progressResponseElementList);
        Assertions.assertEquals(progressResponseElementList.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementList.size());
    }

    @Override
    public void verifyNoEventsInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamList == null) eventStreamList = new LinkedList<>();
        for (StreamCreationRequestV24 request : streamCreationRequestList) {
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
                eventStreamList = new LinkedList<>();
            }
            eventStreamList.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStream(UUID streamId) {
        webhookSteps.getWebhookB2bClient().disableEventStreamV24(streamId);
    }

    @Override
    public void disableStreams() {
        eventStreamList.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV24 response = webhookSteps.getWebhookB2bClient().disableEventStreamV24(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchTimelineElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timeLineOrStatus = ((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV24 webhook = (PnPollingServiceWebhookV24) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V24);
        PnPollingResponseV24 pnPollingResponse = webhook.waitForEvent(webhookSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V24: " + pnPollingResponse.getProgressResponseElementV24());
        if (pnPollingResponse.getProgressResponseElementV24() != null) {
            progressResponseElement = pnPollingResponse.getProgressResponseElementV24();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementListV24();
            return progressResponseElement;
        }
        return null;
    }

    @Override
    public Object searchStatusElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream) {
        NotificationStatus status = ((NotificationStatus) statusForStream.getNotificationStatus());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(status);
        PnPollingServiceWebhookV24 webhook = (PnPollingServiceWebhookV24) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V24);
        PnPollingResponseV24 pnPollingResponse = webhook.waitForEvent(webhookSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V24: " + pnPollingResponse.getProgressResponseElementV24());
        if (pnPollingResponse.getProgressResponseElementListV24() != null) {
            progressResponseElement = pnPollingResponse.getProgressResponseElementV24();
            progressResponseElementList = pnPollingResponse.getProgressResponseElementListV24();
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
            FullSentNotificationV24 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV24(webhookSteps.getSharedSteps().getNotificationIun());
            TimelineElementV24 timelineElement = fullSentNotification.getTimeline().stream()
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
            FullSentNotificationV24 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV24(webhookSteps.getSharedSteps().getNotificationIun());
            NotificationStatusHistoryElement notificationStatusHistoryElement = fullSentNotification.getNotificationStatusHistory().stream().filter(
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
        String iun = webhookSteps.getSharedSteps().getNotificationIun();
        try {
            Assertions.assertNotNull(progressResponseElement);
            TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());

            FullSentNotificationV24 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV24(iun);
            TimelineElementV24 elementToCheck = fullSentNotification.getTimeline().stream()
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
            String message = String.format("%s {IUN: %s -WEBHOOK %s }",
                    assertionFailedError.getMessage(), iun, eventStreamList.get(0).getStreamId());
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //WAIT FOR ACCEPTED PREVISTO DALLA V27
    }

    @Override
    public void verifyIncrementalEventId() {
        Assertions.assertNotNull(progressResponseElementList);
        int lastEventID = 0;
        for (ProgressResponseElementV24 elem : progressResponseElementList) {
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
            pnPollingWebhook.setTimelineElementCategoryV24((TimelineElementCategoryV23) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV24(progressResponseElementList);
        } else if (timeLineOrStatus instanceof NotificationStatus) {
            pnPollingWebhook.setNotificationStatusV24((NotificationStatus) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV24(progressResponseElementList);
        }
        return pnPollingWebhook;
    }

    @Override
    public void getTimelineElementVersionB2B(String iun) {
        webhookSteps.getB2bClient().getSentNotificationV24(iun);
    }

    @Override
    public void compareTimestampWebhook(String timelineElementCategory, String webhookElementCategory, boolean mustBeEqual) {
        String iun = webhookSteps.getSharedSteps().getNotificationIun();
        FullSentNotificationV24 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV24(iun);
        Assertions.assertNotNull(progressResponseElementList);
        OffsetDateTime eventTimestamp = progressResponseElementList.stream().filter(
                elem -> elem.getElement().getCategory().getValue().equals(webhookElementCategory)).findAny().get().getElement().getTimestamp();
        OffsetDateTime notificationTimestamp = fullSentNotification.getTimeline().stream().filter(
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
        for (StreamMetadataResponseV24 stream : eventStreamList) {
            UUID streamId = stream.getStreamId();
            List<ProgressResponseElementV24> progressResponseElements = webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null);
            System.out.println("progressResponseElements V24 size: " + progressResponseElements.size());
            webhookSteps.sleepTest(50L);
        }
    }

    @Override
    public void verificaDeanonimizzazioneEventiTimelineAnalogica(boolean withDelega) {
        Assertions.assertNotNull(progressResponseElement);
        TimelineElementDetailsV23 timelineElementWebhookDetails = progressResponseElement.getElement().getDetails();
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
        TimelineElementDetailsV23 timelineElementWebhookDetails = progressResponseElement.getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails.getDigitalAddress());
        if (withDelega) {
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getTaxId());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getDenomination());
        }
    }

    @Override
    public void setProgressResponseElement(Object progressResponseElement) {
        this.progressResponseElement = (ProgressResponseElementV24) progressResponseElement;
    }

    @Override
    public List<Object> verificaCorrispondenzaElementiTimelineWebhookAndB2B() {
        List<Object> resultList = new LinkedList<>();

        TimelineElementV24 timelineElementWebHook = progressResponseElement.getElement();
        Assertions.assertNotNull(timelineElementWebHook);
        Assertions.assertNotNull(timelineElementWebHook.getCategory());

        String elementId = timelineElementWebHook.getCategory().toString();

        FullSentNotificationV24 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV24(webhookSteps.getSharedSteps().getNotificationIun());
        TimelineElementV24 timelineElement = fullSentNotification.getTimeline().
                stream()
                .filter(data -> data.getCategory() != null)
                .filter(data -> data.getCategory().getValue().equalsIgnoreCase(elementId))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(timelineElement);

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementDetailsV23 timelineElementDetails = timelineElement.getDetails();
        Assertions.assertNotNull(timelineElementDetails);
        resultList.add(timelineElementDetails);

        TimelineElementDetailsV23 timelineElementWebhookDetails = timelineElementWebHook.getDetails();
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
        StreamMetadataResponseV24 eventStream = webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamId);
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

    @Override
    public void checkLegalFactCategory(String timelineCategory, String legalFactCategory) {
        String iun = webhookSteps.getSharedSteps().getNotificationIun();
        FullSentNotificationV24 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV24(iun);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV24 timelineElementWithTargetCategory =
                fullSentNotification.getTimeline().stream().filter(
                        x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
        Assertions.assertNotNull(timelineElementWithTargetCategory);
        timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                x -> Assertions.assertNotEquals(x.getCategory().getValue(), legalFactCategory));
    }
}
