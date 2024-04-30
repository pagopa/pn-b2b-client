package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.ResponsePaperNotificationFailedDto;
import org.springframework.web.client.RestClientException;
import java.time.OffsetDateTime;
import java.util.List;


public interface IPnPrivateDeliveryPushExternalClient {
    NotificationHistoryResponse getNotificationHistory(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException;
    List<ResponsePaperNotificationFailedDto> getPaperNotificationFailed(String recipientInternalId, Boolean getAAR) throws RestClientException;
}