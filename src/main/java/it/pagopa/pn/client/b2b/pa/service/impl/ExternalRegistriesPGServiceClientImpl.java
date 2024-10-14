package it.pagopa.pn.client.b2b.pa.service.impl;
import it.pagopa.pn.client.b2b.pa.service.IExternalPGServiceClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.api.InfoPgApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.model.PaSummary;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.api.InfoPaApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.model.PgGroup;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.model.PgGroupStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExternalRegistriesPGServiceClientImpl implements IExternalPGServiceClient {
    private final InfoPgApi pgApi;
    private final InfoPaApi paApi;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final RestTemplate restTemplate;
    private BearerTokenType bearerTokenSetted;

    public ExternalRegistriesPGServiceClientImpl(RestTemplate restTemplate,
                                                 @Value("${pn.external.dest.base-url}") String basePath,
                                                 @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                                 @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.bearerTokenSetted = BearerTokenType.PG_2;
        this.pgApi = new InfoPgApi(newApiClient(cucumberSpaBearerToken));
        this.paApi = new InfoPaApi(newApiClient(cucumberSpaBearerToken));
    }

    private ApiClient newApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public List<PaSummary> listOnboardedPa(String paNameFilter, List<String> id) throws RestClientException{
        return this.paApi.listOnboardedPa(paNameFilter, id);
    }

    @Override
    public List<PgGroup> getPgUserGroupsPrivate(PgGroupStatus statusFilter) throws RestClientException {
        return this.pgApi.getPgUserGroupsPrivate(statusFilter);
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.pgApi.setApiClient(newApiClient(gherkinSrlBearerToken));
                this.paApi.setApiClient(newApiClient(gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.pgApi.setApiClient(newApiClient(cucumberSpaBearerToken));
                this.paApi.setApiClient(newApiClient(cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }


}
