package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.apikey.manager.pg.PublicKeysApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.*;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.apikey.manager.ApiClient;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IPnLegalPersonAuthClientImpl implements IPnLegalPersonAuthClient {

    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private String basePath;

    private RestTemplate restTemplate;
    private PublicKeysApi publicKeysApi;

    public IPnLegalPersonAuthClientImpl(RestTemplate restTemplate,
                                        @Value("") String basePath,
                                        @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                        @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken) {
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.publicKeysApi = new PublicKeysApi(newApiClient(basePath, null));
    }

    private ApiClient newApiClient(String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(this.restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public BffPublicKeysResponse getPublicKeysV1(Integer limit, String lastKey, String createdAt, Boolean showPublicKey) throws RestClientException {
        return publicKeysApi.getPublicKeysV1(null, null, null, null, null, limit, lastKey, createdAt, showPublicKey);
    }

    @Override
    public BffPublicKeyResponse newPublicKeyV1(BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException {
        return publicKeysApi.newPublicKeyV1(null, null, null, bffPublicKeyRequest, null, null);
    }

    @Override
    public BffPublicKeyResponse rotatePublicKeyV1(String kid, BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException {
        return publicKeysApi.rotatePublicKeyV1(null, null, null, kid, bffPublicKeyRequest, null, null);
    }

    @Override
    public void deletePublicKeyV1(String kid) throws RestClientException {
        publicKeysApi.deletePublicKeyV1(null, null, null, kid, null, null);
    }

    @Override
    public void changeStatusPublicKeyV1(String kid, String status) throws RestClientException {
        publicKeysApi.changeStatusPublicKeyV1(null, null, null, kid, status, null, null);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.publicKeysApi.setApiClient(newApiClient(this.basePath, gherkinSrlBearerToken));
            }
            default ->  {
                this.publicKeysApi.setApiClient(newApiClient(this.basePath, cucumberSpaBearerToken));
            }
        }
    }
}
