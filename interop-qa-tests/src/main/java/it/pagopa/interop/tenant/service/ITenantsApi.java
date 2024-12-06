package it.pagopa.interop.tenant.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;

import java.util.UUID;

public interface ITenantsApi {
    void addCertifiedAttribute(UUID tenantId, CertifiedTenantAttributeSeed certifiedTenantAttributeSeed);
    CertifiedAttributesResponse getCertifiedAttributes(UUID tenantId);
    void addDeclaredAttribute(DeclaredTenantAttributeSeed declaredTenantAttributeSeed);
    DeclaredAttributesResponse getDeclaredAttributes(String xCorrelationId, UUID tenantId);
}
