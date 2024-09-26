package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IBffMandateServiceApi;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.api.MandateApi;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffMandate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BffMandateServiceClientImpl implements IBffMandateServiceApi {
    private final RestTemplate restTemplate;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final MandateApi mandateApi;
    private BearerTokenType bearerTokenSetted;

    public BffMandateServiceClientImpl(RestTemplate restTemplate,
                                       @Value("${pn.webapi.external.base-url}") String basePath,
                                       @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                       @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                       @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                       @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken) {
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.bearerTokenSetted = BearerTokenType.USER_1;
        this.mandateApi = new MandateApi(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public List<BffMandate> getMandatesByDelegatorV1() throws RestClientException {
        return mandateApi.getMandatesByDelegatorV1();
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
            }
            case USER_2 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
            }
            case PG_1 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
            }
            case PG_2 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }
}
