package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.client.b2b.generated.openapi.clients.address_manager.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.address_manager.api.NormalizeAddressServiceSyncApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.address_manager.model.NormalizeSyncRequest;

import it.pagopa.pn.client.b2b.generated.openapi.clients.address_manager.model.Problem;
import it.pagopa.pn.client.b2b.pa.service.AddressManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class AddressManagerServiceImpl implements AddressManagerService {
    private final NormalizeAddressServiceSyncApi normalizeAddressServiceSyncApi;
    private final String pnAddressManagerCxId;
    private final String xApiKey;

    public AddressManagerServiceImpl(RestTemplate restTemplate,
                                     @Value("${pn.address.manager-url}") String basePath,
                                     @Value("${pn.address.manager.cxId}") String cxId,
                                     @Value("${pn.address.manager.api-key}") String apiKey) {
        this.pnAddressManagerCxId = cxId;
        this.xApiKey = apiKey;
        this.normalizeAddressServiceSyncApi = new NormalizeAddressServiceSyncApi(newApiClient(restTemplate, basePath));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public ResponseEntity<?> normalizzazioneSyncWithHttpInfo(NormalizeSyncRequest request) {
        try {
            return normalizeAddressServiceSyncApi.normalizeSyncWithHttpInfo(this.pnAddressManagerCxId,
                    this.xApiKey,
                    request
            );
        } catch (RestClientResponseException ex) {
            try {
                Problem problem = new ObjectMapper().readValue(ex.getResponseBodyAsString(), Problem.class);
                return ResponseEntity.status(ex.getRawStatusCode())
                        .body(problem);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Impossibile deserializzare il Problem", e);
            }
        }
    }
}