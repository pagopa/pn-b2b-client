package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.api.MandateReverseServiceApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenConfigs;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MandateReverseServiceClientImpl implements IMandateReverseServiceClient {
    private final MandateReverseServiceApi mandateReverseServiceApi;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final RestTemplate restTemplate;
    private BearerTokenType bearerTokenSetted;

    @Autowired
    public MandateReverseServiceClientImpl(RestTemplate restTemplate,
                                           PnBearerTokenConfigs pnBearerTokenConfigs,
                                           PnBaseUrlConfig pnBaseUrlConfig) {
        this.gherkinSrlBearerToken = pnBearerTokenConfigs.getPg1();
        this.cucumberSpaBearerToken = pnBearerTokenConfigs.getB2bPg2();
        this.basePath = pnBaseUrlConfig.getExternalDestBaseUrl();
        this.restTemplate = restTemplate;
        this.bearerTokenSetted = BearerTokenType.PG_2;
        this.mandateReverseServiceApi = new MandateReverseServiceApi(newApiClient(cucumberSpaBearerToken));
    }

    private ApiClient newApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public String createReverseMandate(MandateDtoRequest mandateDtoRequest) throws RestClientException {
        return mandateReverseServiceApi.createReverseMandate(mandateDtoRequest);
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.mandateReverseServiceApi.setApiClient(newApiClient(gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.mandateReverseServiceApi.setApiClient(newApiClient(cucumberSpaBearerToken));
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
