package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IPnBFFRecipientNotificationClient {

    ResponseEntity<BffFullNotificationV1> getReceivedNotificationV1WithHttpInfoForRecipient(String iun);

    ResponseEntity<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1> getSentNotificationV1WithHttpInfoForSender(String iun);

    BffDocumentDownloadMetadataResponse getReceivedNotificationDocumentV1(String iun, BffDocumentType documentType, UUID mandateId, Integer documentIdx, String documentId);

    ResponseEntity<BffDocumentDownloadMetadataResponse> getReceivedNotificationPaymentV1WithHttpInfo(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx);

    void setRecipientBearerToken(SettableBearerToken.BearerTokenType bearerToken);

    void setSenderBearerToken(SettableBearerToken.BearerTokenType bearerToken);
}
