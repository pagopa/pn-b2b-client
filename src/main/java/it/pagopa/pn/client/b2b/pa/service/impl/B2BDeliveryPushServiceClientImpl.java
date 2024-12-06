package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenConfigs;
import it.pagopa.pn.client.b2b.pa.service.IB2BDeliveryPushServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class B2BDeliveryPushServiceClientImpl implements IB2BDeliveryPushServiceClient {
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final LegalFactsApi legalFactsApi;

    @Autowired
    public B2BDeliveryPushServiceClientImpl(RestTemplate restTemplate,
                                            PnBearerTokenConfigs pnBearerTokenConfigs,
                                            PnBaseUrlConfig pnBaseUrlConfig) {
        this.basePath = pnBaseUrlConfig.getDeliveryBaseUrl();
        this.marioCucumberBearerToken = pnBearerTokenConfigs.getUser1();
        this.marioGherkinBearerToken = pnBearerTokenConfigs.getUser2();
        this.leonardoBearerToken = pnBearerTokenConfigs.getUser3();
        this.gherkinSrlBearerToken = pnBearerTokenConfigs.getPg1();
        this.cucumberSpaBearerToken = pnBearerTokenConfigs.getPg2();
        this.restTemplate = restTemplate;
        this.legalFactsApi = new LegalFactsApi(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId) throws RestClientException {
        return legalFactsApi.deliveryPushIunDownloadLegalFactsLegalFactIdGet(iun, legalFactId);
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case USER_1 ->
                    this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
            case USER_2 ->
                    this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken));
            case USER_3 -> this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken));
            case PG_1 -> this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
            case PG_2 -> this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
    }
}
