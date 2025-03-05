package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.WebhookStepsInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpStatusCodeException;

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
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        streamRequestV24 = new StreamRequestV24();
        List<String> groups = switch (action.toLowerCase()) {
            case "rimuove" -> (this.webhookSteps.getSharedSteps().getRequestNewApiKey() != null
                    && this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
                    this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
            case "aggiunge" -> this.webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
            case "stesso" ->
                    eventStreamListV24.stream().findFirst().map(StreamMetadataResponseV24::getGroups).orElse(null);
            default -> throw new IllegalArgumentException("Action not supported!: " + action);
        };
        streamRequestV24.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        this.streamCreationRequestListV24 = new LinkedList<>();
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
            this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamID);
            this.webhookSteps.setNotificationError(e);
            this.webhookSteps.getSharedSteps().setNotificationError(e);
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
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV24(idStream, streamRequestV24);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        this.webhookSteps.getWebhookB2bClient().updateEventStreamV24(idStream, streamRequestV24);
    }

    @Override
    public void updateStreams() {
        if (streamRequestV24 == null) {
            streamRequestV24 = new StreamRequestV24();
            streamRequestV24.setGroups(this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups());
        }
        streamRequestV24.setTitle("Update Stream V24");
        streamRequestV24.setEventType(StreamRequestV24.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponseV24 eventStreamV24 : eventStreamListV24) {
            StreamMetadataResponseV24 result = this.webhookSteps.getWebhookB2bClient().updateEventStreamV24(eventStreamV24.getStreamId(), streamRequestV24);
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
        List<StreamListElement> streamListElementsV24 = this.webhookSteps.getWebhookB2bClient().listEventStreamsV24();
        for (StreamMetadataResponseV24 eventStream : eventStreamListV24) {
            StreamListElement streamListElementV24 = streamListElementsV24.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElementV24);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponseV24 eventStreamV24 = Assertions.assertDoesNotThrow(() ->
                this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(streamId));
        this.webhookSteps.getSharedSteps().setEventStreamV24(eventStreamV24);
        Assertions.assertNotNull(eventStreamV24);
        Assertions.assertNotNull(eventStreamV24.getStreamId());
        log.info("EVENTSTREAM: {}", eventStreamV24);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        this.progressResponseElementsV24 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null);
        log.info("progressResponseElementsV24: " + progressResponseElementsV24);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = this.eventStreamListV24.get(0).getStreamId();
        this.progressResponseElementsV24 = this.webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null);
        log.info("progressResponseElementsV24: " + progressResponseElementsV24);
        Assertions.assertEquals(progressResponseElementsV24.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElementsV24.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(this.webhookSteps.getWebhookB2bClient().consumeEventStreamV24(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        if (this.eventStreamListV24 == null) this.eventStreamListV24 = new LinkedList<>();
        for (StreamCreationRequestV24 request : streamCreationRequestListV24) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            if (listGroups != null) {
                request.setGroups(listGroups);
            }
            if (replaceId) {
                request.setReplacedStreamId(this.webhookSteps.getSharedSteps().getEventStreamV24().getStreamId());
            }
            StreamMetadataResponseV24 eventStream = this.webhookSteps.getWebhookB2bClient().createEventStreamV24(request);
            if (replaceId) {
                StreamMetadataResponseV24 eventStreamV24 =
                        this.webhookSteps.getWebhookB2bClient().retrieveEventStreamV24(this.eventStreamListV24.get(0).getStreamId());
                this.webhookSteps.getSharedSteps().setEventStreamV24(eventStreamV24);
                Assertions.assertNotNull(eventStreamV24);
                Assertions.assertNotNull(eventStreamV24.getStreamId());
                Assertions.assertNotNull(eventStreamV24.getDisabledDate());
                log.info("EVENTSTREAM REPLACED: {}", eventStreamV24);
                this.eventStreamListV24 = new LinkedList<>();
            }
            this.eventStreamListV24.add(eventStream);
            this.webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
        this.eventStreamListV24.forEach(s -> {
            UUID streamId = s.getStreamId();
            StreamMetadataResponseV24 response = this.webhookSteps.getWebhookB2bClient().disableEventStreamV24(streamId);
            Assertions.assertNotNull(response);
        });
    }
}
