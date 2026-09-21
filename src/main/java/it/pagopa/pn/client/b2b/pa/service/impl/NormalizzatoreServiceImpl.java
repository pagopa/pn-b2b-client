package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.api.NormalizzatoreApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncResponse;
import it.pagopa.pn.client.b2b.pa.service.NormalizzatoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NormalizzatoreServiceImpl implements NormalizzatoreService {
    private final NormalizzatoreApi normalizzatoreApi;
    private final String pnAddressManagerCxId;
    private final String xApiKey;

    public NormalizzatoreServiceImpl(RestTemplate restTemplate,
    @Value("${pn.address.manager-url}") String basePath,
    @Value("${pn.address.manager.cxId}")String cxId,
    @Value("${pn.address.manager.api-key}")String apiKey) {
        this.pnAddressManagerCxId = cxId;
        this.xApiKey = apiKey;
        this.normalizzatoreApi= new NormalizzatoreApi( newApiClient(restTemplate, basePath, cxId, apiKey));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String cxId, String apiKey) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public NormalizzazioneSyncResponse normalizzazioneSync(
            String pnAddressManagerCxId,
            String xApiKey,
            NormalizzazioneSyncRequest request) {
        return normalizzatoreApi.normalizzazioneSync(pnAddressManagerCxId, xApiKey, request);
    }

    @Override
    public ResponseEntity<NormalizzazioneSyncResponse> normalizzazioneSyncWithHttpInfo(
            NormalizzazioneSyncRequest request) {

        return normalizzatoreApi.normalizzazioneSyncWithHttpInfo(
                pnAddressManagerCxId,
                xApiKey,
                request
        );
    }
}