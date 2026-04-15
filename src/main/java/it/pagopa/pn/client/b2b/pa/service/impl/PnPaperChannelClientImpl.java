package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.api.InformalMessagesApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperChannelClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class PnPaperChannelClientImpl implements IPnPaperChannelClientImpl {

   private InformalMessagesApi informalMessagesApi;

    PnPaperChannelClientImpl(RestTemplate restTemplate, @Value("${pn.delivery.base-url}") String basePath) {
        informalMessagesApi = new InformalMessagesApi(newApiClient(restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public InformalPrepareResponse sendInformalPrepareRequest(InformalPrepareRequest informalPrepareRequest, String xClientId) throws RestClientException {
        return informalMessagesApi.sendInformalPrepareRequest(informalPrepareRequest, xClientId);
    }
}
