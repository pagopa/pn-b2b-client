package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.apikey.manager.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.apikey.manager.pg.VirtualKeysApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffNewVirtualKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffNewVirtualKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffVirtualKeyStatusRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffVirtualKeysResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonVirtualKeyServiceClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IPnLegalPersonVirtualKeyServiceClientImpl implements IPnLegalPersonVirtualKeyServiceClient {

    private final String aldameriniPGBearerToken;
    private final String mariaMontessoriPGBearerToken;
    private final String nildeIottiPGBearerToken;
    private final String cucumberSpaBearerToken;

    private final String basePath;

    private RestTemplate restTemplate;

    private final VirtualKeysApi virtualKeysApi;

    public IPnLegalPersonVirtualKeyServiceClientImpl(RestTemplate restTemplate,
                     @Value("${pn.webapi.external.base-url}") String basePath,
                     @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                     @Value("${pn.bearer-token.pg3}") String aldameriniPGBearerToken,
                     @Value("${pn.bearer-token.pg4}") String mariaMontessoriPGBearerToken,
                     @Value("${pn.bearer-token.pg5}") String nildeIottiPGBearerToken) {
        this.aldameriniPGBearerToken = aldameriniPGBearerToken;
        this.mariaMontessoriPGBearerToken = mariaMontessoriPGBearerToken;
        this.nildeIottiPGBearerToken = nildeIottiPGBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.virtualKeysApi = new VirtualKeysApi(newApiClient(aldameriniPGBearerToken));
    }

    private ApiClient newApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(this.restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public void changeStatusVirtualKeys(String kid, BffVirtualKeyStatusRequest bffNewVirtualKeyRequest) throws RestClientException {
        virtualKeysApi.changeStatusVirtualKeysV1(kid, bffNewVirtualKeyRequest);
    }

    @Override
    public BffNewVirtualKeyResponse createVirtualKey(BffNewVirtualKeyRequest requestNewVirtualKey) throws RestClientException {
        return virtualKeysApi.newVirtualKeyV1(requestNewVirtualKey);
    }

    @Override
    public void deleteVirtualKey(String kid) throws RestClientException {
        virtualKeysApi.deleteVirtualKeyV1(kid);
    }

    @Override
    public BffVirtualKeysResponse getVirtualKeys(Integer limit, String lastKey, String lastUpdate, Boolean showVirtualKey) throws RestClientException {
        return virtualKeysApi.getVirtualKeysV1(limit, lastKey, lastUpdate, showVirtualKey);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_3 -> {
                this.virtualKeysApi.setApiClient(newApiClient(aldameriniPGBearerToken));
            }
            case PG_4 -> {
                this.virtualKeysApi.setApiClient(newApiClient(mariaMontessoriPGBearerToken));
            }
            case PG_5 -> {
                this.virtualKeysApi.setApiClient(newApiClient(nildeIottiPGBearerToken));
            }
            case PG_2 -> {
                this.virtualKeysApi.setApiClient(newApiClient(cucumberSpaBearerToken));
            }
            default ->  {
                throw new IllegalArgumentException("User not found");
            }
        }
    }
}
