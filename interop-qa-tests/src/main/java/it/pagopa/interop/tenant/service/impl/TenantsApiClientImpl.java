package it.pagopa.interop.tenant.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RequesterCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.RevokeVerifiedAttributeRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantDelegatedFeaturesFlagsUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantFeatureType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenants;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateVerifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
import it.pagopa.interop.tenant.service.ITenantsApi;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
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
    public DeclaredAttributesResponse getDeclaredAttributes(UUID tenantId) {
        return tenantsApi.getDeclaredAttributes(tenantId);
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
    public void updateVerifiedAttribute(UUID tenantId, UUID attributeId, UpdateVerifiedTenantAttributeSeed updateVerifiedTenantAttributeSeed) {
        tenantsApi.updateVerifiedAttribute(tenantId, attributeId, updateVerifiedTenantAttributeSeed);
    }

    @Override
    public Tenant getTenant(UUID tenantId) {
        return tenantsApi.getTenant(tenantId);
    }

    @Override
    public Tenants getTenants(Integer limit, String name, List<TenantFeatureType> features) {
        return tenantsApi.getTenants(limit, name, features);
    }

    @Override
    public void verifyVerifiedAttribute(UUID tenantId, VerifiedTenantAttributeSeed verifiedTenantAttributeSeed) {
        tenantsApi.verifyVerifiedAttribute(tenantId, verifiedTenantAttributeSeed);
    }

    @Override
    public VerifiedAttributesResponse getVerifiedAttributes(UUID tenantId) {
        return tenantsApi.getVerifiedAttributes(tenantId);
    }

    @Override
    public void revokeCertifiedAttribute(UUID tenantId, UUID attributeId) {
        tenantsApi.revokeCertifiedAttribute(tenantId, attributeId);
    }

    @Override
    public void revokeVerifiedAttribute(UUID tenantId, UUID attributeId, UUID agreementId) {
        tenantsApi.revokeVerifiedAttribute(tenantId, attributeId, new RevokeVerifiedAttributeRequest().agreementId(agreementId));
    }

    @Override
    public void revokeDeclaredAttribute(UUID attributeId) {
        tenantsApi.revokeDeclaredAttribute(attributeId);
    }

    @Override
    public CompactOrganizations getConsumers(Integer offset, Integer limit, String q) {
        return tenantsApi.getConsumers(offset, limit, q);
    }

    @Override
    public CompactOrganizations getProducers(Integer offset, Integer limit, String q) {
        return tenantsApi.getProducers(offset, limit, q);
    }

    @Override
    public void addTenantMail(UUID tenantId, MailSeed mailSeed) {
        tenantsApi.addTenantMail(tenantId, mailSeed);
    }

    @Override
    public RequesterCertifiedAttributes getRequesterCertifiedAttributes(Integer offset, Integer limit) {
        return tenantsApi.getRequesterCertifiedAttributes(offset, limit);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.tenantsApi.setApiClient(createApiClient(bearerToken));
    }

}
