package it.pagopa.interop.tenant.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TenantVerifiedAttributeSeed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

public interface IM2MTenantsApiClient extends SettableBearerToken {
    void revokeTenantVerifiedAttribute(UUID tenantId, UUID attributeId, UUID agreementId);

    void assignTenantVerifiedAttribute(UUID tenantId, UUID delegationId, UUID agreementId, String expirationDate);
}
