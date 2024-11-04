package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotificationV24;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatus;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v1.FullReceivedNotification;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.UUID;


public interface IPnWebRecipientClient extends SettableBearerToken {
    FullReceivedNotificationV24 getReceivedNotification(String iun, String mandateId) throws RestClientException;

    it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v1.NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException;

    FullReceivedNotification getReceivedNotificationV1(String iun, String mandateId) throws RestClientException;

    it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v2.FullReceivedNotification getReceivedNotificationV2(String iun, String mandateId) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException;

    NotificationSearchResponse searchReceivedDelegatedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String senderId, String recipientId, String group, NotificationStatus status, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;

    NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;

    //OLD VERSION PRE v25
//    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException;

    LegalFactDownloadMetadataResponse getLegalFact(String iun, String legalFactType, String legalFactId) throws RestClientException;

    //OLD VERSION PRE v25
//    DocumentDownloadMetadataResponse getDocumentsWeb(String iun, DocumentCategory documentType, String documentId, String mandateId) throws RestClientException;

    DocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentType, String documentId, String mandateId) throws RestClientException;
}