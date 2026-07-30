package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bExternalInformalClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PnPaB2bExternalInformalClientImpl implements IPnPaB2bExternalInformalClient {

    private final RestTemplate restTemplate;
    private final String basePath;

    public PnPaB2bExternalInformalClientImpl(RestTemplate restTemplate, @Value("${pn.external.base-url}") String basePath) {

        this.restTemplate = restTemplate;
        this.basePath = basePath+"/informal/";
    }

    @Override
    public List<InformalPreLoadResponse> informalPresignedUploadRequest(String apiKey, List<InformalPreLoadRequest> requests) {
        NewInformalNotificationApi api = new NewInformalNotificationApi(newExternalInformalApiClient(restTemplate, basePath, apiKey));
        return api.informalPresignedUploadRequest(requests);
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient newExternalInformalApiClient(RestTemplate restTemplate, String basePath, String apiKey) {

        var client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient(restTemplate);
        client.setBasePath(basePath);
        client.addDefaultHeader("x-api-key", apiKey);
        return client;
    }
}