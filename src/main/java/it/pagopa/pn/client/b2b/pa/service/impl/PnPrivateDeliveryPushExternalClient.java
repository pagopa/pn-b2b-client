package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api.PaperNotificationFailedApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api.TimelineAndStatusApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.ResponsePaperNotificationFailedDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class PnPrivateDeliveryPushExternalClient implements IPnPrivateDeliveryPushExternalClient {
    private final TimelineAndStatusApi timelineAndStatusApi;
    private final PaperNotificationFailedApi paperNotificationFailedApi;

    public PnPrivateDeliveryPushExternalClient(
            RestTemplate restTemplate,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath
    ) {
        this.timelineAndStatusApi = new TimelineAndStatusApi( newApiClient( restTemplate, deliveryPushBasePath ) );
        this.paperNotificationFailedApi = new PaperNotificationFailedApi(newApiClient( restTemplate, deliveryPushBasePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        return newApiClient;
    }

    public NotificationHistoryResponse getNotificationHistory(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException {
        return this.timelineAndStatusApi.getNotificationHistory(iun, numberOfRecipients, createdAt);
    }

    public List<ResponsePaperNotificationFailedDto> getPaperNotificationFailed(String recipientInternalId, Boolean getAAR) throws RestClientException {
        return this.paperNotificationFailedApi.paperNotificationFailed(recipientInternalId, getAAR);
    }
}
