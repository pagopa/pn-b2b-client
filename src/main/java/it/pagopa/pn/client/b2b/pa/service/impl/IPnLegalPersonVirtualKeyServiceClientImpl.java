package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.virtualKey.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.virtualKey.pg.VirtualKeysApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.virtualKey.pg.RequestNewVirtualKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.virtualKey.pg.RequestVirtualKeyStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.virtualKey.pg.ResponseNewVirtualKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.virtualKey.pg.VirtualKeysResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonVirtualKeyServiceClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class IPnLegalPersonVirtualKeyServiceClientImpl implements IPnLegalPersonVirtualKeyServiceClient {

    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private String basePath;

    private RestTemplate restTemplate;

    private VirtualKeysApi virtualKeysApi;

    public IPnLegalPersonVirtualKeyServiceClientImpl(RestTemplate restTemplate,
                                                     @Value("") String basePath,
                                                     @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                                     @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken) {
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.virtualKeysApi = new VirtualKeysApi(newApiClient(cucumberSpaBearerToken));
    }

    private ApiClient newApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(this.restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public void changeStatusVirtualKeys(String id, RequestVirtualKeyStatus requestVirtualKeyStatus) throws RestClientException {
        virtualKeysApi.changeStatusVirtualKeys(null, null, null, null, id, requestVirtualKeyStatus, null);
    }

    @Override
    public ResponseNewVirtualKey createVirtualKey(RequestNewVirtualKey requestNewVirtualKey) throws RestClientException {
        return virtualKeysApi.createVirtualKey(null, null, null, null, requestNewVirtualKey, null);
    }

    @Override
    public void deleteVirtualKey(String id) throws RestClientException {
        virtualKeysApi.deleteVirtualKey(null, null, null, null, id, null);
    }

    @Override
    public VirtualKeysResponse getVirtualKeys(Integer limit, String lastKey, String lastUpdate, Boolean showVirtualKey) throws RestClientException {
        return virtualKeysApi.getVirtualKeys(null, null, null, null, null, limit, lastKey, lastUpdate, showVirtualKey);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.virtualKeysApi.setApiClient(newApiClient(gherkinSrlBearerToken));
            }
            default ->  {
                this.virtualKeysApi.setApiClient(newApiClient(cucumberSpaBearerToken));
            }
        }
    }
}
