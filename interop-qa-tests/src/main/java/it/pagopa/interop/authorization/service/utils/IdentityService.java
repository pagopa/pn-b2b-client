package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IdentityService {
    private final SessionTokenFactory sessionTokenFactory;
    private final List<Tenant> tenantList;

    public IdentityService(SessionTokenFactory sessionTokenFactory,
                           ConfigFileReader configFileReader) {
        this.sessionTokenFactory = sessionTokenFactory;
        this.tenantList = configFileReader.getTenantList();
    }

    public String getToken(String tenantType, String role) {
        return Optional.ofNullable(sessionTokenFactory.getCachedTokens())
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
