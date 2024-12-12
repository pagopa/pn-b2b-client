package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.Ente;
import it.pagopa.interop.authorization.service.factory.SessionTokenFactory;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CommonUtils {
    private final SessionTokenFactory sessionTokenFactory;
    private final KeyPairGeneratorUtil keyPairGeneratorUtil;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final List<Ente> configFile;

    private Map<String, Map<String, String>> cachedTokens = null;
    @Getter @Setter
    private String userToken;
    @Getter @Setter
    private String tenantType;

    public CommonUtils(SessionTokenFactory sessionTokenFactory,
                       KeyPairGeneratorUtil keyPairGeneratorUtil,
                       ClientTokenConfigurator clientTokenConfigurator) {
        this.sessionTokenFactory = sessionTokenFactory;
        this.keyPairGeneratorUtil = keyPairGeneratorUtil;
        this.clientTokenConfigurator = clientTokenConfigurator;
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
                .filter(ente -> tenantType.equals(ente.getName()))
                .map(Ente::getUserRoles)
                .map(userRole -> userRole.get(role))
                .findFirst()
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("TenantID or Role not defined in the config file!"));
    }

    public UUID getOrganizationId(String tenantType) {
        return configFile.stream()
                .filter(ente -> tenantType.equals(ente.getName()))
                .map(ente -> ente.getOrganizationId())
                .map(o -> o.get("dev"))
                .findAny()
                .map(UUID::fromString)
                .orElse(null);
    }

    public <T> void makePolling(Supplier<T> promise, Predicate<T> shouldStop, String errorMessage) {
        try {
            for (int i = 0; i < 30; i++) {
                Thread.sleep(30);

                // Esegui la funzione fornita e ottieni il risultato
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


    private List<Ente> readProperty() {
        InputStream inputStream = null;
        List<Ente> enteList = new ArrayList<>();
        try {
            inputStream = new FileInputStream(new File("config/token-2.yaml"));
            Yaml yaml = new Yaml(new Constructor(Ente.class));
            yaml.loadAll(inputStream).forEach(i -> enteList.add((Ente) i));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return enteList;
    }


}
