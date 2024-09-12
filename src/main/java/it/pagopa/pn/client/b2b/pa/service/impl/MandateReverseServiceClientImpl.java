package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.api.MandateReverseServiceApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class MandateReverseServiceClientImpl implements IMandateReverseServiceClient {
    private final MandateReverseServiceApi mandateReverseServiceApi;

    public MandateReverseServiceClientImpl(RestTemplate restTemplate, @Value("${pn.delivery.base-url}") String basePath) {
        this.mandateReverseServiceApi = new MandateReverseServiceApi(newApiClient(restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public ResponseEntity<String> createReverseMandateWithHttpInfo(MandateDtoRequest mandateDtoRequest) throws RestClientException {
        return mandateReverseServiceApi.createReverseMandateWithHttpInfo(mandateDtoRequest);
    }
}
