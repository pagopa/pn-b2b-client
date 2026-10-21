package it.pagopa.interop.tenant.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeSeed;
import it.pagopa.interop.tenant.service.IM2MTenantsApiClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
public class M2MTenantsApiClientImpl extends AbstractClient implements IM2MTenantsApiClient {
    private final TenantsApi tenantsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MTenantsApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.tenantsApi = new TenantsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void revokeTenantVerifiedAttribute(UUID tenantId, UUID attributeId, UUID agreementId) {
        this.tenantsApi.revokeTenantVerifiedAttribute(tenantId, attributeId, agreementId);
    }

    @Override
    public void assignTenantVerifiedAttribute(UUID tenantId, UUID delegationId, UUID agreementId, String expirationDate) {
        // TODO tuning per delegationId
        TenantVerifiedAttributeSeed seed = new TenantVerifiedAttributeSeed();
        seed.setAgreementId(agreementId);
        seed.setExpirationDate(expirationDate);
        this.tenantsApi.assignTenantVerifiedAttribute(tenantId, seed);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.tenantsApi.setApiClient(createApiClient(bearerToken));
    }

}
