package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.*;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.WebhookStepsInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
        this.progressResponseElementsV23 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV23 = new StreamRequestV23();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (this.webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> this.webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV23.stream().findFirst().map(StreamMetadataResponseV23::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV23.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        this.streamCreationRequestListV23 = new LinkedList<>();
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
            this.webhookSteps.getWebhookB2bClient().getEventStreamV23(streamID);
            this.webhookSteps.setNotificationError(e);
            this.webhookSteps.getSharedSteps().setNotificationError(e);
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
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV23(idStream, streamRequestV23);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV23(idStream, streamRequestV23);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV23 == null) {
            streamRequestV23 = new StreamRequestV23();
            streamRequestV23.setGroups(this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV23.setTitle("Update Stream V23");
        streamRequestV23.setEventType(StreamRequestV23.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV23 eventStreamV23 : eventStreamListV23) {
            StreamMetadataResponseV23 result = this.webhookSteps.getWebhookB2bClient().updateEventStreamV23(eventStreamV23.getStreamId(), streamRequestV23);
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
        List<StreamListElement> streamListElementsV23 = this.webhookSteps.getWebhookB2bClient().listEventStreamsV23();
        for (StreamMetadataResponseV23 eventStream : eventStreamListV23) {
            StreamListElement streamListElementV23 = streamListElementsV23.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV23);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV23 eventStreamV23 = Assertions.assertDoesNotThrow(() ->
                this.webhookSteps.getWebhookB2bClient().getEventStreamV23(streamId));
        this.webhookSteps.getSharedSteps().setEventStreamV23(eventStreamV23);
        Assertions.assertNotNull(eventStreamV23);
        Assertions.assertNotNull(eventStreamV23.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV23);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        this.progressResponseElementsV23 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV23(streamId, null);
        log.info("progressResponseElementsV23: " + progressResponseElementsV23);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = this.eventStreamListV23.get(0).getStreamId();
        this.progressResponseElementsV23 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV23(streamId, null);
        log.info("progressResponseElementsV23: " + progressResponseElementsV23);
        Assertions.assertEquals(progressResponseElementsV23.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV23.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(this.webhookSteps.getWebhookB2bClient().consumeEventStreamV23(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        if (this.eventStreamListV23 == null) this.eventStreamListV23 = new LinkedList<>();
        for (StreamCreationRequestV23 request : streamCreationRequestListV23) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (replaceId) {
                request.setReplacedStreamId(this.webhookSteps.getSharedSteps().getEventStreamV23().getStreamId());
            }
            StreamMetadataResponseV23 eventStream = this.webhookSteps.getWebhookB2bClient().createEventStreamV23(request);
            if (replaceId) {
                StreamMetadataResponseV23 eventStreamV23 =
                        this.webhookSteps.getWebhookB2bClient().getEventStreamV23(this.eventStreamListV23.get(0).getStreamId());
                this.webhookSteps.getSharedSteps().setEventStreamV23(eventStreamV23);
                Assertions.assertNotNull(eventStreamV23);
                Assertions.assertNotNull(eventStreamV23.getStreamId());
                Assertions.assertNotNull(eventStreamV23.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV23);
                this.eventStreamListV23 = new LinkedList<>();
            }
            this.eventStreamListV23.add(eventStream);
            this.webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        this.eventStreamListV23.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV23 response = this.webhookSteps.getWebhookB2bClient().disableEventStreamV23(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        TimelineElementCategoryV23 timeLineOrStatus = ((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV23 webhookV23 = (PnPollingServiceWebhookV23) this.webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V23);
        PnPollingResponseV23 pnPollingResponseV23 = webhookV23.waitForEvent(this.webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV23.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V23: " + pnPollingResponseV23.getProgressResponseElementV23());
        if (pnPollingResponseV23.getProgressResponseElementsV23() != null) {
            this.webhookSteps.getSharedSteps().setProgressResponseElementsV23(pnPollingResponseV23.getProgressResponseElementsV23());
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
            this.webhookSteps.getSharedSteps().setSentNotificationV23(this.webhookSteps.getB2bClient().getSentNotificationV23(this.webhookSteps.getSharedSteps().getSentNotification().getIun()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 timelineElement = this.webhookSteps.getSharedSteps()
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
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(((TimelineElementCategoryV23) timelineForStream.getTimelineElementCategory()).name());

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 elementToCheck = this.webhookSteps.getSharedSteps().getSentNotificationV23().getTimeline().stream()
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
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }

    @Override
    public String getSentNotificationIun() {
        return this.webhookSteps.getSharedSteps().getSentNotificationV23().getIun();
    }
}
