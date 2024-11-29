package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnInteropProbingClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.api.InteropProbingApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class IPnInteropProbingClientImpl implements IPnInteropProbingClient {

    private final InteropProbingApi interopProbingApi;


    public IPnInteropProbingClientImpl(RestTemplate restTemplate, PnBaseUrlConfig pnBaseUrlConfig) {
        this.interopProbingApi = new InteropProbingApi(
                newApiClient(restTemplate, pnBaseUrlConfig.getExternalBaseUrl()));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath/*+"/downtime/v1"*/);
        return newApiClient;
    }

    public ResponseEntity<Void> getEserviceStatus() throws RestClientException {
        return this.interopProbingApi.getEserviceStatusWithHttpInfo();
    }

}