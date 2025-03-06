package it.pagopa.pn.cucumber.steps.pa;

import java.util.List;
import java.util.UUID;

public interface WebhookStepsInterface {

    void initializeStreamRequest(String action, String pa);

    void createStreamRequest(List<String> filterValues, int number, String title, String eventType);

    void deleteStreams(String pa);

    void deleteStreamsBeforeTest(String pa);

    void updateStreamCreatingNewRequest(UUID idStream);

    void updateStreamWithExistingRequest(UUID idStream);

    void updateStreams();

    void verifySpecificEventNotInStream(String elementType);

    void createStreamRequestWithGroupsPA(List<String> groupIdByPa);

    UUID getStreamId();

    Object getStreamRequest();

    Object initStreamRequest(Object streamRequest);

    void checkCorrectCancellation();

    void getStreamById(UUID streamId);

    void consumeEventStream(UUID streamId);

    void consumeEventStreamAndCheckNumEvents(int numEvents);

    void verifyNoEventInStream();

    void createEventStream(String pa, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced);

    void disableStreams();

    default Object searchInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        return null;
    }

    default boolean checkInternalTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream) {
        return false;
    }

    default <T> void verifyAssertions(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream, T progressResponseElement) {
    }

    void setValueForWaitForAccepted(boolean waitForAccepted);

    String getSentNotificationIun();

}