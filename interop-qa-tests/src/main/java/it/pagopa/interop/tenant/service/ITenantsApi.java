package it.pagopa.interop.tenant.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RequesterCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantFeatureType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenants;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateVerifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ITenantsApi extends SettableBearerToken {
    void addCertifiedAttribute(UUID tenantId, CertifiedTenantAttributeSeed certifiedTenantAttributeSeed);

    CertifiedAttributesResponse getCertifiedAttributes(UUID tenantId);

    void addDeclaredAttribute(DeclaredTenantAttributeSeed declaredTenantAttributeSeed);

    DeclaredAttributesResponse getDeclaredAttributes(UUID tenantId);

    void updateTenantDelegatedFeatures(boolean isProducerFeatureEnabled, boolean isConsumerFeatureEnabled);

    void updateVerifiedAttribute(UUID tenantId, UUID attributeId, UpdateVerifiedTenantAttributeSeed updateVerifiedTenantAttributeSeed);

    Tenant getTenant(UUID tenantId);

    Tenants getTenants(Integer limit, String name, List<TenantFeatureType> features);

    void verifyVerifiedAttribute(UUID tenantId, VerifiedTenantAttributeSeed verifiedTenantAttributeSeed);

    VerifiedAttributesResponse getVerifiedAttributes(UUID tenantId);

    RequesterCertifiedAttributes getRequesterCertifiedAttributes(Integer offset, Integer limit);

    void revokeCertifiedAttribute(UUID tenantId, UUID attributeId);

    void revokeVerifiedAttribute(UUID tenantId, UUID attributeId, UUID agreementId);

    void revokeDeclaredAttribute(UUID attributeId);

    CompactOrganizations getConsumers(Integer offset, Integer limit, String q);

    CompactOrganizations getProducers(Integer offset, Integer limit, String q);

    void addTenantMail(UUID tenantId, MailSeed mailSeed);

    ResponseEntity<Void> addTenantMailWithHttpInfo(UUID tenantId, MailSeed mailSeed);

    Boolean isTenantAllowedToDelegation(UUID tenantId);
}
