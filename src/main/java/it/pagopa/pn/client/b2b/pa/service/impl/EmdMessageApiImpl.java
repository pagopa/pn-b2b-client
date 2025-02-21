package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.ApiClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.api.MessageApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageRequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class EmdMessageApiImpl {
    private final MessageApi messageApi;
    private ApiClient apiClient;

    public EmdMessageApiImpl(RestTemplate restTemplate,
                             @Value("${pn.delivery.base-url}") String basePath) {
        this.messageApi = new MessageApi(createApiClient(restTemplate, basePath, "dummyBearer"));
    }

    private ApiClient createApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    public void sendMessage(SendMessageRequestBody sendMessageRequestBody) {
        messageApi.sendMessage(sendMessageRequestBody);
    }
}
