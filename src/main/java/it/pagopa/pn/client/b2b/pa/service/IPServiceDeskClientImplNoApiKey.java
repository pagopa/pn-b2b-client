package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import org.springframework.web.client.RestClientException;

public interface IPServiceDeskClientImplNoApiKey {

    public NotificationsUnreachableResponse notification(NotificationRequest notificationRequest) throws RestClientException;

    public OperationsResponse createOperation(CreateOperationRequest createOperationRequest)throws RestClientException;

    public VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest);

    public SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest);



    }
