package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.apikey.manager.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.apikey.manager.pg.PublicKeysApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeysResponse;
import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenConfigs;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IPnLegalPersonAuthClientImpl implements IPnLegalPersonAuthClient {

    private final String aldameriniPGBearerToken;
    private final String mariaMontessoriPGBearerToken;
    private final String nildeIottiPGBearerToken;
    private final String cucumberSpaBearerToken;

    private final String basePath;
    private RestTemplate restTemplate;
    private final PublicKeysApi publicKeysApi;

    @Autowired
    public IPnLegalPersonAuthClientImpl(RestTemplate restTemplate,
                                        PnBearerTokenConfigs pnBearerTokenConfigs,
                                        PnBaseUrlConfig pnBaseUrlConfig) {
        this.cucumberSpaBearerToken = pnBearerTokenConfigs.getPg2();
        this.aldameriniPGBearerToken = pnBearerTokenConfigs.getPg3();
        this.mariaMontessoriPGBearerToken = pnBearerTokenConfigs.getPg4();
        this.nildeIottiPGBearerToken = pnBearerTokenConfigs.getPg5();
        this.basePath = pnBaseUrlConfig.getWebApiExternalBaseUrl();
        this.restTemplate = restTemplate;
        this.publicKeysApi = new PublicKeysApi(newApiClient(basePath, aldameriniPGBearerToken));
    }

    private ApiClient newApiClient(String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(this.restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public BffPublicKeysResponse getPublicKeysV1(Integer limit, String lastKey, String createdAt, Boolean showPublicKey) throws RestClientException {
        return publicKeysApi.getPublicKeysV1(limit, lastKey, createdAt, showPublicKey);
    }

    @Override
    public BffPublicKeyResponse newPublicKeyV1(BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException {
        return publicKeysApi.newPublicKeyV1(bffPublicKeyRequest);
    }

    @Override
    public BffPublicKeyResponse rotatePublicKeyV1(String kid, BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException {
        return publicKeysApi.rotatePublicKeyV1(kid, bffPublicKeyRequest);
    }

    @Override
    public void deletePublicKeyV1(String kid) throws RestClientException {
        publicKeysApi.deletePublicKeyV1(kid);
    }

    @Override
    public void changeStatusPublicKeyV1(String kid, String status) throws RestClientException {
        publicKeysApi.changeStatusPublicKeyV1(kid, status);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_2 -> this.publicKeysApi.setApiClient(newApiClient(this.basePath, cucumberSpaBearerToken));
            case PG_3 -> this.publicKeysApi.setApiClient(newApiClient(this.basePath, aldameriniPGBearerToken));
            case PG_4 -> this.publicKeysApi.setApiClient(newApiClient(this.basePath, mariaMontessoriPGBearerToken));
            case PG_5 -> this.publicKeysApi.setApiClient(newApiClient(this.basePath, nildeIottiPGBearerToken));
            default -> {
                this.publicKeysApi.setApiClient(newApiClient(this.basePath, aldameriniPGBearerToken));
            }
        }
    }
}
