package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.api.AppIoPnMandateCreateApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.CIEValidationData;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateIo.model.MandateCreationResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnMandateAppIoClient;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.ApiClient;
import lombok.extern.slf4j.Slf4j;
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

    private BearerTokenType bearerTokenSetted;
    private final AppIoPnMandateCreateApi appIoMandateApi;

    public PnMandateAppIoClientImpl(RestTemplate restTemplate, AppIoPnMandateCreateApi appIoMandateApi) {
        this.restTemplate = restTemplate;
        this.appIoMandateApi = appIoMandateApi;
    }


    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
//        switch (bearerToken) {
//            case USER_1 -> {
//                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
//                this.bearerTokenSetted = BearerTokenType.USER_1;
//                beenSet = true;
//            }
//            case USER_2 -> {
//                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
//                this.bearerTokenSetted = BearerTokenType.USER_2;
//                beenSet = true;
//            }
//            case USER_3 -> {
//                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
//                this.bearerTokenSetted = BearerTokenType.USER_3;
//                beenSet = true;
//            }
//            case PG_1 -> {
//                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
//                this.bearerTokenSetted = BearerTokenType.PG_1;
//                beenSet = true;
//            }
//            case PG_2 -> {
//                this.mandateServiceApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
//                this.bearerTokenSetted = BearerTokenType.PG_2;
//                beenSet = true;
//            }
//            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
//        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
    }

    @Override
    public MandateCreationResponse createIOMandate(String xPagopaCxTaxid, String xPagopaLollipopOriginalUrl, String xPagopaLollipopOriginalMethod, String xPagopaLollipopPublicKey, String xPagopaLollipopAssertionRef, String xPagopaLollipopAssertionType, String xPagopaLollipopAuthJwt, String xPagopaLollipopUserId, String signatureInput, String signature, MandateCreationRequest mandateCreationRequest) throws RestClientException {
        return null;
    }

    @Override
    public void acceptIOMandate(String xPagopaCxTaxid, String mandateId, String xPagopaLollipopOriginalUrl, String xPagopaLollipopOriginalMethod, String xPagopaLollipopPublicKey, String xPagopaLollipopAssertionRef, String xPagopaLollipopAssertionType, String xPagopaLollipopAuthJwt, String xPagopaLollipopUserId, String signatureInput, String signature, CIEValidationData ciEValidationData) throws RestClientException {

    }
}
