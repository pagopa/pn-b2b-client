package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IBffMandateServiceApi;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.api.MandateApi;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffMandate;
import org.springframework.beans.factory.annotation.Value;
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
    private final String user1BearerToken;
    private final String user2BearerToken;
    private final String basePath;
    private final MandateApi mandateApi;
    private BearerTokenType bearerTokenSetted;

    public BffMandateServiceClientImpl(RestTemplate restTemplate,
                                       @Value("${pn.webapi.external.base-url}") String basePath,
                                       @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                       @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                                       @Value("${pn.bearer-token.user1}") String user1BearerToken,
                                       @Value("${pn.bearer-token.user2}") String user2BearerToken) {
        this.restTemplate = restTemplate;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.bearerTokenSetted = BearerTokenType.PG_1;
        this.user1BearerToken = user1BearerToken;
        this.user2BearerToken = user2BearerToken;
        this.mandateApi = new MandateApi(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
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
        switch (bearerToken) {
            case PG_1 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
            }
            case USER_1 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, user1BearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_1;
            }
            case USER_2 -> {
                this.mandateApi.setApiClient(newApiClient(restTemplate, basePath, user2BearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_2;
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
