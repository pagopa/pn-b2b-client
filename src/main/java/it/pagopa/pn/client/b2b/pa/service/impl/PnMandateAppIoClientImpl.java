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

    private final AppIoPnMandateCreateApi appIoMandateApi;

    public PnMandateAppIoClientImpl(RestTemplate restTemplate,
                                    @Value("${pn.appio.externa.base-url}") String appIoBaseUrl,
                                    @Value("${pn.external.appio.api-key}") String appIoApiKey) {
        this.appIoMandateApi = new AppIoPnMandateCreateApi(newApiClient(restTemplate, appIoBaseUrl, appIoApiKey));
    }


    private ApiClient newApiClient(RestTemplate restTemplate, String appIoBaseUrl, String appIoApiKey) {
        log.info("APP IO BASEURL: " + appIoBaseUrl);
        log.info("APP IO APIKEY: " + appIoApiKey);
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(appIoBaseUrl);
        newApiClient.addDefaultHeader("x-api-key", appIoApiKey);
        return newApiClient;
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
