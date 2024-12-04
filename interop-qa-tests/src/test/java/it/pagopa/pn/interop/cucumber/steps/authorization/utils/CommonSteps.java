package it.pagopa.pn.interop.cucumber.steps.authorization.utils;

import it.pagopa.pn.interop.cucumber.steps.authorization.factory.SessionTokenFactory;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CommonSteps {
    private static Map<String, Map<String, String>> cachedTokens = null;
    private final SessionTokenFactory sessionTokenFactory;

    public CommonSteps(SessionTokenFactory sessionTokenFactory) {
        this.sessionTokenFactory = sessionTokenFactory;
    }

    public String getToken(String tenantType, String role) {
        try {
            if (cachedTokens == null) {
                cachedTokens = sessionTokenFactory.generateSessionToken();
            }
        } catch (Exception ex) {
            throw new AssertionFailedError("There was an error while creating the session token: " + ex);
        }
        Map<String, String> tenantTokens = cachedTokens.get(tenantType);

        String token = Optional.ofNullable(cachedTokens)
                .map(m -> m.get(tenantType))
                .filter(Objects::nonNull)
                .map(m -> m.get(role))
                .filter(Objects::nonNull)
                .orElse(null);

        if (token == null) {
            throw new IllegalArgumentException("Token not found for tenant: " + tenantType + " and role: " + role);
        }
        return token;
    }

}
