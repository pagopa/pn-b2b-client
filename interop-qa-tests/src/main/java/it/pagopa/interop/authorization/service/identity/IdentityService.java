package it.pagopa.interop.authorization.service.identity;

import it.pagopa.interop.authorization.service.DPoPTokenService;

import java.util.List;
import java.util.UUID;

public interface IdentityService {
    String getToken(String tenantType, String role);

    String getToken(String tenantType, String role, int userIndex);

    String getMaintenanceToken();

    UUID getUserId(String tenantType, String role);

    UUID getUserId(String tenantType, String role, int userIndex);

    List<UUID> getUserIds(String tenantType, String role);

    UUID getOrganizationId(String tenantType);

    String getTenantName(String tenantType);

    String getTenant(UUID organizationId);

    String getKind(String tenantType);

    List<String> getTenantTypesByKind(String tenantKind);

    DPoPTokenService.PreparedClient getPreparedClient(UUID clientId);
}
