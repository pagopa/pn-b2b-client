package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.recipient.NotificationSentApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.NotificationReceivedApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.pa.service.IPnBFFRecipientNotificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnBFFRecipientNotificationClientImpl implements IPnBFFRecipientNotificationClient {
    private final NotificationReceivedApi notificationReceivedApi;
    private final NotificationSentApi notificationSentApi;

    public PnBFFRecipientNotificationClientImpl(RestTemplate restTemplate,
                                                @Value("${pn.webapi.external.base-url}")String basePath,
                                                @Value("${pn.bearer-token.user1}")String berearToken) {
        this.notificationReceivedApi = new NotificationReceivedApi(newApiClientForRecipient(restTemplate, basePath, berearToken));
        this.notificationSentApi = new NotificationSentApi(newApiClientForSender(restTemplate, basePath, berearToken));
    }

    private static ApiClient newApiClientForRecipient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer "+bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient newApiClientForSender(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient apiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.addDefaultHeader("Authorization","Bearer "+bearerToken);
        return apiClient;
    }

    @Override
    public ResponseEntity<BffFullNotificationV1> getReceivedNotificationV1WithHttpInfoForRecipient(String iun) {
        return notificationReceivedApi.getReceivedNotificationV1WithHttpInfo(iun, null);
    }

    @Override
    public ResponseEntity<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1> getSentNotificationV1WithHttpInfoForSender(String iun) {
        return notificationSentApi.getSentNotificationV1WithHttpInfo(iun);
    }
}
