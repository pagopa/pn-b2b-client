package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import org.springframework.web.client.RestClientException;
import java.time.OffsetDateTime;
import java.util.List;


public interface IPServiceDeskClientImpl {
    NotificationsUnreachableResponse notification(NotificationRequest notificationRequest) throws RestClientException;
    OperationsResponse createOperation(CreateOperationRequest createOperationRequest)throws RestClientException;
    VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest);
    SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest);
    //Integration Cruscotto Assistenza....
    ResponseApiKeys getApiKeys(String paId) throws RestClientException;
    DocumentsResponse getDocumentsOfIUN(String iun, DocumentsRequest documentsRequest) throws RestClientException;
    NotificationDetailResponse getNotificationFromIUN(String iun) throws RestClientException ;
    TimelineResponse getTimelineOfIUN(String iun) throws RestClientException ;
    SearchNotificationsResponse searchNotificationsAsDelegateFromInternalId(String mandateId, String delegateInternalId,String recipientType, Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate) throws RestClientException ;
    SearchNotificationsResponse searchNotificationsFromTaxId(Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate, SearchNotificationsRequest searchNotificationsRequest) throws RestClientException ;
    TimelineResponse getTimelineOfIUNAndTaxId(String iun, SearchNotificationsRequest searchNotificationsRequest) throws RestClientException;
    List<PaSummary> getListOfOnboardedPA() throws RestClientException ;
    SearchNotificationsResponse searchNotificationsFromSenderId(Integer size, String nextPagesKey, PaNotificationsRequest paNotificationsRequest) throws RestClientException ;
    ProfileResponse getProfileFromTaxId(ProfileRequest profileRequest) throws RestClientException ;
    NotificationRecipientDetailResponse getNotificationRecipientDetail(String iun, NotificationRecipientDetailRequest notificationRecipientDetailRequest) throws RestClientException;
}