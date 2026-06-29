package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.selfcare.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.selfcare.privateapi.AooUoIdsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.FilteredPaIdsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class AooUoIdsClientImpl {
    private final AooUoIdsApi aooUoIdsApi;

    public AooUoIdsClientImpl(RestTemplate restTemplate,
                              @Value("${pn.delivery.base-url}") String basePath) {
        this.aooUoIdsApi = new AooUoIdsApi(newApiClient(restTemplate, basePath));
    }

    private ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public FilteredPaIdsResponse getFilteredAooUoIdV2Private(List<String> id) {
        return aooUoIdsApi.getFilteredAooUoIdV2Private(id);
    }

}
