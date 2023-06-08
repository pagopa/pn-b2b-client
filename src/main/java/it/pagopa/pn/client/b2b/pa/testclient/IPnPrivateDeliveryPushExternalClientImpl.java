package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;

public interface IPnPrivateDeliveryPushExternalClientImpl {

    NotificationHistoryResponse getNotificationHistory(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException;
}
