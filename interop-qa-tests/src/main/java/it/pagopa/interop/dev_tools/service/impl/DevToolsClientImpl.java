package it.pagopa.interop.dev_tools.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.dev_tools.service.IDevToolsClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ToolsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.TokenGenerationValidationResult;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DevToolsClientImpl extends AbstractClient implements IDevToolsClient {
    private final ToolsApi toolsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public DevToolsClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.basePath = interopClientConfigs.getBaseUrl();
        this.restTemplate = restTemplate;
        this.toolsApi = new ToolsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public TokenGenerationValidationResult validateTokenGeneration(String clientAssertion, String clientAssertionType, String grantType, String clientId, Boolean isAsync, String dpopProof) {
        String asyncParam = isAsync == null ? null : isAsync.toString();
        return performOperation(
                () -> toolsApi.validateTokenGenerationWithHttpInfo(clientAssertion, clientAssertionType, grantType, UUID.fromString(clientId), asyncParam, dpopProof)
        ).orElseThrow(
                () -> new IllegalStateException("Failed to validate token generation request after retries")
        );
    }

    @Override
    public void setBearerToken(String bearerToken) {
        toolsApi.getApiClient().setBearerToken(bearerToken);
    }
}
