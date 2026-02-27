package it.pagopa.interop.utils;

import it.pagopa.interop.common.client.NoAuthApiClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

// NOTE 17/02/2026: potrebbe rendere meglio come componente Spring che come classe di utils
public final class ApiClientUtils {
    public static final String V3_UNSUPPORTED_BEARER_MSG = "I client M2M v3 non supportano l'autenticazione attraverso Bearer token";

    private ApiClientUtils() {
        throw new AssertionError("You're trying to instantiate an utility class");
    }

    public static ApiClient createApiClient(RestTemplate restTemplate, String basePath, Map<String, String> headers
    ) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);

        for(var header : headers.entrySet()) {
            apiClient.addDefaultHeader(header.getKey(), header.getValue());
        }

        return apiClient;
    }

    public static ApiClient createApiClient(DpopRestTemplate dpopRestTemplate, String basePath, Map<String, String> headers
    ) {
        ApiClient apiClient = new NoAuthApiClient(dpopRestTemplate.getRestTemplate());
        apiClient.setBasePath(basePath);
        return apiClient;
    }
}
