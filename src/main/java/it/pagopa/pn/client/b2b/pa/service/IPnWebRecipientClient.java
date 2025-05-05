package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v1.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v1.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffLegalFactId;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v1.FullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.*;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public interface IPnWebRecipientClient extends SettableBearerToken {
    FullReceivedNotificationV25 getReceivedNotification(String iun, String mandateId) throws RestClientException;
    BffDocumentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException;

    BffFullNotificationV1 getReceivedNotificationV1(String iun, String mandateId) throws RestClientException;

    it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffFullNotificationV1 getReceivedNotificationV2(String iun, String mandateId) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException;

    NotificationSearchResponse searchReceivedDelegatedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String senderId, String recipientId, String group, NotificationStatusV26 status, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;

    NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatusV26 status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;
    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException;

    List<BffLegalFactId> getLegalFactsV20(String iun, UUID mandateId) throws RestClientException;

    LegalFactDownloadMetadataResponse downloadLegalFactById(String iun, String legalFactId, UUID mandateId) throws RestClientException;

    DocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentId, String mandateId) throws RestClientException;

}