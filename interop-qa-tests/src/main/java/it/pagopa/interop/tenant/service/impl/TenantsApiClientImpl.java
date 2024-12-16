package it.pagopa.interop.tenant.service.impl;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.tenant.service.ITenantsApi;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class TenantsApiClientImpl implements ITenantsApi {
    private final TenantsApi tenantsApi;
    private final RestTemplate restTemplate;
    private final InteropClientConfigs interopClientConfigs;
    private final String basePath;
    private final String bearerToken;

    public TenantsApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.interopClientConfigs = interopClientConfigs;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.bearerToken = "bearerToken";
        this.tenantsApi = new TenantsApi(createApiClient(bearerToken));
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
    public void setBearerToken(String bearerToken) {
        this.tenantsApi.setApiClient(createApiClient(bearerToken));
    }

}