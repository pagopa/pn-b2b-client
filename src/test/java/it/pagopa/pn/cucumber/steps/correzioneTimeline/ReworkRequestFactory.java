package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.InvalidateTimelineElementsRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.RestartAttemptRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.UpdateReworkRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Factory di sole request verso il client di rework/restart timeline.
 * Logica pura senza stato, estratta da {@link TimelineReworkSteps} per alleggerire la classe di step.
 */
@Slf4j
final class ReworkRequestFactory {

    static ReworkRequest defaultReworkRequest() {
        return reworkRequest(ReworkRequest.AttemptIdEnum._0, "reason", "PCRETRY_0", "RECINDEX_0", "RECRI003C", null);
    }

    static ReworkRequest reworkRequest(ReworkRequest.AttemptIdEnum attemptId,
                                       String reason,
                                       String pcRetry,
                                       String recIndex,
                                       String expectedStatusCode,
                                       String expectedDeliveryFailureCause) {
        ReworkRequest reworkRequest = new ReworkRequest();
        reworkRequest.setAttemptId(attemptId);
        reworkRequest.setExpectedDeliveryFailureCause(expectedDeliveryFailureCause);
        reworkRequest.setReason(reason);
        reworkRequest.setPcRetry(pcRetry);
        reworkRequest.setRecIndex(recIndex);
        reworkRequest.setExpectedStatusCode(expectedStatusCode);
        log.info("REWORK request built: {}", reworkRequest);
        return reworkRequest;
    }

    static RestartAttemptRequest defaultRestartRequest() {
        return restartRequest(RestartAttemptRequest.AttemptIdEnum._0, "RECINDEX_0", "reasonTest", "TEST-12345", "false");
    }

    static RestartAttemptRequest restartRequest(RestartAttemptRequest.AttemptIdEnum attemptId,
                                                String recIndex,
                                                String reason,
                                                String task,
                                                String canInvalidateViewed) {
        RestartAttemptRequest restartAttemptRequest = new RestartAttemptRequest();
        restartAttemptRequest.setAttemptId(attemptId);
        restartAttemptRequest.setRecIndex(recIndex);
        restartAttemptRequest.setReason(reason);
        restartAttemptRequest.setTask(task);
        restartAttemptRequest.setCanInvalidateViewed(canInvalidateViewed != null ? Boolean.parseBoolean(canInvalidateViewed) : null);
        log.info("RESTART request built: %s", restartAttemptRequest);
        return restartAttemptRequest;
    }

    static InvalidateTimelineElementsRequest invalidationRequest(String recIndex, List<String> timelineELementsId) {
        InvalidateTimelineElementsRequest invalidationRequest = new InvalidateTimelineElementsRequest();
        invalidationRequest.setRecIndex(recIndex);
        invalidationRequest.setTimelineElementIds(timelineELementsId);
        log.info("INVALIDATION request built: %s", invalidationRequest);
        return invalidationRequest;
    }

    static InvalidateTimelineElementsRequest invalidationRequest(Map<String, String> inputData, String recIndex, FullSentNotificationV29 fsn) {
        List<String> timelineElementsId = new ArrayList<>();
        inputData.forEach((key, value) -> {
            if (key.contains("element")) {
                String[] filters = value.split(";");
                String category = filters[0];
                String recIndexFilter = Arrays.stream(filters).toList().stream().filter(x -> x.contains("RECINDEX_")).findFirst().orElse(null);
                String attemptFilter = Arrays.stream(filters).toList().stream().filter(x -> x.contains("ATTEMPT_")).findFirst().orElse(null);
                String timelineElementId = fsn.getTimeline().stream().filter(x ->
                                x.getCategory().getValue().equals(category)
                                        && (recIndexFilter != null ? x.getElementId().contains(recIndexFilter) : true)
                                        && (attemptFilter != null ? x.getElementId().contains(attemptFilter) : true))
                        .map(te -> te.getElementId())
                        .findFirst()
                        .orElse(null);
                if (timelineElementId != null) {
                    timelineElementsId.add(timelineElementId);
                }
            }
            if (key.contains("id") && value != null && !value.isEmpty()) {
                timelineElementsId.add(value);
            }
        });
        InvalidateTimelineElementsRequest invalidationRequest = new InvalidateTimelineElementsRequest();
        invalidationRequest.setRecIndex(recIndex);
        invalidationRequest.setTimelineElementIds(timelineElementsId);
        log.info("INVALIDATION request built: %s", invalidationRequest);
        return invalidationRequest;
    }

    static UpdateReworkRequest updateReworkRequest(String expectedStatusCode, String expectedDeliveryFailureCause) {
        UpdateReworkRequest request = new UpdateReworkRequest();
        if (expectedStatusCode != null) {
            request.setExpectedStatusCode(expectedStatusCode);
        }
        if (expectedDeliveryFailureCause != null) {
            request.setExpectedDeliveryFailureCause(expectedDeliveryFailureCause);
        }
        return request;
    }
}