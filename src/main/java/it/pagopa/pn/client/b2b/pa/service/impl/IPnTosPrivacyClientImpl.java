package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.tos.privacy.UserConsentsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffConsent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyActionBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType;
import it.pagopa.pn.client.b2b.pa.service.IPnTosPrivacyClient;
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
    public void acceptTosPrivacyV1(List<BffTosPrivacyActionBody> bffTosPrivacyBody) throws RestClientException {
        userConsentsApi.acceptPgTosPrivacyV1(bffTosPrivacyBody);
    }

    @Override
    public List<BffConsent> getTosPrivacyV1(List<ConsentType> consentTypes) throws RestClientException {
        return userConsentsApi.getPgConsentByType(consentTypes);
    }

    @Override
    public void acceptTosPrivacyV2(List<BffTosPrivacyActionBody> bffTosPrivacyBody) throws RestClientException {
        userConsentsApi.acceptTosPrivacyV2(bffTosPrivacyBody);
    }

    @Override
    public List<BffConsent> getTosPrivacyV2(List<ConsentType> consentTypes) throws RestClientException {
        return userConsentsApi.getTosPrivacyV2(consentTypes);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.userConsentsApi.setApiClient(newApiClient(gherkinSrlBearerToken));
            }
            case PG_2 -> {
                this.userConsentsApi.setApiClient(newApiClient(cucumberSpaBearerToken));
            }
            default ->  {
                this.userConsentsApi.setApiClient(newApiClient(cucumberSpaBearerToken));
            }
        }
    }
}
