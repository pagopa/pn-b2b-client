package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeRevokers;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeVerifiers;
import java.util.UUID;

public interface IM2MTenantClient extends SettableBearerToken {
    TenantVerifiedAttributeVerifiers getVerifiers(UUID tenantId, UUID attributeId);
    TenantVerifiedAttributeRevokers getRevokers(UUID organizationId, UUID verifiedAttributeId);
    void revokeTenantVerifiedAttribute(UUID tenantId, UUID attributeId, UUID agreementId);
    void revokeTenantVerifiedAttribute(UUID tenantId, UUID delegationId, UUID attributeId, UUID agreementId);
    void assignTenantVerifiedAttribute(UUID tenantId, UUID attributeId, UUID agreementId, String expirationDate);
    void assignTenantVerifiedAttribute(UUID tenantId, UUID delegationId, UUID attributeId, UUID agreementId, String expirationDate);
}
