package it.pagopa.interop.authorization.service.identity;

import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.JWTUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@ToString
@EqualsAndHashCode
public class IdentityServiceSelfcareImpl implements IdentityService {
    private final SessionTokenFactory sessionTokenFactory;
    private final List<Tenant> tenantList;

    @Value("${spring.profiles.active}")
    private String runProfile;

    /* DEV. NOTE 26/05/2026: le logiche di costruzione dei token sono contenutie in SessionTokenFactory, tuttavia
    * la costruzione di un token di maintenance segue un iter leggermente diverso dal solito.
    * In futuro andrebbe verificato che non si possa portare anche questa responsabilità in SessionTokenFactory. */
    @Value("${pn.interop.maintenance.tokenTemplate}")
    private String maintenanceTokenTemplate;

    @Value("${session.tokens.duration.seconds}")
    private int sessionTokenDurationSeconds;

    private String lastMaintenanceToken;
    /* ***********************************************************************************************************
    **************************************************************************************************************
    **************************************************************************************************************/

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
        if(nonNull(lastMaintenanceToken) && isNotExpired(lastMaintenanceToken)) {
            return lastMaintenanceToken;
        }

        long exp = Instant.now().plusSeconds(this.sessionTokenDurationSeconds).getEpochSecond();
        JWTUtils.JWTPojo jwtPojo = JWTUtils.decodeJwt(this.maintenanceTokenTemplate);
        jwtPojo.getPayload().put("exp", exp);
        lastMaintenanceToken = JWTUtils.encodeJwt(jwtPojo);
        return lastMaintenanceToken;
    }

    private boolean isNotExpired(String token) {
        long now = Instant.now().getEpochSecond();
        Object oExp = Objects.requireNonNull(
            JWTUtils.decodeJwt(token).getPayload().get("exp"),
            "Il campo 'exp' non è presente nel token '%s'".formatted(token)
        );

        long exp;
        // DEV.NOTE al momento si usa Java 17, dunque pattern matching non supportato
        if (oExp instanceof Integer iExp) {
            exp = iExp.longValue();
        } else if (oExp instanceof Long lExp) {
            exp = lExp;
        } else {
            throw new IllegalArgumentException(
                    "Il campo 'exp' deve essere di tipo Integer o Long, trovato invece: " + oExp.getClass().getName()
            );
        }

        // Il token è considerato scaduto se: exp <= (adesso + 2 secondi di sicurezza)
        return exp > (now + 2);
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
    public DPoPTokenService.PreparedClient getPreparedClient(UUID clientId) {
        throw new RuntimeException("Not implemented yet");
    }

}
