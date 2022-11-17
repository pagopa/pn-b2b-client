package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.api.ConsentsApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.model.Consent;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.model.ConsentAction;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.model.ConsentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class PnWebUserAttributesExternalClientImpl implements IPnWebUserAttributesClient {


    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final ConsentsApi ConsentsApi;

    private BearerTokenType bearerTokenSetted = BearerTokenType.USER_1;
    private final String fieramoscaEBearerToken;
    private final String cristoforoCBearerToken;
    private final String userAgent;
    private final String basePath;

    public PnWebUserAttributesExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.bearer-token.FieramoscaE}") String fieramoscaEBearerToken,
            @Value("${pn.bearer-token.CristoforoC}") String cristoforoCBearerToken,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.fieramoscaEBearerToken = fieramoscaEBearerToken;
        this.cristoforoCBearerToken = cristoforoCBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.ConsentsApi = new ConsentsApi( newApiClient( restTemplate, basePath, fieramoscaEBearerToken,userAgent) );
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("user-agent",userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }
    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken){
            case USER_1:
                this.ConsentsApi.setApiClient(newApiClient( restTemplate, basePath, fieramoscaEBearerToken,userAgent));                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.ConsentsApi.setApiClient(newApiClient( restTemplate, basePath, cristoforoCBearerToken,userAgent));                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }


    public void consentAction(ConsentType consentType, ConsentAction consentAction, String version) throws RestClientException {
        this.ConsentsApi.consentAction(consentType, consentAction, version);
    }

    public Consent getConsentByType(ConsentType consentType, String version) throws RestClientException {
        return this.ConsentsApi.getConsentByType(consentType, version);
    }


    public List<Consent> getConsents() throws RestClientException {
        return this.ConsentsApi.getConsents();
    }


}
