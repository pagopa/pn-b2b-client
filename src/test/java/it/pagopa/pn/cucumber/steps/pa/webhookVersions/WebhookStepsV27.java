package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV27;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.*;
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
public class WebhookStepsV27 implements WebhookStepsInterface {
    private List<StreamCreationRequestV27> streamCreationRequestListV27;
    private List<StreamMetadataResponseV27> eventStreamListV27;
    private StreamRequestV27 streamRequestV27;
    private List<ProgressResponseElementV27> progressResponseElementsV27;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV27(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        this.progressResponseElementsV27 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV27 = new StreamRequestV27();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (this.webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> this.webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV27.stream().findFirst().map(StreamMetadataResponseV27::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV27.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        this.streamCreationRequestListV27 = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequestV27 streamRequest = new StreamCreationRequestV27();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequestV27.EventTypeEnum.STATUS : StreamCreationRequestV27.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestListV27.add(streamRequest);
            streamRequest.setWaitForAccepted(true);//Campo introdotto con la V27
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
            this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV27(streamID);
            this.webhookSteps.setNotificationError(e);
            this.webhookSteps.getSharedSteps().setNotificationError(e);
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
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV27(idStream, streamRequestV27);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV27(idStream, streamRequestV27);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV27 == null) {
            streamRequestV27 = new StreamRequestV27();
            streamRequestV27.setGroups(this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV27.setTitle("Update Stream V27");
        streamRequestV27.setEventType(StreamRequestV27.EventTypeEnum.TIMELINE);
        streamRequestV27.setWaitForAccepted(true);//campo introdotto con la V27
        for (StreamMetadataResponseV27 eventStreamV27 : eventStreamListV27) {
            StreamMetadataResponseV27 result = this.webhookSteps.getWebhookB2bClient().updateEventStreamV27(eventStreamV27.getStreamId(), streamRequestV27);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequestV27.getTitle().equalsIgnoreCase(result.getTitle()));
            Assertions.assertEquals(Boolean.TRUE, eventStreamV27.getWaitForAccepted());//campo introdotto con la V27
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
        List<StreamListElement> streamListElementsV27 = this.webhookSteps.getWebhookB2bClient().listEventStreamsV27();
        for (StreamMetadataResponseV27 eventStream : eventStreamListV27) {
            StreamListElement streamListElementV27 = streamListElementsV27.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV27);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV27 eventStreamV27 = Assertions.assertDoesNotThrow(() ->
                this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV27(streamId));
        this.webhookSteps.getSharedSteps().setEventStreamV27(eventStreamV27);
        Assertions.assertNotNull(eventStreamV27);
        Assertions.assertNotNull(eventStreamV27.getStreamId());
        Assertions.assertEquals(Boolean.TRUE, eventStreamV27.getWaitForAccepted());//campo introdotto con la V27
        log.info("EVENTSTREAM: {}", eventStreamV27);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        this.progressResponseElementsV27 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV27(streamId, null);
        log.info("progressResponseElementsV27: " + progressResponseElementsV27);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = this.eventStreamListV27.get(0).getStreamId();
        this.progressResponseElementsV27 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV27(streamId, null);
        log.info("progressResponseElementsV27: " + progressResponseElementsV27);
        Assertions.assertEquals(progressResponseElementsV27.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV27.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(this.webhookSteps.getWebhookB2bClient().consumeEventStreamV27(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        if (this.eventStreamListV27 == null) this.eventStreamListV27 = new LinkedList<>();
        for (StreamCreationRequestV27 request : streamCreationRequestListV27) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (replaceId) {
                request.setReplacedStreamId(this.webhookSteps.getSharedSteps().getEventStreamV27().getStreamId());
            }
            request.setWaitForAccepted(true);//campo introdotto con la V27
            StreamMetadataResponseV27 eventStream = this.webhookSteps.getWebhookB2bClient().createEventStreamV27(request);
            if (replaceId) {
                StreamMetadataResponseV27 eventStreamV27 =
                        this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV27(this.eventStreamListV27.get(0).getStreamId());
                this.webhookSteps.getSharedSteps().setEventStreamV27(eventStreamV27);
                Assertions.assertNotNull(eventStreamV27);
                Assertions.assertNotNull(eventStreamV27.getStreamId());
                Assertions.assertNotNull(eventStreamV27.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV27);
                this.eventStreamListV27 = new LinkedList<>();
            }
            this.eventStreamListV27.add(eventStream);
            this.webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        this.eventStreamListV27.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV27 response = this.webhookSteps.getWebhookB2bClient().disableEventStreamV27(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timeLineOrStatus = ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV27 webhookV27 = (PnPollingServiceWebhookV27) this.webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V27);
        PnPollingResponseV27 pnPollingResponseV27 = webhookV27.waitForEvent(this.webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV27.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V26: " + pnPollingResponseV27.getProgressResponseElementV27());
        if (pnPollingResponseV27.getProgressResponseElementListV27() != null) {
            this.webhookSteps.getSharedSteps().setProgressResponseElementsV27(pnPollingResponseV27.getProgressResponseElementListV27());
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
            this.webhookSteps.getSharedSteps().setSentNotification(this.webhookSteps.getB2bClient().getSentNotification(this.webhookSteps.getSharedSteps().getSentNotification().getIun()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElement = this.webhookSteps.getSharedSteps()
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
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory()).name());

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 elementToCheck = this.webhookSteps.getSharedSteps().getSentNotification().getTimeline().stream()
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
    }
}
