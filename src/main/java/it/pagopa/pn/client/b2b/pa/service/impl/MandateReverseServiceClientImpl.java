package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.api.MandateReverseServiceApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.pa.service.IMandateReverseServiceClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class MandateReverseServiceClientImpl implements IMandateReverseServiceClient {
    private final MandateReverseServiceApi mandateReverseServiceApi;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final RestTemplate restTemplate;

    public MandateReverseServiceClientImpl(RestTemplate restTemplate,
                                           @Value("${pn.external.dest.base-url}") String basePath,
                                           @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                           @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken) {
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.mandateReverseServiceApi = new MandateReverseServiceApi(newApiClient(cucumberSpaBearerToken));
    }

    private ApiClient newApiClient(String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
//        newApiClient.setBearerToken(bearerToken);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public ResponseEntity<String> createReverseMandateWithHttpInfo(MandateDtoRequest mandateDtoRequest) throws RestClientException {
        return mandateReverseServiceApi.createReverseMandateWithHttpInfo(mandateDtoRequest);
    }

    @Override
    public void setBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.mandateReverseServiceApi.setApiClient(newApiClient(gherkinSrlBearerToken));
            }
            default ->  {
                this.mandateReverseServiceApi.setApiClient(newApiClient(cucumberSpaBearerToken));
            }
        }
    }


}
