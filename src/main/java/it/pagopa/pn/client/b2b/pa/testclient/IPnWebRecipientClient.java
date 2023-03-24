package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface IPnWebRecipientClient extends SettableBearerToken {

    FullReceivedNotification getReceivedNotification(String iun, String mandateId) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException;

    NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;

    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException;
}
