package it.pagopa.pn.cucumber.steps.pa.webhookVersions;


import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamMetadataResponseV23;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
public class WebhookStepsV10_23 implements WebhookStepsInterface {

    private List<StreamMetadataResponse> eventStreamList;
    private List<StreamMetadataResponseV23> eventStreamListV23;

    private StreamCreationRequest streamRequest;
    private List<ProgressResponseElement> progressResponseElements;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV10_23(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
        progressResponseElements = new LinkedList<>();
    }

    @Override
    public void initializeStreamRequest(String action, String pa) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void createStreamRequest(List<String> filterValues, int number, String title, String eventType) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void deleteStreams(String pa) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void cleanWebHookDelete() {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void deleteStreamsBeforeTest(String pa) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void updateStreamCreatingNewRequest(UUID idStream) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void updateStreamWithExistingRequest(UUID idStream) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void updateStreams() {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void verifySpecificEventNotInStream(String elementType) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void createStreamRequestWithGroupsPA(List<String> groupIdByPa) {
        // Not implemented as it is not currently needed.
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
        // Not implemented as it is not currently needed.
        return null;
    }

    @Override
    public void checkCorrectCancellation() {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void getStreamById(UUID streamId) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void consumeEventStream(UUID streamId) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void consumeEventStreamAndCheckNumEvents(int numEvents) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void verifyNoEventInStream() {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced) {
        if (this.eventStreamListV23 == null) this.eventStreamListV23 = new LinkedList<>();
        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamCreationRequestV23 request = new it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamCreationRequestV23();
        if (filteredValues != null && !filteredValues.isEmpty()) {
            request.setFilterValues(filteredValues);
        }
        if (listGroups != null) {
            request.setGroups(listGroups);
        }

        if (streamIdToReplace != null) {
            request.setReplacedStreamId(this.webhookSteps.getSharedSteps().getEventStream().getStreamId());
        }
        StreamMetadataResponseV23 eventStream = webhookSteps.getWebhookB2bClient().createEventStreamV23(request);
        if (streamIdToReplace != null) {
            StreamMetadataResponseV23 eventStreamV23 = Assertions.assertDoesNotThrow(() ->
                    webhookSteps.getWebhookB2bClient().getEventStreamV23(this.eventStreamList.get(0).getStreamId()));
            this.webhookSteps.getSharedSteps().setEventStreamV23(eventStreamV23);
            Assertions.assertNotNull(eventStreamV23);
            Assertions.assertNotNull(eventStreamV23.getStreamId());
            Assertions.assertNotNull(eventStreamV23.getDisabledDate());
            log.info("EVENTSTREAM REPLACED: {}", eventStreamV23);
        }
        this.eventStreamListV23.add(eventStream);
        webhookSteps.getPaStreamOwner().add(pa);
    }

    @Override
    public void disableStreams() {
        // Not implemented as it is not currently needed.
    }

    @Override
    public Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        // Not implemented as it is not currently needed.
        return null;
    }

    @Override
    public boolean checkInternalTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        // Not implemented as it is not currently needed.
        return false;
    }

    @Override
    public <T> void verifyAssertions(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream, T progressResponseElement) {
        // Not implemented as it is not currently needed.
    }

    @Override
    public void setValueForWaitForAccepted(boolean waitForAccepted) {
        // Feature available from version 27 onwards.
    }

    @Override
    public String getSentNotificationIun() {
        return webhookSteps.getSharedSteps().getSentNotificationV1().getIun();
    }

    @Override
    public void verifyIncrementalEventId() {
        // Not implemented as it is not currently needed.
    }

    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T> getTimelineEventForStream(String timelineEventCategory, TimingForPolling.TimingResult timingForElement) {
        // Not implemented as it is not currently needed.
        return null;
    }

    @Override
    public <T> AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T> getStatusEventForStream(String notificationStatusName, TimingForPolling.TimingResult timingForElement) {
        // Not implemented as it is not currently needed.
        return null;
    }

    @Override
    public <T> PnPollingWebhook getPnPollingWebhook(T timeLineOrStatus) {
        // Not implemented as it is not currently needed.
        return null;
    }


}
