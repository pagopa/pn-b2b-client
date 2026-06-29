package it.pagopa.interop.authorization.service.identity;

import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.kms.model.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ToString
@EqualsAndHashCode
public class IdentityServiceSelfcareImpl implements IdentityService {
    private final SessionTokenFactory sessionTokenFactory;
    private final List<Tenant> tenantList;

    @Value("${spring.profiles.active}")
    private String runProfile;

    @Value("${session.tokens.duration.seconds}")
    private int sessionTokenDurationSeconds;

    public IdentityServiceSelfcareImpl(SessionTokenFactory sessionTokenFactory,
                                       ConfigFileReader configFileReader) {
        this.sessionTokenFactory = sessionTokenFactory;
        this.tenantList = configFileReader.getTenantList();
    }

    @Override
    public String getToken(String tenantType, String role) {
        return getToken(tenantType, role, 0);
    }

    @Override
    public String getToken(String tenantType, String role, int userIndex) {
        return Optional.ofNullable(sessionTokenFactory.loadToken())
                .map(m -> m.get(tenantType))
                .map(m -> (role == null) ? m.get("admin") : m.get(role))
                .map(m -> m.get(userIndex))
                .filter(Objects::nonNull)
                .orElseThrow(() -> new IllegalArgumentException("Token not found for tenant: " + tenantType + " and role: " + role));
    }

    @Override
    public String getMaintenanceToken() {
        try {
            return sessionTokenFactory.getMaintenanceToken();

        } catch (NotFoundException e) {
            log.warn(e.getMessage());
            log.warn("Maintenance token skipped");
            return "";

        } catch (Exception e) {
            throw new RuntimeException("Errore durante il reperimento del token di maintenance", e);
        }
    }

    @Override
    public UUID getUserId(String tenantType, String role) {
        return getUserId(tenantType, role, 0);
    }

    @Override
    public UUID getUserId(String tenantType, String role, int userIndex) {
        return tenantList.stream()
                .filter(tenant -> tenantType.equals(tenant.getName()))
                .map(Tenant::getUserRoles)
                .map(userRole -> userRole.get(role))
                .map(user -> user.get(userIndex))
                .findFirst()
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("TenantID or Role not defined in the config file!"));
    }

    @Override
    public UUID getOrganizationId(String tenantType) {
        return tenantList.stream()
                .filter(tenant -> tenantType.equals(tenant.getName()))
                .map(Tenant::getOrganizationId)
                .map(o -> o.get(this.runProfile))
                .findAny()
                .map(UUID::fromString)
                .orElse(null);
    }

    @Override
    public String getTenantName(String tenantType) {
        return tenantList.stream()
                .filter(tenant -> tenantType.equals(tenant.getName()))
                .map(Tenant::getTenantName)
                .map(t -> t.get(this.runProfile))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tenant name not found"));
    }

    @Override
    public String getTenant(UUID organizationId) {
        for (Tenant tenant : tenantList) {
            if (tenant.getOrganizationId().containsValue(organizationId.toString())) {
                return tenant.getName();
            }
        }

        throw new IllegalArgumentException("Organization id not found");
    }

    @Override
    public String getKind(String tenantType) {
        return tenantList.stream()
                .filter(tenant -> tenantType.equals(tenant.getName()))
                .map(Tenant::getKind)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Kind of tenant '%s' not found".formatted(tenantType)));
    }

    @Override
    public List<String> getTenantTypesByKind(String tenantKind) {
        return tenantList.stream()
                .filter(tenant -> tenantKind.equals(tenant.getKind()))
                .map(Tenant::getName)
                .toList();
    }

    @Override
    public DPoPTokenService.PreparedClient getPreparedClient(UUID clientId) {
        throw new RuntimeException("Not implemented yet");
    }

}
