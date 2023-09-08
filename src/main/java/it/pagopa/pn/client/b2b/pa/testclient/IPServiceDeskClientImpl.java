package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

public interface IPServiceDeskClientImpl {

    public NotificationsUnreachableResponse notification(NotificationRequest notificationRequest) throws RestClientException;

    public OperationsResponse createOperation(CreateOperationRequest createOperationRequest)throws RestClientException;

    public VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest);

    public SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest);



    }
