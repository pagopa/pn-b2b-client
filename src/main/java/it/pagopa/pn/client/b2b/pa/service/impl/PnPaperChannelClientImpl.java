package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.api.InformalMessagesApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperChannelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PnPaperChannelClientImpl implements IPnPaperChannelClient {

   private InformalMessagesApi informalMessagesApi;

    public PnPaperChannelClientImpl(RestTemplate restTemplate, @Value("${pn.delivery.base-url}") String basePath) {
        informalMessagesApi = new InformalMessagesApi(newApiClient(restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public ResponseEntity<InformalPrepareResponse> sendInformalPrepareRequest(InformalPrepareRequest informalPrepareRequest, String xClientId) throws RestClientException {
        return informalMessagesApi.sendInformalPrepareRequestWithHttpInfo(informalPrepareRequest, xClientId);
    }
}
