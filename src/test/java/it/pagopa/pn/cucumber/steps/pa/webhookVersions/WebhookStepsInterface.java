package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;

import java.util.List;
import java.util.UUID;

public interface WebhookStepsInterface {

    void initializeStreamRequest(String action, String pa);

    void createStreamRequest(List<String> filterValues, int number, String title, String eventType);

    Object retrieveStreamEvent(UUID streamId);

    void deleteStream(UUID streamId);

    void deleteStreams(String pa);

    void cleanWebHookDelete();

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

    void verifyNoEventsInStream();

    void createEventStream(String pa, List<String> listGroups, UUID streamIdToReplace, List<String> filteredValues, boolean forced);

    void disableStream(UUID streamId);

    void disableStreams();

    Object searchTimelineElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream);

    Object searchStatusElementInWebhook(String lastEventId, int deepCount, int position, AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream);

    boolean checkTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream);

    boolean checkStatus(AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<?> statusForStream);

    <T> void verifyAssertionsTimeline(AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<?> timelineForStream, T progressResponseElement);

    void setValueForWaitForAccepted(boolean waitForAccepted);

    void verifyIncrementalEventId();

    <T> AvanzamentoNotificheWebhookB2bSteps.TimelineElementSearchResult<T> getTimelineEventForStream(String timelineEventCategory, TimingForPolling.TimingResult timingForElement);

    <T> AvanzamentoNotificheWebhookB2bSteps.StatusElementSearchResult<T> getStatusEventForStream(String notificationStatusName, TimingForPolling.TimingResult timingForElement);

    <T> PnPollingWebhook getPnPollingWebhook(T timeLineOrStatus);

    void getTimelineElementVersionB2B(String iun);

    void compareTimestampWebhook(String timelineElementCategory, String webhookElementCategory, boolean mustBeEqual);

    void getStreamEventListForStressTest();

    void verificaDeanonimizzazioneEventiTimelineAnalogica(boolean delega);

    void verificaDeanonimizzazioneEventiTimelineDigitale(boolean delega);

    void setProgressResponseElement(Object progressResponseElement);

    List<Object> verificaCorrispondenzaElementiTimelineWebhookAndB2B();

    void checkLegalFactId();

    void checkCorrectDisabling(UUID streamId);

    void verificaPresenzaSercQ(boolean isPresent);

    void checkLegalFactCategory(String timelineCategory, String legalFactCategory);
}