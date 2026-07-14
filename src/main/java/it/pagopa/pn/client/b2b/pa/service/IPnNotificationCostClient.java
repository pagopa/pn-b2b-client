package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NewNotificationCostRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NotificationCostPaymentResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NotificationCostRecipientResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.PaperCostToInvalidate;
import org.springframework.web.client.RestClientException;

public interface IPnNotificationCostClient {

    NotificationCostRecipientResponse getNotificationCost(String iun, Integer recIndex) throws RestClientException;

    NotificationCostPaymentResponse getNotificationCostByPayment(String creditorTaxId, String noticeCode) throws RestClientException;

    String initializeNotificationCost(String iun, NewNotificationCostRequest newNotificationCostRequest) throws RestClientException;

    void invalidatePaperCost(String iun, PaperCostToInvalidate paperCostToInvalidate) throws RestClientException;
}
