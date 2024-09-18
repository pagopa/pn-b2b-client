package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.privacy.UserConsentsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyConsent;
import it.pagopa.pn.client.b2b.pa.service.IPnTosPrivacyClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IPnTosPrivacyClientImpl implements IPnTosPrivacyClient {
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private String basePath;

    private RestTemplate restTemplate;

    UserConsentsApi userConsentsApi;

    public IPnTosPrivacyClientImpl(RestTemplate restTemplate,
                                   @Value("${pn.webapi.external.base-url}") String basePath,
                                   @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                   @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken) {
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.userConsentsApi = new UserConsentsApi(newApiClient(cucumberSpaBearerToken));
    }

    private ApiClient newApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(this.restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public void acceptTosPrivacyV1(BffTosPrivacyBody bffTosPrivacyBody) throws RestClientException {
        userConsentsApi.acceptTosPrivacyV1(bffTosPrivacyBody);
    }

    @Override
    public BffTosPrivacyConsent getTosPrivacyV1() throws RestClientException {
        return userConsentsApi.getTosPrivacyV1();
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.userConsentsApi.setApiClient(newApiClient(gherkinSrlBearerToken));
            }
            default ->  {
                this.userConsentsApi.setApiClient(newApiClient(cucumberSpaBearerToken));
            }
        }
    }
}
