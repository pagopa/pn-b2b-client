package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.api.AppIoPnMandateCreateApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.CIEValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnMandateAppIoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnMandateAppIoClientImpl implements IPnMandateAppIoClient {

    private final RestTemplate restTemplate;
    private String marioCucumberBearerToken;
    private String marioGherkinBearerToken;
    private String leonardoBearerToken;
    private String gherkinSrlBearerToken;
    private String cucumberSpaBearerToken;

    private BearerTokenType bearerTokenSetted;

    private final String basePath;
    private final AppIoPnMandateCreateApi appIoMandateApi;

    public PnMandateAppIoClientImpl(RestTemplate restTemplate,
                                    @Value("${pn.appio.externa.base-url.pagopa}") String basePath,
                                    @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                    @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                    @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                    @Value("${pn.bearer-token.user4}") String galileoBearerToken,
                                    @Value("${pn.bearer-token.user5}") String dinoBearerToken,
                                    @Value("${pn.bearer-token.scaduto}") String userBearerTokenScaduto,
                                    @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                    @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.bearerTokenSetted = BearerTokenType.PG_1;
        this.appIoMandateApi = new AppIoPnMandateCreateApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken));
    }


    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
//        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1 -> {
                this.appIoMandateApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
            }
            case USER_2 -> {
                this.appIoMandateApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
            }
            case USER_3 -> {
                this.appIoMandateApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
            }
            case PG_1 -> {
                this.appIoMandateApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
            }
            case PG_2 -> {
                this.appIoMandateApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
    }

    @Override
    public MandateCreationResponse createIOMandate(String xPagopaCxTaxid, String xPagopaLollipopOriginalUrl, String xPagopaLollipopOriginalMethod, String xPagopaLollipopPublicKey, String xPagopaLollipopAssertionRef, String xPagopaLollipopAssertionType, String xPagopaLollipopAuthJwt, String xPagopaLollipopUserId, String signatureInput, String signature, MandateCreationRequest mandateCreationRequest) throws RestClientException {
        return appIoMandateApi.createIOMandate(
                xPagopaCxTaxid,
                xPagopaLollipopOriginalUrl,
                xPagopaLollipopOriginalMethod,
                xPagopaLollipopPublicKey,
                xPagopaLollipopAssertionRef,
                xPagopaLollipopAssertionType,
                xPagopaLollipopAuthJwt,
                xPagopaLollipopUserId,
                signatureInput,
                signature,
                mandateCreationRequest);
    }

    @Override
    public void acceptIOMandate(String xPagopaCxTaxid, String mandateId, String xPagopaLollipopOriginalUrl, String xPagopaLollipopOriginalMethod, String xPagopaLollipopPublicKey, String xPagopaLollipopAssertionRef, String xPagopaLollipopAssertionType, String xPagopaLollipopAuthJwt, String xPagopaLollipopUserId, String signatureInput, String signature, CIEValidationData ciEValidationData) throws RestClientException {
        appIoMandateApi.acceptIOMandate(
                xPagopaCxTaxid,
                mandateId,
                xPagopaLollipopOriginalUrl,
                xPagopaLollipopOriginalMethod,
                xPagopaLollipopPublicKey,
                xPagopaLollipopAssertionRef,
                xPagopaLollipopAssertionType,
                xPagopaLollipopAuthJwt,
                xPagopaLollipopUserId,
                signatureInput,
                signature,
                ciEValidationData);
    }
}
