package it.pagopa.interop.common.client;

import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class NoAuthApiClient extends ApiClient {

    public NoAuthApiClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected void updateParamsForAuth(String[] authNames,
                                       MultiValueMap<String, String> queryParams,
                                       HttpHeaders headerParams,
                                       MultiValueMap<String, String> cookieParams) {
        // NO-OP: auth gestita dai RestTemplate interceptors (Bearer + DPoP)
    }
}
