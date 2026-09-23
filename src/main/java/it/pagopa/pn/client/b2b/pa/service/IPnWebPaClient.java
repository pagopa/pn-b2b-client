package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.info.BffAdditionalLanguages;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNewNotificationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNewNotificationResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffRequestStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;


public interface IPnWebPaClient extends SettableBearerToken {
    BffLegalNotificationsResponse searchSentNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, NotificationStatusV26 status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;
    BffNewNotificationResponse newSentNotificationV1(BffNewNotificationRequest notificationRequest) throws RestClientException;
    BffFullNotificationV1 getSentNotificationV1(String iun) throws RestClientException;
    BffRequestStatus notificationCancellationV1(String iun) throws RestClientException;
    BffAdditionalLanguages changeAdditionalLang(BffAdditionalLanguages bffAdditionalLanguages) throws RestClientException;
    BffDocumentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, BffDocumentType documentType, Integer documentIdx, String documentId) throws RestClientException;
    BffDocumentDownloadMetadataResponse getSentNotificationPaymentV1(String iun, Integer recipientIdx, String attachmentName, Integer attachmentIdx) throws RestClientException;
}