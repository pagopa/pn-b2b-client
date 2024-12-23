package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class CommonUtils {
    private final SessionTokenFactory sessionTokenFactory;
    private final InteropClientConfigs interopClientConfigs;
    private final List<Tenant> tenantList;

    public CommonUtils(SessionTokenFactory sessionTokenFactory,
                       KeyPairGeneratorUtil keyPairGeneratorUtil,
                       InteropClientConfigs interopClientConfigs,
                       ConfigFileReader configFileReader) {
        this.sessionTokenFactory = sessionTokenFactory;
        this.interopClientConfigs = interopClientConfigs;
        this.tenantList = configFileReader.getTenantList();
    }

    public String getToken(String tenantType, String role) {
        String token = Optional.ofNullable(sessionTokenFactory.getCachedTokens())
                .map(m -> m.get(tenantType))
                .filter(Objects::nonNull)
                .map(m -> (role == null) ? m.get("admin") : m.get(role))
                .filter(Objects::nonNull)
                .orElseThrow(() -> new IllegalArgumentException("Token not found for tenant: " + tenantType + " and role: " + role));
        return token;
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
                .map(tenant -> tenant.getOrganizationId())
                .map(o -> o.get("dev"))
                .findAny()
                .map(UUID::fromString)
                .orElse(null);
    }

    public <T> void makePolling(Supplier<T> promise, Predicate<T> shouldStop, String errorMessage) {
        try {
            for (int i = 0; i < interopClientConfigs.getMaxPollingTry(); i++) {
                Thread.sleep(interopClientConfigs.getMaxPollingSleep());

                // Execute the provided function and obtain the result
                T response = promise.get();

                boolean shouldStopPolling = shouldStop.test(response);
                if (shouldStopPolling) {
                    return;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error during shouldStop polling logic evaluation: " + e.getMessage());
        }

        throw new IllegalArgumentException("Eventual consistency error: " + errorMessage);
    }

}
