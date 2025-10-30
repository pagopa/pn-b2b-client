package it.pagopa.interop.selfcare.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.SelfcareApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.User;
import it.pagopa.interop.selfcare.service.ISelfcareClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SelfcareClientImpl implements ISelfcareClient {
    private final SelfcareApi selfcareApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public SelfcareClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.selfcareApi = new SelfcareApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken)  {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public ResponseEntity<List<User>> getInstitutionUsers(UUID tenantId, UUID personId, List<String> roles, String query) {
        return selfcareApi.getInstitutionUsersWithHttpInfo(tenantId, personId, roles, query);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.selfcareApi.setApiClient(createApiClient(bearerToken));
    }
}
