package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import org.springframework.web.client.RestClientException;


public interface IPServiceDeskClientImplWrongApiKey {
    NotificationsUnreachableResponse notification(NotificationRequest notificationRequest) throws RestClientException;
    OperationsResponse createOperation(CreateOperationRequest createOperationRequest)throws RestClientException;
    VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest);
    SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest);
}