package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiCallHelper;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiResult;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.NotificationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.OperationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.ApiKeysApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.NotificationAndMessageApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.PaApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.ProfileApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class PnServiceDeskClientImpl implements IPServiceDeskClientImpl {
    //Call Center Evoluto....
    private final NotificationApi notification;
    private final OperationApi operation;
    private final OperationApi operationApiWithInvalidApiKey;
    //Integration Cruscotto Assistenza....
    private final ApiKeysApi apiKeysApi;
    private final NotificationAndMessageApi notificationAndMessageApi;
    private final PaApi paApi;
    private final ProfileApi profileApi;
    private final String operatorId;

    public PnServiceDeskClientImpl(RestTemplate restTemplate,
                                   @Value("${pn.delivery.base-url}") String deliveryBasePath,
                                   @Value("${pn.external.api-keys.service-desk}") String apiKeyBase) {
        this.operatorId = "AutomationMv";
        //Call Center Evoluto....
        this.notification = new NotificationApi(newApiClient(restTemplate, deliveryBasePath, apiKeyBase));
        this.operation = new OperationApi(newApiClient(restTemplate, deliveryBasePath, apiKeyBase));
        this.operationApiWithInvalidApiKey = new OperationApi(newApiClient(restTemplate, deliveryBasePath, "invalid-api-key"));
        //Integration Cruscotto Assistenza....
        this.apiKeysApi = new ApiKeysApi(newApiClientIntegration(restTemplate, deliveryBasePath, apiKeyBase));
        this.notificationAndMessageApi = new NotificationAndMessageApi(newApiClientIntegration(restTemplate, deliveryBasePath, apiKeyBase));
        this.paApi = new PaApi(newApiClientIntegration(restTemplate, deliveryBasePath, apiKeyBase));
        this.profileApi = new ProfileApi(newApiClientIntegration(restTemplate, deliveryBasePath, apiKeyBase));
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

    public OperationsResponse createActOperation(CreateActOperationRequest createActOperationRequest) throws RestClientException {
        return operation.createActOperation(operatorId, createActOperationRequest);
    }

    @Override
    public ApiResult<OperationsResponse> createActOperationWithHttpInfo(CreateActOperationRequest req) {
        return ApiCallHelper.call(() -> operation.createActOperationWithHttpInfo(operatorId, req));
    }


    public VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest) {
        return operation.presignedUrlVideoUpload(operatorId, operationid, videoUploadRequest);
    }

    @Override
    public ApiResult<VideoUploadResponse> presignedUrlVideoUploadWithHttpInfo(String operationId, VideoUploadRequest videoUploadRequest) {
        return ApiCallHelper.call(() ->
                operation.presignedUrlVideoUploadWithHttpInfo(operatorId, operationId, videoUploadRequest)
        );
    }

    public SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest) {
        return operation.searchOperationsFromTaxId(operatorId, searchNotificationRequest);
    }

    public String getOperationStatus(String operationId) {
        return operation.getOperationStatus(operationId);
    }

    @Override
    public ApiResult<String> getOperationStatusWithHttpInfo(String operationId) {
        return ApiCallHelper.call(() -> operation.getOperationStatusWithHttpInfo(operationId));
    }

    @Override
    public ApiResult<String> getOperationStatusWithHttpInfoAndInvalidApiKey(String operationId) {
        return ApiCallHelper.call(() -> operationApiWithInvalidApiKey.getOperationStatusWithHttpInfo(operationId));
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

    @Override
    public NotificationRecipientDetailResponse getNotificationRecipientDetail(String xPagopaPnUid, String iun, NotificationRecipientDetailRequest notificationRecipientDetailRequest) throws RestClientException {
        return notificationAndMessageApi.getNotificationRecipientDetail(xPagopaPnUid, iun, notificationRecipientDetailRequest);
    }

    private HttpHeaders safeHeaders(RestClientResponseException ex) {
        HttpHeaders h = ex.getResponseHeaders();
        return (h != null) ? h : new HttpHeaders();
    }

    private MediaType safeContentType(RestClientResponseException ex) {
        HttpHeaders h = ex.getResponseHeaders();
        return (h != null) ? h.getContentType() : null; // può essere null: gestiamo a valle
    }

    private String safeBodyString(RestClientResponseException ex) {
        try {
            String s = ex.getResponseBodyAsString();
            return (s != null) ? s : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String extractProblemAsString(RestClientResponseException ex) {
        MediaType ct = safeContentType(ex);
        String body = safeBodyString(ex);
        if (ct != null && "application".equals(ct.getType()) && "problem+json".equals(ct.getSubtype())) {
            return body;
        }
        return body.isBlank() ? null : body;
    }
}
