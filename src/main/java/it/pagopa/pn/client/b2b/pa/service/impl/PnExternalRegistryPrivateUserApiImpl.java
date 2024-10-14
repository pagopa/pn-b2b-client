package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.privateapi.InternalOnlyApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PgUser;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalRegistryPrivateUserApi;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PnExternalRegistryPrivateUserApiImpl implements IPnExternalRegistryPrivateUserApi {

    private RestTemplate restTemplate;
    private String basePath;

    private InternalOnlyApi externalRegistryUserApi;

    public PnExternalRegistryPrivateUserApiImpl(RestTemplate restTemplate,
                                                String basePath) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.externalRegistryUserApi = new InternalOnlyApi(createApiClient());
    }

    private ApiClient createApiClient() {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public PgUser getPgUsersPrivate(String xPagopaPnUid, String xPagopaPnCxId) throws RestClientException {
        return externalRegistryUserApi.getPgUsersPrivate(xPagopaPnUid, xPagopaPnCxId);
    }
}
