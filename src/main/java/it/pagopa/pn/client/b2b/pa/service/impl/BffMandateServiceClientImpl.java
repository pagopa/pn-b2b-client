package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenConfigs;
import it.pagopa.pn.client.b2b.pa.service.IBffMandateServiceApi;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.api.MandateApi;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffMandate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BffMandateServiceClientImpl implements IBffMandateServiceApi {
    private final RestTemplate restTemplate;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final MandateApi mandateApi;
    private BearerTokenType bearerTokenSetted;

    @Autowired
    public BffMandateServiceClientImpl(RestTemplate restTemplate,
                                       PnBearerTokenConfigs pnBearerTokenConfigs,
                                       PnBaseUrlConfig pnBaseUrlConfig) {
        this.restTemplate = restTemplate;
        this.gherkinSrlBearerToken = pnBearerTokenConfigs.getPg1();
        this.cucumberSpaBearerToken = pnBearerTokenConfigs.getPg2();
        this.basePath = pnBaseUrlConfig.getWebApiExternalBaseUrl();
        this.bearerTokenSetted = BearerTokenType.PG_1;
        this.mandateApi = new MandateApi(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public List<BffMandate> getMandatesByDelegatorV1() throws RestClientException {
        return mandateApi.getMandatesByDelegatorV1();
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
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
