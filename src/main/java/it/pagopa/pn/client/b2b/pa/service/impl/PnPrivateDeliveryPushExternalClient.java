package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v24.PaperNotificationFailedApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.ResponsePaperNotificationFailedDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;


@Component
public class PnPrivateDeliveryPushExternalClient implements IPnPrivateDeliveryPushExternalClient {

    private final it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v1.TimelineAndStatusApi timelineAndStatusApiV1;
    private final it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v2.TimelineAndStatusApi timelineAndStatusApiV2;
    private final it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v21.TimelineAndStatusApi timelineAndStatusApiV21;
    private final it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v23.TimelineAndStatusApi timelineAndStatusApiV23;
    private final it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v24.TimelineAndStatusApi timelineAndStatusApiV24;
    private final it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v25.TimelineAndStatusApi timelineAndStatusApiV25;
    private final PaperNotificationFailedApi paperNotificationFailedApi;


    public PnPrivateDeliveryPushExternalClient(RestTemplate restTemplate, @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath) {
        this.timelineAndStatusApiV1 = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v1.TimelineAndStatusApi(newApiClient(restTemplate, deliveryPushBasePath));
        this.timelineAndStatusApiV2 = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v2.TimelineAndStatusApi(newApiClient(restTemplate, deliveryPushBasePath));
        this.timelineAndStatusApiV21 = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v21.TimelineAndStatusApi(newApiClient(restTemplate, deliveryPushBasePath));
        this.timelineAndStatusApiV23 = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v23.TimelineAndStatusApi(newApiClient(restTemplate, deliveryPushBasePath));
        this.timelineAndStatusApiV24 = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v24.TimelineAndStatusApi(newApiClient(restTemplate, deliveryPushBasePath));
        this.timelineAndStatusApiV25 = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v25.TimelineAndStatusApi(newApiClient(restTemplate, deliveryPushBasePath));

        this.paperNotificationFailedApi = new PaperNotificationFailedApi(newApiClient(restTemplate, deliveryPushBasePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v1.NotificationHistoryResponse getNotificationHistoryV1(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException {
        return this.timelineAndStatusApiV1.getNotificationHistory(iun, numberOfRecipients, createdAt);
    }

    public it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v2.NotificationHistoryResponse getNotificationHistoryV2(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException {
        return this.timelineAndStatusApiV2.getNotificationHistory(iun, numberOfRecipients, createdAt);
    }

    public it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v21.NotificationHistoryResponse getNotificationHistoryV21(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException {
        return this.timelineAndStatusApiV21.getNotificationHistory(iun, numberOfRecipients, createdAt);
    }

    public it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v23.NotificationHistoryResponse getNotificationHistoryV23(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException {
        return this.timelineAndStatusApiV23.getNotificationHistory(iun, numberOfRecipients, createdAt);
    }

    public it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.NotificationHistoryResponse getNotificationHistoryV24(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException {
        return this.timelineAndStatusApiV24.getNotificationHistory(iun, numberOfRecipients, createdAt);
    }

    public it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v25.NotificationHistoryResponse getNotificationHistoryV25(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException {
        return this.timelineAndStatusApiV25.getNotificationHistory(iun, numberOfRecipients, createdAt);
    }

    public List<ResponsePaperNotificationFailedDto> getPaperNotificationFailed(String recipientInternalId, Boolean getAAR) throws RestClientException {
        return this.paperNotificationFailedApi.paperNotificationFailed(recipientInternalId, getAAR);
    }
}