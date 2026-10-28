package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bExternalInformalClient;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.INTEROP_ENABLED;

@Component
public class PnPaB2bExternalInformalClientImpl implements IPnPaB2bExternalInformalClient {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final RestTemplate restTemplate;
    private final String basePath;
    private final String enableInterop;
    private final InteropTokenSingleton interopTokenSingleton;

    public PnPaB2bExternalInformalClientImpl(RestTemplate restTemplate,
                                              @Value("${pn.external.informal-base-url}") String basePath,
                                              @Value("${pn.interop.enable}") String enableInterop,
                                              InteropTokenSingleton interopTokenSingleton) {

        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.enableInterop = enableInterop;
        this.interopTokenSingleton = interopTokenSingleton;
    }

    @Override
    public List<InformalPreLoadResponse> informalPresignedUploadRequest(String apiKey, List<InformalPreLoadRequest> requests) {
        NewInformalNotificationApi api = new NewInformalNotificationApi(newExternalInformalApiClient(apiKey));
        return api.informalPresignedUploadRequest(requests);
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient newExternalInformalApiClient(String apiKey) {

        var client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient(restTemplate);
        client.setBasePath(basePath);
        client.addDefaultHeader("x-api-key", apiKey);
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            client.addDefaultHeader(AUTHORIZATION, BEARER + interopTokenSingleton.getTokenInterop());
        }
        return client;
    }
}
