package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.http.ResponseEntity;

public interface IPnBFFRecipientNotificationClient {

    ResponseEntity<BffFullNotificationV1> getReceivedNotificationV1WithHttpInfoForRecipient(String iun);

    ResponseEntity<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1> getSentNotificationV1WithHttpInfoForSender(String iun);

    void setRecipientBearerToken(SettableBearerToken.BearerTokenType bearerToken);

    void setSenderBearerToken(SettableBearerToken.BearerTokenType bearerToken);
}
