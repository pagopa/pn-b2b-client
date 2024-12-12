package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatusV26;
import org.springframework.web.client.RestClientException;
import java.time.OffsetDateTime;


public interface IPnWebPaClient extends SettableBearerToken {
    NotificationSearchResponse searchSentNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, NotificationStatusV26 status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException;
}