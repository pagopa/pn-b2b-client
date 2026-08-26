package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.api.NotificationReworkApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReworkTimelineClientImpl {
    private final NotificationReworkApi reworkApi;

    public ReworkTimelineClientImpl(RestTemplate restTemplate,
                                    @Value("${pn.delivery.base-url}") String basePath) {
        this.reworkApi = new NotificationReworkApi(createApi(restTemplate, basePath));
    }

    private ApiClient createApi(RestTemplate restTemplate, String basePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    public ReworkResponse notificationRework(String iun, ReworkRequest reworkRequest) throws RestClientException {
        return reworkApi.notificationRework(iun, reworkRequest);
    }

    public ReworkItemsResponse retrieveNotificationReworkById(String iun, String reworkId) throws RestClientException {
        return reworkApi.retrieveNotificationRework(iun, reworkId);
    }

    public void updateNotificationRework(String iun, String reworkId, UpdateReworkRequest updateReworkRequest) throws RestClientException {
        reworkApi.updateNotificationRework(iun, reworkId, updateReworkRequest);
    }

    public RestartAttemptResponse restartAttempt(String iun, RestartAttemptRequest restartAttemptRequest) throws RestClientException {
        return reworkApi.restartAttempt(iun, restartAttemptRequest);
    }

    public InvalidateTimelineElementsResponse invalidateTimelineElements(String iun, InvalidateTimelineElementsRequest invalidateTimelineElementsRequest) throws RestClientException {
        return reworkApi.invalidateTimelineElements(iun, invalidateTimelineElementsRequest);
    }
}
