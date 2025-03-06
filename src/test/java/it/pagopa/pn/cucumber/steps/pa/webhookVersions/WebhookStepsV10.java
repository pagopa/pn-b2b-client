package it.pagopa.pn.cucumber.steps.pa.webhookVersions;


import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
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
public class WebhookStepsV10 implements WebhookStepsInterface {

    private List<StreamCreationRequest> streamCreationRequestList;
    private List<StreamMetadataResponse> eventStreamList;
    private StreamCreationRequest streamRequest;
    private List<ProgressResponseElement> progressResponseElements;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV10(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
//        streamRequest = new StreamRequest();
//        List<String> groups = switch (action.toLowerCase()) {
//            case "rimuove" -> (this.webhookSteps.getSharedSteps().getRequestNewApiKey() != null
//                    && this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().size() >= 2) ?
//                    this.webhookSteps.getSharedSteps().getRequestNewApiKey().getGroups().subList(0, 0) : null;
//            case "aggiunge" -> this.webhookSteps.getSharedSteps().getGroupAllActiveByPa(pa);
//            case "stesso" ->
//                    eventStreamList.stream().findFirst().map(StreamMetadataResponse::getGroups).orElse(null);
//            default -> throw new IllegalArgumentException("Action not supported!: " + action);
//        };
//        streamRequest.setGroups(groups);
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        this.streamCreationRequestList = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            StreamCreationRequest streamRequest = new StreamCreationRequest();
            streamRequest.setTitle(title + "_" + i);
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE);
            streamRequest.setFilterValues(filterValues);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @Override
    public void deleteStreams(String pa) {
        if (eventStreamList != null) {
            for (StreamMetadataResponse eventStream : eventStreamList) {
                deleteStream(eventStream.getStreamId(), pa);
            }
        }
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        List<StreamListElement> streamListElements = webhookSteps.getWebhookB2bClient().listEventStreams();
        for (StreamListElement elem : streamListElements) {
            deleteStream(elem.getStreamId(), pa);
        }
    }

    private boolean deleteStream(UUID streamId, String pa) {
        try {
            webhookSteps.getWebhookB2bClient().deleteEventStream(streamId);
            return true;
        } catch (HttpStatusCodeException e) {
            return handleException(e, pa, streamId);
        }
    }

    private boolean handleException(HttpStatusCodeException e, String pa, UUID streamID) {
        try {
            this.webhookSteps.getWebhookB2bClient().getEventStream(streamID);
            this.webhookSteps.setNotificationError(e);
            this.webhookSteps.getSharedSteps().setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion  pa {}", streamID, pa);
            return false;
        } catch (HttpStatusCodeException ex) {
            log.info("Not needed to remove since stream found has different version!");
            return true;
        }
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        streamRequest = new StreamCreationRequest();
        streamRequest.setTitle("Update Stream ");
        streamRequest.setEventType(StreamCreationRequest.EventTypeEnum.TIMELINE);
        this.webhookSteps.getWebhookB2bClient().updateEventStream(idStream, streamRequest);
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        this.webhookSteps.getWebhookB2bClient().updateEventStream(idStream, streamRequest);
    }

    @Override
    public void updateStreams() {
        if (streamRequest == null) {
            streamRequest = new StreamCreationRequest();
        }
        streamRequest.setTitle("Update Stream ");
        streamRequest.setEventType(StreamCreationRequest.EventTypeEnum.TIMELINE);
        for (StreamMetadataResponse eventStream : eventStreamList) {
            StreamMetadataResponse result = this.webhookSteps.getWebhookB2bClient().updateEventStream(eventStream.getStreamId(), streamRequest);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(streamRequest.getTitle().equalsIgnoreCase(result.getTitle()));
            log.info("EVENTSTREAM update : {}", result);
        }
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
//        Assertions.assertFalse(progressResponseElements.stream().map(ProgressResponseElement::getElement).anyMatch(x -> x.getElementId().contains(elementType)));
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
//        streamRequest = new StreamCreationRequest();
//        streamRequest.setGroups(groupIdByPa);
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
//        if (streamRequest == null) {
//            streamRequest = new StreamRequest();
//        }
//        streamRequest = (StreamRequest) streamRequest;
//        streamRequest.setTitle("Update Stream ");
//        streamRequest.setEventType(StreamRequest.EventTypeEnum.TIMELINE);
//        return streamRequest;
        return null;
    }

    @Override
    public void checkCorrectCancellation() {
        List<StreamListElement> streamListElements = this.webhookSteps.getWebhookB2bClient().listEventStreams();
        for (StreamMetadataResponse eventStream : eventStreamList) {
            StreamListElement streamListElement = streamListElements.stream().filter(
                    elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElement);
        }
    }

    @Override
    public void getStreamById(UUID streamId) {
        StreamMetadataResponse eventStream = Assertions.assertDoesNotThrow(() ->
                this.webhookSteps.getWebhookB2bClient().getEventStream(streamId));
        this.webhookSteps.getSharedSteps().setEventStream(eventStream);
        Assertions.assertNotNull(eventStream);
        Assertions.assertNotNull(eventStream.getStreamId());
        log.info("EVENTSTREAM: {}", eventStream);
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        this.progressResponseElements = this.webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null);
        log.info("progressResponseElements: " + progressResponseElements);
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        UUID streamId = this.eventStreamList.get(0).getStreamId();
        this.progressResponseElements = this.webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null);
        log.info("progressResponseElements: " + progressResponseElements);
        Assertions.assertEquals(progressResponseElements.size(), numEvents);
        System.out.println("ELEMENTI NEL WEBHOOK: " + progressResponseElements.size());
    }

    @Override
    public void verifyNoEventInStream() {
        UUID streamId = getStreamId();
        Assertions.assertTrue(this.webhookSteps.getWebhookB2bClient().consumeEventStream(streamId, null).isEmpty());
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        if (this.eventStreamList == null) this.eventStreamList = new LinkedList<>();
        for (StreamCreationRequest request : streamCreationRequestList) {
            if (filteredValues != null && !filteredValues.isEmpty()) {
                request.setFilterValues(filteredValues);
            }
            StreamMetadataResponse eventStream = this.webhookSteps.getWebhookB2bClient().createEventStream(request);
            this.eventStreamList.add(eventStream);
            this.webhookSteps.getPaStreamOwner().add(pa);
        }
    }

    @Override
    public void disableStreams() {
//        this.eventStreamList.forEach(s -> {
//            UUID streamId = s.getStreamId();
//            StreamMetadataResponse response = this.webhookSteps.getWebhookB2bClient().disableEventStream(streamId);
//            Assertions.assertNotNull(response);
//        });
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        //Funzionalità prevista dalla versione 27 in poi
    }

    @Override
    public String getSentNotificationIun() {
        return this.webhookSteps.getSharedSteps().getSentNotificationV1().getIun();
    }
}
