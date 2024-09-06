package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.api.PaperCalculatorApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PaperCalculatorClientImpl {
    private final PaperCalculatorApi paperCalculatorApi;

    public PaperCalculatorClientImpl(RestTemplate restTemplate, @Value("${pn.paper-channel.base-url}") String basePath) {
        this.paperCalculatorApi = new PaperCalculatorApi(newApiClient(restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        return newApiClient;
    }

    public ResponseEntity<ShipmentCalculateResponse> calculateCostWithHttpInfo(String tenderId, ShipmentCalculateRequest shipmentCalculateRequest) {
        return paperCalculatorApi.calculateCostWithHttpInfo(tenderId, shipmentCalculateRequest);
    }

}

