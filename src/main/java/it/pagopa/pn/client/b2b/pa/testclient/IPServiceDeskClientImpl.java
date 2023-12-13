package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface IPServiceDeskClientImpl {

    public NotificationsUnreachableResponse notification(NotificationRequest notificationRequest) throws RestClientException;

    public OperationsResponse createOperation(CreateOperationRequest createOperationRequest)throws RestClientException;

    public VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest);

    public SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest);

    //Integration Cruscotto Assistenza....
    public ResponseApiKeys getApiKeys(String paId) throws RestClientException;

    public DocumentsResponse getDocumentsOfIUN(String iun, DocumentsRequest documentsRequest) throws RestClientException;

    public NotificationDetailResponse getNotificationFromIUN(String iun) throws RestClientException ;

    public TimelineResponse getTimelineOfIUN(String iun) throws RestClientException ;

    public SearchNotificationsResponse searchNotificationsAsDelegateFromInternalId(String mandateId, String delegateInternalId, Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate) throws RestClientException ;

    public SearchNotificationsResponse searchNotificationsFromTaxId(Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate, SearchNotificationsRequest searchNotificationsRequest) throws RestClientException ;

    public List<PaSummary> getListOfOnboardedPA() throws RestClientException ;

    public SearchNotificationsResponse searchNotificationsFromSenderId(Integer size, String nextPagesKey, PaNotificationsRequest paNotificationsRequest) throws RestClientException ;

    public ProfileResponse getProfileFromTaxId(ProfileRequest profileRequest) throws RestClientException ;



}
