package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IM2MTenantClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeRevokers;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeVerifiers;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MTenantClientImpl implements IM2MTenantClient {
    private final TenantsApi tenantsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MTenantClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.tenantsApi = new TenantsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);

        apiClient.setBearerToken(bearerToken);

        return apiClient;
    }

    @Override
    public TenantVerifiedAttributeVerifiers getVerifiers(UUID tenantId, UUID attributeId) {
        return this.tenantsApi.getTenantVerifiedAttributeVerifiers(tenantId, attributeId, 0, 30);
    }

    @Override
    public TenantVerifiedAttributeRevokers getRevokers(UUID tenantId, UUID attributeId) {
        return this.tenantsApi.getTenantVerifiedAttributeRevokers(tenantId, attributeId, 0, 30);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.tenantsApi.setApiClient(createApiClient(bearerToken));
    }
}
