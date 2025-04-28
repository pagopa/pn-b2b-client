package it.pagopa.interop.tenant.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;

import java.util.UUID;

public interface ITenantsApi extends SettableBearerToken {
    void addCertifiedAttribute(UUID tenantId, CertifiedTenantAttributeSeed certifiedTenantAttributeSeed);

    CertifiedAttributesResponse getCertifiedAttributes(UUID tenantId);

    void addDeclaredAttribute(DeclaredTenantAttributeSeed declaredTenantAttributeSeed);

    DeclaredAttributesResponse getDeclaredAttributes(UUID tenantId);

    void updateTenantDelegatedFeatures(boolean isProducerFeatureEnabled, boolean isConsumerFeatureEnabled);

    Tenant getTenant(UUID tenantId);

    void verifyVerifiedAttribute(UUID tenantId, VerifiedTenantAttributeSeed verifiedTenantAttributeSeed);

    VerifiedAttributesResponse getVerifiedAttributes(UUID tenantId);

    void revokeCertifiedAttribute(UUID tenantId, UUID attributeId);

    void revokeVerifiedAttribute(UUID tenantId, UUID attributeId, UUID agreementId);

    void revokeDeclaredAttribute(UUID attributeId);
}
