package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatus;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;

public interface IPnWebRecipientClient {

    FullReceivedNotification getReceivedNotification(String iun, String mandateId) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, String mandateId) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, String mandateId) throws RestClientException;

    NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;

}
