package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnExternalApiKeyConfig;
import it.pagopa.pn.client.b2b.pa.service.IPServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.NotificationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.OperationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.ApiKeysApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.NotificationAndMessageApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.PaApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.ProfileApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;


@Component
public class PnServiceDeskClientImpl implements IPServiceDeskClientImpl {
    //Call Center Evoluto....
    private final NotificationApi notification;
    private final OperationApi operation;
    //Integration Cruscotto Assistenza....
    private final ApiKeysApi apiKeysApi;
    private final NotificationAndMessageApi notificationAndMessageApi;
    private final PaApi paApi;
    private final ProfileApi profileApi;
    private final String operatorId;
    private final String baseUrl;
    private final String apiKey;

    @Autowired
    public PnServiceDeskClientImpl(RestTemplate restTemplate,
                                   PnBaseUrlConfig pnBaseUrlConfig,
                                   PnExternalApiKeyConfig pnExternalApiKeyConfig) {
        this.baseUrl = pnBaseUrlConfig.getDeliveryBaseUrl();
        this.apiKey = pnExternalApiKeyConfig.getServiceDeskApiKey();
        this.operatorId = "AutomationMv";
        //Call Center Evoluto....
        this.notification = new NotificationApi(newApiClient(restTemplate, baseUrl, apiKey));
        this.operation = new OperationApi(newApiClient(restTemplate, baseUrl, apiKey));
        //Integration Cruscotto Assistenza....
        this.apiKeysApi = new ApiKeysApi(newApiClientIntegration(restTemplate, baseUrl, apiKey));
        this.notificationAndMessageApi = new NotificationAndMessageApi(newApiClientIntegration(restTemplate, baseUrl, apiKey));
        this.paApi = new PaApi(newApiClientIntegration(restTemplate, baseUrl, apiKey));
        this.profileApi = new ProfileApi(newApiClientIntegration(restTemplate, baseUrl, apiKey));
    }

    //Call Center Evoluto....
    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apiKey);
        return newApiClient;
    }

    //Integration Cruscotto Assistenza....
    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.ApiClient newApiClientIntegration(RestTemplate restTemplate, String basePath, String apiKey) {
        it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.ApiClient newApiClient = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apiKey);
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

    //Integration Cruscotto Assistenza....
    public ResponseApiKeys getApiKeys(String paId) throws RestClientException {
        return apiKeysApi.getApiKeys(paId);
    }

    public DocumentsResponse getDocumentsOfIUN(String iun, DocumentsRequest documentsRequest) throws RestClientException {
        return notificationAndMessageApi.getDocumentsOfIUN(operatorId, iun, documentsRequest);
    }

    public NotificationDetailResponse getNotificationFromIUN(String iun) throws RestClientException {
        return notificationAndMessageApi.getNotificationFromIUN(operatorId, iun);
    }

    public TimelineResponse getTimelineOfIUN(String iun) throws RestClientException {
        return notificationAndMessageApi.getTimelineOfIUN(operatorId, iun);
    }

    public SearchNotificationsResponse searchNotificationsAsDelegateFromInternalId(String mandateId, String delegateInternalId, String recipientType, Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate) throws RestClientException {
        return notificationAndMessageApi.searchNotificationsAsDelegateFromInternalId(operatorId, mandateId, delegateInternalId, recipientType, startDate, endDate, size, nextPagesKey);
    }

    public SearchNotificationsResponse searchNotificationsFromTaxId(Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate, SearchNotificationsRequest searchNotificationsRequest) throws RestClientException {
        return notificationAndMessageApi.searchNotificationsFromTaxId(operatorId, startDate, endDate, size, nextPagesKey, searchNotificationsRequest);
    }

    public TimelineResponse getTimelineOfIUNAndTaxId(String iun, SearchNotificationsRequest searchNotificationsRequest) throws RestClientException {
        return notificationAndMessageApi.getTimelineOfIUNAndTaxId(operatorId, iun, searchNotificationsRequest);
    }

    public List<PaSummary> getListOfOnboardedPA() throws RestClientException {
        return paApi.getListOfOnboardedPA(operatorId);
    }

    public SearchNotificationsResponse searchNotificationsFromSenderId(Integer size, String nextPagesKey, PaNotificationsRequest paNotificationsRequest) throws RestClientException {
        return paApi.searchNotificationsFromSenderId(operatorId, size, nextPagesKey, paNotificationsRequest);
    }

    public ProfileResponse getProfileFromTaxId(ProfileRequest profileRequest) throws RestClientException {
        return profileApi.getProfileFromTaxId(operatorId, profileRequest);
    }
}