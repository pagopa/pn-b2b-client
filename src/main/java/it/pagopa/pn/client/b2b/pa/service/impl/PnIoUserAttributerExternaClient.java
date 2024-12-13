package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnExternalApiKeyConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnIoUserAttributerExternaClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.io.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.io.api.CourtesyApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.io.model.IoCourtesyDigitalAddressActivation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnIoUserAttributerExternaClient implements IPnIoUserAttributerExternaClient {

    private final String appIoApiKey;
    private final CourtesyApi courtesyApiIo;

    @Autowired
    public PnIoUserAttributerExternaClient(RestTemplate restTemplate,
                                           PnBaseUrlConfig pnBaseUrlConfig,
                                           PnExternalApiKeyConfig pnExternalApiKeyConfig) {
        this.appIoApiKey = pnExternalApiKeyConfig.getAppIoApiKey();
        this.courtesyApiIo = new CourtesyApi(newApiClient(restTemplate, pnBaseUrlConfig.getAppIoExternalBaseUrl(), appIoApiKey));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apiKey);
        return newApiClient;
    }

    public IoCourtesyDigitalAddressActivation getCourtesyAddressIo(String xPagopaCxTaxid) throws RestClientException {
        return courtesyApiIo.getCourtesyAddressIo(xPagopaCxTaxid);
    }

    public void setCourtesyAddressIo(String xPagopaCxTaxid, IoCourtesyDigitalAddressActivation ioCourtesyDigitalAddressActivation) throws RestClientException {
        courtesyApiIo.setCourtesyAddressIo(xPagopaCxTaxid, ioCourtesyDigitalAddressActivation);
    }
}