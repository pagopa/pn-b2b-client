package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.privateapi.InternalOnlyApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PgUser;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalRegistryPrivateUserApi;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PnExternalRegistryPrivateUserApiImpl implements IPnExternalRegistryPrivateUserApi {

    private final String aldameriniPGBearerToken;
    private final String mariaMontessoriPGBearerToken;
    private final String nildeIottiPGBearerToken;

    private RestTemplate restTemplate;
    private String basePath;

    private InternalOnlyApi externalRegistryUserApi;

    public PnExternalRegistryPrivateUserApiImpl(RestTemplate restTemplate,
                                                @Value("${pn.bearer-token.pg5}") String nildeIottiPGBearerToken,
                                                @Value("${pn.bearer-token.pg3}") String aldameriniPGBearerToken,
                                                @Value("${pn.bearer-token.pg4}") String mariaMontessoriPGBearerToken,
                                                @Value("${pn.webapi.external.base-url}") String basePath) {

        this.aldameriniPGBearerToken = aldameriniPGBearerToken;
        this.mariaMontessoriPGBearerToken = mariaMontessoriPGBearerToken;
        this.nildeIottiPGBearerToken = nildeIottiPGBearerToken;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.externalRegistryUserApi = new InternalOnlyApi(createApiClient(aldameriniPGBearerToken));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public PgUser getPgUsersPrivate(String xPagopaPnUid, String xPagopaPnCxId) throws RestClientException {
        return externalRegistryUserApi.getPgUsersPrivate(xPagopaPnUid, xPagopaPnCxId);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_3 -> this.externalRegistryUserApi.setApiClient(createApiClient(aldameriniPGBearerToken));
            case PG_4 -> this.externalRegistryUserApi.setApiClient(createApiClient(mariaMontessoriPGBearerToken));
            case PG_5 -> this.externalRegistryUserApi.setApiClient(createApiClient(nildeIottiPGBearerToken));
            default ->  {
                throw new IllegalArgumentException("user not allowed");
                }
            }
    }
}
