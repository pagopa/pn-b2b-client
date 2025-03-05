package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.*;
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
public class WebhookStepsV26 implements WebhookStepsInterface {

    private List<StreamCreationRequestV26> streamCreationRequestListV26;
    private List<StreamMetadataResponseV26> eventStreamListV26;
    private StreamRequestV26 streamRequestV26;
    private List<ProgressResponseElementV26> progressResponseElementsV26;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV26(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        this.progressResponseElementsV26 = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV26 = new StreamRequestV26();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (this.webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> this.webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV26.stream().findFirst().map(StreamMetadataResponseV26::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV26.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        this.streamCreationRequestListV26 = new LinkedList<>();
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
            this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV26(streamID);
            this.webhookSteps.setNotificationError(e);
            this.webhookSteps.getSharedSteps().setNotificationError(e);
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
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV26(idStream, streamRequestV26);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV26(idStream, streamRequestV26);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV26 == null) {
            streamRequestV26 = new StreamRequestV26();
            streamRequestV26.setGroups(this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV26.setTitle("Update Stream V26");
        streamRequestV26.setEventType(StreamRequestV26.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV26 eventStreamV26 : eventStreamListV26) {
            StreamMetadataResponseV26 result = this.webhookSteps.getWebhookB2bClient().updateEventStreamV26(eventStreamV26.getStreamId(), streamRequestV26);
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
        List<StreamListElement> streamListElementsV26 = this.webhookSteps.getWebhookB2bClient().listEventStreamsV26();
        for (StreamMetadataResponseV26 eventStream : eventStreamListV26) {
            StreamListElement streamListElementV26 = streamListElementsV26.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV26);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV26 eventStreamV26 = Assertions.assertDoesNotThrow(() ->
                this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV26(streamId));
        this.webhookSteps.getSharedSteps().setEventStreamV26(eventStreamV26);
        Assertions.assertNotNull(eventStreamV26);
        Assertions.assertNotNull(eventStreamV26.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV26);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        this.progressResponseElementsV26 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV26(streamId, null);
        log.info("progressResponseElementsV26: " + progressResponseElementsV26);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = this.eventStreamListV26.get(0).getStreamId();
        this.progressResponseElementsV26 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV26(streamId, null);
        log.info("progressResponseElementsV26: " + progressResponseElementsV26);
        Assertions.assertEquals(progressResponseElementsV26.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV26.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(this.webhookSteps.getWebhookB2bClient().consumeEventStreamV26(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        if (this.eventStreamListV26 == null) this.eventStreamListV26 = new LinkedList<>();
        for (StreamCreationRequestV26 request : streamCreationRequestListV26) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (replaceId) {
                request.setReplacedStreamId(this.webhookSteps.getSharedSteps().getEventStreamV26().getStreamId());
            }

            StreamMetadataResponseV26 eventStream = this.webhookSteps.getWebhookB2bClient().createEventStreamV26(request);
            if (replaceId) {
                StreamMetadataResponseV26 eventStreamV26 =
                        this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV26(this.eventStreamListV26.get(0).getStreamId());
                this.webhookSteps.getSharedSteps().setEventStreamV26(eventStreamV26);
                Assertions.assertNotNull(eventStreamV26);
                Assertions.assertNotNull(eventStreamV26.getStreamId());
                Assertions.assertNotNull(eventStreamV26.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV26);
                this.eventStreamListV26 = new LinkedList<>();
            }
            this.eventStreamListV26.add(eventStream);
            this.webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        this.eventStreamListV26.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV26 response = this.webhookSteps.getWebhookB2bClient().disableEventStreamV26(streamId);
            Assertions.assertNotNull(response);
        });
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timeLineOrStatus = ((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26) timelineForStream.getTimelineElementCategory());
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV26 webhookV26 = (PnPollingServiceWebhookV26) this.webhookSteps.getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V26);
        PnPollingResponseV26 pnPollingResponseV26 = webhookV26.waitForEvent(this.webhookSteps.getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV26.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V26: " + pnPollingResponseV26.getProgressResponseElementV26());
        if (pnPollingResponseV26.getProgressResponseElementListV26() != null) {
            this.webhookSteps.getSharedSteps().setProgressResponseElementsV26(pnPollingResponseV26.getProgressResponseElementListV26());
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

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }
}
