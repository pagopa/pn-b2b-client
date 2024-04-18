package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPServiceDeskClientImplNoApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.NotificationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.OperationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class PnServiceDeskClientImplNoApiKey implements IPServiceDeskClientImplNoApiKey {
    private final NotificationApi notification;
    private final OperationApi operation;
    private final String operatorId;

    public PnServiceDeskClientImplNoApiKey(RestTemplate restTemplate, @Value("${pn.externalChannels.base-url}") String deliveryBasePath) {
        this.operatorId = "AutomationMv";
        this.notification = new NotificationApi(newApiClient(restTemplate, deliveryBasePath));
        this.operation = new OperationApi(newApiClient(restTemplate, deliveryBasePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public NotificationsUnreachableResponse notification(NotificationRequest notificationRequest) throws RestClientException {
        return notification.numberOfUnreachableNotifications(operatorId, notificationRequest);
    }

    public OperationsResponse createOperation(CreateOperationRequest createOperationRequest) throws RestClientException {
        return operation.createOperation(operatorId, createOperationRequest);
    }

    public VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest) {
        return operation.presignedUrlVideoUpload(operatorId, operationid, videoUploadRequest);
    }

    public SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest) {
        return operation.searchOperationsFromTaxId(operatorId, searchNotificationRequest);
    }
}