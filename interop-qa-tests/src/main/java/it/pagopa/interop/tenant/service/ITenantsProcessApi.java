package it.pagopa.interop.tenant.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ITenantsProcessApi extends SettableBearerToken {
    ResponseEntity<Tenant> getTenant(String xCorrelationId, UUID organizationId);

    ResponseEntity<Void> maintenanceTenantUpdate(String xCorrelationId, UUID organizationId, MaintenanceTenantUpdatePayload mapped);
}
