package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV26;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV28;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.v28.PnPollingServiceWebhookV28;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v28.*;
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
public class WebhookStepsV28 implements WebhookStepsInterface {

    private ProgressResponseElementV28 progressResponseElement;
    private List<ProgressResponseElementV28> progressResponseElementList;
    private List<StreamCreationRequestV28> streamCreationRequestList;
    private List<StreamMetadataResponseV28> eventStreamList;
    private StreamRequestV28 streamRequest;
    private final AvanzamentoNotificheWebhookB2bSteps webhookSteps;
    private final StreamVersion streamVersion;
    private boolean waitForAccepted;

    public WebhookStepsV28(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        streamVersion = StreamVersion.V28;
        progressResponseElementList = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequest = new StreamRequestV28();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamList.stream().findFirst().map(StreamMetadataResponseV28::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequest.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        streamCreationRequestList = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV28 streamRequest = new StreamCreationRequestV28();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV28.EventTypeEnum.STATUS : StreamCreationRequestV28.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @Override
    public Object retrieveStreamEvent(UUID streamId) {
        return webhookSteps.getWebhookB2bClient().retrieveEventStreamV28(streamId);
    }

    @Override
    public void deleteStream(UUID streamId) {
        webhookSteps.getWebhookB2bClient().deleteEventStreamV28(streamId);
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamList != null) {
            for (StreamMetadataResponseV28 eventStream : eventStreamList) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void cleanWebHookDelete() {
        List<StreamListElement> streamList = AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().listEventStreamsV28();
        for (StreamListElement stream : streamList) {
            try {
                AvanzamentoNotificheWebhookB2bSteps.getWebhookClientForClean().deleteEventStreamV28(stream.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElementsV28 = webhookSteps.getWebhookB2bClient().listEventStreamsV28();
        for (StreamListElement elem : streamListElementsV28) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStreamV28(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            webhookSteps.getWebhookB2bClient().retrieveEventStreamV28(streamID);
            webhookSteps.setNotificationError(e);
            webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion V28 pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequest = new StreamRequestV28();
        streamRequest.setTitle("Update Stream V28");
        streamRequest.setEventType(StreamRequestV28.EventTypeEnum.TIMELINE);
        webhookSteps.getWebhookB2bClient().updateEventStreamV28(idStream, streamRequest);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        webhookSteps.getWebhookB2bClient().updateEventStreamV28(idStream, streamRequest);
    }

    @Override
    public void updateStreams() {
        if (streamRequest == null) {
            streamRequest = new StreamRequestV28();
            streamRequest.setGroups(webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequest.setTitle("Update Stream V28");
        streamRequest.setEventType(StreamRequestV28.EventTypeEnum.TIMELINE);
        streamRequest.setWaitForAccepted(waitForAccepted);
        for (StreamMetadataResponseV28 eventStreamV28 : eventStreamList) {
            StreamMetadataResponseV28 result = webhookSteps.getWebhookB2bClient().updateEventStreamV28(eventStreamV28.getStreamId(), streamRequest);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequest.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        Assertions.assertFalse(progressResponseElementList.stream().map(ProgressResponseElementV28::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        streamRequest = new StreamRequestV28();
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
            streamRequest = new StreamRequestV28();
        }
        this.streamRequest = (StreamRequestV28) streamRequest;
        this.streamRequest.setTitle("Update Stream V28");
        this.streamRequest.setEventType(StreamRequestV28.EventTypeEnum.TIMELINE);
        return this.streamRequest;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElementsV28 = webhookSteps.getWebhookB2bClient().listEventStreamsV28();
        for (StreamMetadataResponseV28 eventStream : eventStreamList) {
            StreamListElement streamListElementV28 = streamListElementsV28.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV28);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV28 eventStreamV28 = Assertions.assertDoesNotThrow(() ->
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV28(streamId));
        Assertions.assertNotNull(eventStreamV28);
        Assertions.assertNotNull(eventStreamV28.getStreamId());
        Assertions.assertEquals(waitForAccepted, eventStreamV28.getWaitForAccepted());
        log.info("EVENTSTREAM: {}", eventStreamV28);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStreamV28(streamId, null);
        log.info("progressResponseElementsV28 size: " + progressResponseElementList.size());
        log.info("progressResponseElementsV28: " + progressResponseElementList);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = eventStreamList.get(0).getStreamId();
        progressResponseElementList = webhookSteps.getWebhookB2bClient().consumeEventStreamV28(streamId, null);
        log.info("progressResponseElementsV28: " + progressResponseElementList);
        Assertions.assertEquals(progressResponseElementList.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementList.size());
    }

    @Override
    public void verifyNoEventsInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(webhookSteps.getWebhookB2bClient().consumeEventStreamV28(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (eventStreamList == null) eventStreamList = new LinkedList<>();
        for (StreamCreationRequestV28 request : streamCreationRequestList) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (streamIdToReplace != null) {
                request.setReplacedStreamId(streamIdToReplace);
            }
            request.setWaitForAccepted(waitForAccepted);
            StreamMetadataResponseV28 eventStream = webhookSteps.getWebhookB2bClient().createEventStreamV28(request);
            if (streamIdToReplace != null) {
                eventStreamList = new LinkedList<>();
            }
            eventStreamList.add(eventStream);
            webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStream(UUID streamId) {
        webhookSteps.getWebhookB2bClient().disableEventStreamV28(streamId);
    }

    @Override
    public void disableStreams() {
        eventStreamList.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV28 response = webhookSteps.getWebhookB2bClient().disableEventStreamV28(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchTimelineElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV26 timeLineOrStatus = ((TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV28 webhook = (PnPollingServiceWebhookV28) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V28);
        PnPollingResponseV28 pnPollingResponse = webhook.waitForEvent(webhookSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V26: " + pnPollingResponse.getProgressResponseElement());
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
        PnPollingServiceWebhookV28 webhook = (PnPollingServiceWebhookV28) webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V28);
        PnPollingResponseV28 pnPollingResponse = webhook.waitForEvent(webhookSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamList.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V28: " + pnPollingResponse.getProgressResponseElement());
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

            FullSentNotificationV26 fullSentNotification = webhookSteps.getSharedSteps().getSentNotificationLastVersion();
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElement = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
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
            FullSentNotificationV26 fullSentNotification = webhookSteps.getSharedSteps().getSentNotificationLastVersion();
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
            Assertions.assertNotNull(progressResponseElement);
            TimelineElementCategoryV26 timelineElementInternalCategory = TimelineElementCategoryV26.valueOf(((TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory()).name());
            FullSentNotificationV26 fullSentNotification = webhookSteps.getSharedSteps().getSentNotificationLastVersion();
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 elementToCheck = fullSentNotification.getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);
            ProgressResponseElementV28 convertedProgressResponseElement = ((ProgressResponseElementV28) progressResponseElement);
            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement());
            Assertions.assertNotNull(convertedProgressResponseElement.getElement().getTimestamp());
            Assertions.assertEquals(convertedProgressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            String message = String.format("%s {IUN: %s -WEBHOOK %s }",
                    assertionFailedError.getMessage(), webhookSteps.getSharedSteps().getNotificationIun(), eventStreamList.get(0).getStreamId());

            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void setValueForWaitForAccepted(boolean bool) {
        waitForAccepted = bool;
    }

    @Override
    public void verifyIncrementalEventId() {
        Assertions.assertNotNull(progressResponseElementList);
        int lastEventID = 0;
        for (ProgressResponseElementV28 elem : progressResponseElementList) {
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
            pnPollingWebhook.setTimelineElementCategoryV28((TimelineElementCategoryV27) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV28(progressResponseElementList);
        } else if (timeLineOrStatus instanceof NotificationStatusV26) {
            pnPollingWebhook.setNotificationStatusV28((NotificationStatusV26) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV28(progressResponseElementList);
        }
        return pnPollingWebhook;
    }

    @Override
    public void getTimelineElementVersionB2B(String iun) {
        webhookSteps.getB2bClient().getSentNotificationV27(iun);
    }

    @Override
    public void compareTimestampWebhook(String timelineElementCategory, String webhookElementCategory, boolean mustBeEqual) {
        FullSentNotificationV26 fullSentNotification = webhookSteps.getSharedSteps().getSentNotificationLastVersion();
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
        for (StreamMetadataResponseV28 stream : eventStreamList) {
            UUID streamId = stream.getStreamId();
            List<ProgressResponseElementV28> progressResponseElements = webhookSteps.getWebhookB2bClient().consumeEventStreamV28(streamId, null);
            System.out.println("progressResponseElements V28 size: " + progressResponseElements.size());
            webhookSteps.sleepTest(50L);
        }
    }

    @Override
    public void verificaDeanonimizzazioneEventiTimelineAnalogica(boolean withDelega) {
        Assertions.assertNotNull(progressResponseElement);
        TimelineElementDetailsV27 timelineElementWebhookDetails = progressResponseElement.getElement().getDetails();
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
        TimelineElementDetailsV27 timelineElementWebhookDetails = progressResponseElement.getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails.getDigitalAddress());
        if (withDelega) {
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getTaxId());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getDenomination());
        }
    }

    @Override
    public void setProgressResponseElement(Object progressResponseElement) {
        this.progressResponseElement = (ProgressResponseElementV28) progressResponseElement;
    }

    @Override
    public List<Object> verificaCorrispondenzaElementiTimelineWebhookAndB2B() {
        List<Object> resultList = new LinkedList<>();

        TimelineElementV27 timelineElementWebHook = progressResponseElement.getElement();
        Assertions.assertNotNull(timelineElementWebHook);
        Assertions.assertNotNull(timelineElementWebHook.getCategory());

        String elementId = timelineElementWebHook.getCategory().toString();
        FullSentNotificationV26 fullSentNotification = webhookSteps.getSharedSteps().getSentNotificationLastVersion();
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElement = fullSentNotification.getTimeline().
                stream()
                .filter(data -> data.getCategory() != null)
                .filter(data -> data.getCategory().getValue().equalsIgnoreCase(elementId))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(timelineElement);

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementDetailsV26 timelineElementDetails = timelineElement.getDetails();
        Assertions.assertNotNull(timelineElementDetails);
        resultList.add(timelineElementDetails);

        TimelineElementDetailsV27 timelineElementWebhookDetails = timelineElementWebHook.getDetails();
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
        StreamMetadataResponseV28 eventStream =
                webhookSteps.getWebhookB2bClient().retrieveEventStreamV28(streamId);
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

    public void checkLegalFactCategory(String timelineCategory, String legalFactCategory) {
        String iun = webhookSteps.getSharedSteps().getNotificationIun();
        FullSentNotificationV27 fullSentNotification = webhookSteps.getB2bClient().getSentNotificationV27(iun);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElementWithTargetCategory =
                fullSentNotification.getTimeline().stream().filter(
                        x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
        Assertions.assertNotNull(timelineElementWithTargetCategory);
        timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                x -> Assertions.assertNotEquals(x.getCategory(), legalFactCategory));
    }
}
