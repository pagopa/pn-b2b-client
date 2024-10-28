package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.IB2BDeliveryPushServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class B2BDeliveryPushServiceClientImpl implements IB2BDeliveryPushServiceClient {

    private RestTemplate restTemplate;
    private final String basePath;

    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private LegalFactsApi legalFactsApi;

    public B2BDeliveryPushServiceClientImpl(@Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                            @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                            @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                            @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                            @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                                            RestTemplate restTemplate,
                                            @Value("${pn.delivery.base-url}") String basePath) {
        this.basePath = basePath;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.restTemplate = restTemplate;
        this.legalFactsApi = new LegalFactsApi(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId) throws RestClientException {
        return legalFactsApi.deliveryPushIunDownloadLegalFactsLegalFactIdGet(iun, legalFactId);
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean operation = false;
        switch (bearerToken) {
            case USER_1 -> {
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
                operation = true;
            }
            case USER_2 -> {
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken));
                operation = true;
            }
            case USER_3 -> {
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken));
                operation = true;
            }
            case PG_1 -> {
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                operation = true;
            }
            case PG_2 -> {
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                operation = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return operation;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
    }
}
