package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdentityService {
    private final SessionTokenFactory sessionTokenFactory;
    private final List<Tenant> tenantList;

    public IdentityService(SessionTokenFactory sessionTokenFactory,
                           ConfigFileReader configFileReader) {
        this.sessionTokenFactory = sessionTokenFactory;
        this.tenantList = configFileReader.getTenantList();
    }

    public String getToken(String tenantType, String role) {
        return Optional.ofNullable(sessionTokenFactory.loadToken())
                .map(m -> m.get(tenantType))
                .map(m -> (role == null) ? m.get("admin") : m.get(role))
                .filter(Objects::nonNull)
                .orElseThrow(() -> new IllegalArgumentException("Token not found for tenant: " + tenantType + " and role: " + role));
    }

    public UUID getUserId(String tenantType, String role) {
        return tenantList.stream()
                .filter(tenant -> tenantType.equals(tenant.getName()))
                .map(Tenant::getUserRoles)
                .map(userRole -> userRole.get(role))
                .findFirst()
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("TenantID or Role not defined in the config file!"));
    }

    public UUID getOrganizationId(String tenantType) {
        return tenantList.stream()
                .filter(tenant -> tenantType.equals(tenant.getName()))
                .map(Tenant::getOrganizationId)
                .map(o -> o.get("dev"))
                .findAny()
                .map(UUID::fromString)
                .orElse(null);
    }

}
