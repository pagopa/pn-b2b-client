package it.pagopa.interop.authorization.service.identity;

import it.pagopa.interop.authorization.service.DPoPTokenService;

import java.util.UUID;

public interface IdentityService {
    String getToken(String tenantType, String role);

    String getToken(String tenantType, String role, int userIndex);

    UUID getUserId(String tenantType, String role);

    UUID getUserId(String tenantType, String role, int userIndex);

    UUID getOrganizationId(String tenantType);

    String getTenantName(String tenantType);

    String getTenant(UUID organizationId);

    String getKind(String tenantType);

    DPoPTokenService.PreparedClient getPreparedClient(UUID clientId);
}
