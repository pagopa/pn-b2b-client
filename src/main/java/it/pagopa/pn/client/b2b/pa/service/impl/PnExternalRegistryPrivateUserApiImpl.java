package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.privateapi.InternalOnlyApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.privateapi.PaperCostApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.selfcare.privateapi.InfoPaApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.selfcare.privateapi.InfoPgApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PaperCostToInvalidate;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PgUser;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.PaSummary;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.PgGroup;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalRegistryPrivateUserApi;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PnExternalRegistryPrivateUserApiImpl implements IPnExternalRegistryPrivateUserApi {

    private final String pg2BearerToken;
    private final String aldameriniPGBearerToken;
    private final String mariaMontessoriPGBearerToken;
    private final String nildeIottiPGBearerToken;

    private RestTemplate restTemplate;
    private String basePath;

    private InternalOnlyApi externalRegistryUserApi;

    private PaperCostApi paperCostApi;

    private InfoPaApi infoPaApi;

    private InfoPgApi infoPgApi;

    public PnExternalRegistryPrivateUserApiImpl(RestTemplate restTemplate,
                                                @Value("${pn.bearer-token.pg2}") String pg2BearerToken,
                                                @Value("${pn.bearer-token.pg3}") String aldameriniPGBearerToken,
                                                @Value("${pn.bearer-token.pg4}") String mariaMontessoriPGBearerToken,
                                                @Value("${pn.bearer-token.pg5}") String nildeIottiPGBearerToken,
                                                @Value("${pn.delivery.base-url}") String basePath) {

        this.pg2BearerToken = pg2BearerToken;
        this.aldameriniPGBearerToken = aldameriniPGBearerToken;
        this.mariaMontessoriPGBearerToken = mariaMontessoriPGBearerToken;
        this.nildeIottiPGBearerToken = nildeIottiPGBearerToken;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.externalRegistryUserApi = new InternalOnlyApi(createApiClient(pg2BearerToken));
        this.paperCostApi = new PaperCostApi(createApiClient(pg2BearerToken));
        this.infoPaApi = new InfoPaApi(createSelfcareApiClient(pg2BearerToken));
        this.infoPgApi = new InfoPgApi(createSelfcareApiClient(pg2BearerToken));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    private it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.selfcare.ApiClient createSelfcareApiClient(String bearerToken) {
        var newApiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.generate.api.externalregistry.selfcare.ApiClient(restTemplate);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public PgUser getPgUsersPrivate(String xPagopaPnUid, String xPagopaPnCxId) throws RestClientException {
        return externalRegistryUserApi.getPgUsersPrivate(xPagopaPnUid, xPagopaPnCxId);
    }

    @Override
    public void invalidatePaperCost(String iun, PaperCostToInvalidate paperCostToInvalidate) throws RestClientException {
        paperCostApi.invalidatePaperCost(iun, paperCostToInvalidate);
    }

    @Override
    public List<PaSummary> listOnboardedPa(String paNameFilter, List<String> ids) throws RestClientException {
        return infoPaApi.listOnboardedPa(paNameFilter, ids);
    }

    @Override
    public List<PgGroup> getPgUserGroups(String xPagopaPnCxId, String xPagopaPnUid) throws RestClientException {
        return infoPgApi.getPgUserGroupsPrivate(xPagopaPnCxId, xPagopaPnUid, null);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        String token = switch (bearerToken) {
            case PG_2 -> pg2BearerToken;
            case PG_3 -> aldameriniPGBearerToken;
            case PG_4 -> mariaMontessoriPGBearerToken;
            case PG_5 -> nildeIottiPGBearerToken;
            default -> throw new IllegalArgumentException("user not allowed");
        };
        this.externalRegistryUserApi.setApiClient(createApiClient(token));
        this.infoPaApi.setApiClient(createSelfcareApiClient(token));
        this.infoPgApi.setApiClient(createSelfcareApiClient(token));
    }
}
