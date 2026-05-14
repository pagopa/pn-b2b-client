package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.api.NotificationCostRecipientApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NewNotificationCostRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NotificationCostPaymentResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.notificationcostservice.model.NotificationCostRecipientResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnNotificationCostClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnNotificationCostClientImpl implements IPnNotificationCostClient {

    private final NotificationCostRecipientApi notificationCostRecipientApi;

    public PnNotificationCostClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.delivery.base-url}") String basePath) {
        this.notificationCostRecipientApi = new NotificationCostRecipientApi(newApiClient(restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.addDefaultHeader("Accept", "application/io+json");
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public NotificationCostRecipientResponse getNotificationCost(String iun, Integer recIndex) throws RestClientException {
        return notificationCostRecipientApi.getNotificationCost(iun, recIndex);
    }

    @Override
    public NotificationCostPaymentResponse getNotificationCostByPayment(String creditorTaxId, String noticeCode) throws RestClientException {
        return notificationCostRecipientApi.getNotificationCostByPayment(creditorTaxId, noticeCode);
    }

    @Override
    public String initializeNotificationCost(String iun, NewNotificationCostRequest newNotificationCostRequest) throws RestClientException {
        return notificationCostRecipientApi.initializeNotificationCost(iun, newNotificationCostRequest);
    }
}
