package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommonUtils {
    private final SessionTokenFactory sessionTokenFactory;
    private final KeyPairGeneratorUtil keyPairGeneratorUtil;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final InteropClientConfigs interopClientConfigs;
    private final List<Tenant> configFile;

    private Map<String, Map<String, String>> cachedTokens = null;

    public CommonUtils(SessionTokenFactory sessionTokenFactory,
                       KeyPairGeneratorUtil keyPairGeneratorUtil,
                       ClientTokenConfigurator clientTokenConfigurator,
                       InteropClientConfigs interopClientConfigs) {
        this.sessionTokenFactory = sessionTokenFactory;
        this.keyPairGeneratorUtil = keyPairGeneratorUtil;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.interopClientConfigs = interopClientConfigs;
        this.configFile = readProperty();
    }

    public String getToken(String tenantType, String role) {
        try {
            if (cachedTokens == null) {
                cachedTokens = sessionTokenFactory.generateSessionToken(configFile);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("There was an error while creating the session token: " + ex.getMessage(), ex);
        }
        Map<String, String> tenantTokens = cachedTokens.get(tenantType);

        String token = Optional.ofNullable(cachedTokens)
                .map(m -> m.get(tenantType))
                .filter(Objects::nonNull)
                .map(m -> (role == null) ? m.get("admin") : m.get(role))
                .filter(Objects::nonNull)
                .orElse(null);

        if (token == null) {
            throw new IllegalArgumentException("Token not found for tenant: " + tenantType + " and role: " + role);
        }
        return token;
    }

    public void setBearerToken(String token) {
        clientTokenConfigurator.setBearerToken(token);
    }

    public UUID getUserId(String tenantType, String role) {
        return configFile.stream()
                .filter(tenant -> tenantType.equals(tenant.getName()))
                .map(Tenant::getUserRoles)
                .map(userRole -> userRole.get(role))
                .findFirst()
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("TenantID or Role not defined in the config file!"));
    }

    public UUID getOrganizationId(String tenantType) {
        return configFile.stream()
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

    private List<Tenant> readProperty() {
        InputStream inputStream = null;
        List<Tenant> tenantList = new ArrayList<>();
        try {
            inputStream = new FileInputStream(new File("config/tenants-ids.yaml"));
            Yaml yaml = new Yaml(new Constructor(Tenant.class));
            yaml.loadAll(inputStream).forEach(i -> tenantList.add((Tenant) i));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tenantList;
    }


}
