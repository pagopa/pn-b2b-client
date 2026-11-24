package it.pagopa.interop.authorization.service.identity;

import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.M2MTokenService;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.authorization.enums.M2MRole.fromValue;
import static it.pagopa.interop.authorization.enums.M2MRole.isM2MRole;

/* 29/05/2025 classe orchestratrice: se il token è di tipo m2m ripiega sul relativo servizio,
 * altrimenti segue il flusso di auth usuale */
@Slf4j
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class IdentityServiceInteropImpl implements IdentityService {
    private final IdentityService defaultIdentityService;
    private final M2MTokenService m2mService;

    @Override
    public String getToken(String tenantType, String role) {
        return this.getToken(tenantType, role, 0);
    }

    @Override
    public String getToken(String tenantType, String userRole, int userIndex) {
        if (isM2MRole(userRole)) {
            M2MRole m2mRole = fromValue(userRole);
            return this.m2mService.getToken(tenantType, m2mRole, userIndex);
        } else {
            return this.defaultIdentityService.getToken(tenantType, userRole, userIndex);
        }
    }

    @Override
    public String getMaintenanceToken() {
        return defaultIdentityService.getMaintenanceToken();
    }

    @Override
    public UUID getUserId(String tenantType, String role) {
        return defaultIdentityService.getUserId(tenantType, role);
    }

    @Override
    public UUID getUserId(String tenantType, String role, int userIndex) {
        return defaultIdentityService.getUserId(tenantType, role, userIndex);
    }

    @Override
    public List<UUID> getUserIds(String tenantType, String role) {
        return defaultIdentityService.getUserIds(tenantType, role);
    }

    @Override
    public UUID getOrganizationId(String tenantType) {
        return defaultIdentityService.getOrganizationId(tenantType);
    }

    @Override
    public String getTenantName(String tenantType) {
        return defaultIdentityService.getTenantName(tenantType);
    }

    @Override
    public String getTenant(UUID organizationId) {
        return defaultIdentityService.getTenant(organizationId);
    }

    @Override
    public String getKind(String tenantType) {
        return defaultIdentityService.getKind(tenantType);
    }

    @Override
    public List<String> getTenantTypesByKind(String tenantKind) {
        return defaultIdentityService.getTenantTypesByKind(tenantKind);
    }

    @Override
    public DPoPTokenService.PreparedClient getPreparedClient(UUID clientId) {
        return m2mService.getPreparedClient(clientId);
    }

}
