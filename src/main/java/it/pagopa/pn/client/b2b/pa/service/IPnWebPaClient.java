package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;


public interface IPnWebPaClient extends SettableBearerToken {
    BffLegalNotificationsResponse searchSentNotification(
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            String recipientId,
            NotificationStatusV26 status,
            String iunMatch,
            Integer size,
            String nextPagesKey
    ) throws RestClientException;
}