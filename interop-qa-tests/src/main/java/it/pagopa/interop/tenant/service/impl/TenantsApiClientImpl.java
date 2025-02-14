package it.pagopa.interop.tenant.service.impl;

import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantDelegatedFeaturesFlagsUpdateSeed;
import it.pagopa.interop.tenant.service.ITenantsApi;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantsApiClientImpl implements ITenantsApi {
    private final TenantsApi tenantsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public TenantsApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
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
    public void addCertifiedAttribute(UUID tenantId, CertifiedTenantAttributeSeed certifiedTenantAttributeSeed) {
        tenantsApi.addCertifiedAttribute(tenantId, certifiedTenantAttributeSeed);
    }

    @Override
    public CertifiedAttributesResponse getCertifiedAttributes(UUID tenantId) {
       return tenantsApi.getCertifiedAttributes(tenantId);
    }

    @Override
    public void addDeclaredAttribute(DeclaredTenantAttributeSeed declaredTenantAttributeSeed) {
        tenantsApi.addDeclaredAttribute(declaredTenantAttributeSeed);
    }

    @Override
    public DeclaredAttributesResponse getDeclaredAttributes(String xCorrelationId, UUID tenantId) {
        return tenantsApi.getDeclaredAttributes(xCorrelationId, tenantId);
    }

    @Override
    public void updateTenantDelegatedFeatures(boolean isProducerFeatureEnabled, boolean isConsumerFeatureEnabled) {
        tenantsApi.updateTenantDelegatedFeatures(
                new TenantDelegatedFeaturesFlagsUpdateSeed()
                        .isDelegatedProducerFeatureEnabled(isProducerFeatureEnabled)
                        .isDelegatedConsumerFeatureEnabled(isConsumerFeatureEnabled)
        );
    }

    @Override
    public Tenant getTenant(String xCorrelationId, UUID tenantId) {
        return tenantsApi.getTenant(xCorrelationId, tenantId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.tenantsApi.setApiClient(createApiClient(bearerToken));
    }

}
