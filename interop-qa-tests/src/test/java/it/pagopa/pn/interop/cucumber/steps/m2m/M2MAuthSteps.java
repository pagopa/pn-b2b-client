package it.pagopa.pn.interop.cucumber.steps.m2m;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.JWTUtils;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;

import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class M2MAuthSteps {

    public static final String INVALID_AUTH_TOKEN = "c29tZQ==.aW52YWxpZA==.dG9rZW4=";

    @ParameterType("m2m|m2m-admin")
    public static M2MRole m2mRole(String m2mRole) {
        return M2MRole.fromValue(m2mRole.toUpperCase());
    }

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public M2MAuthSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @Given("l'utente è un {m2mRole} dell'ente {delegationRole}")
    public void authenticateM2MDelegationUser(M2MRole m2MRole, DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        authenticateM2MUser("admin", tenantType, m2MRole);
    }

    @Given("l'utente è un {string} di {string} con ruolo M2M {m2mRole}")
    public void authenticateM2MUser(String selfcareRole, String tenant, M2MRole m2MRole) {
        String token = identityService.getToken(tenant, m2MRole.toString());
        UUID clientId = getClientId(token);

        DPoPTokenService.PreparedClient preparedClient = identityService.getPreparedClient(clientId);
        sharedStepsContext.getClientCommonContext().addClient(preparedClient);

        clientTokenConfigurator.setBearerToken(token);

        // Dpop Auth
        Auth auth = Auth.of(clientId.toString(), tenant, selfcareRole.toUpperCase(), preparedClient.keyPair().getKeyPair());
        clientTokenConfigurator.setAuth(auth);

        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setRole(Role.fromValue(selfcareRole.toUpperCase()));
        sharedStepsContext.setTenantType(tenant);

        sharedStepsContext.getClientCommonContext().addClient(clientId);
    }

    @Given("viene impostato per l'utente un token m2m non valido")
    @Given("viene impostato per l'utente un token non valido")
    public void setExpiredM2MAuth() {
        clientTokenConfigurator.setBearerToken(INVALID_AUTH_TOKEN);
        sharedStepsContext.setUserToken(INVALID_AUTH_TOKEN);
    }

    @Deprecated(forRemoval = true)
    @Given("l'utente è un {string} di {string} e predispone le credenziali per il ruolo M2M {m2mRole}")
    public void prepareM2MUser(String selfcareRole, String tenant, M2MRole m2MRole) {
        String token = identityService.getToken(tenant, m2MRole.toString());
        UUID clientId = getClientId(token);
        sharedStepsContext.getClientCommonContext().addClient(clientId);

        token = identityService.getToken(tenant, selfcareRole);
        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setRole(Role.fromValue(selfcareRole.toUpperCase()));
        sharedStepsContext.setTenantType(tenant);
    }

    private static UUID getClientId(String token) {
        Map<String, Object> jwtPayload = JWTUtils.decodeJwtPayload(token);
        String clientIdField = "client_id";
        Object oClientId = jwtPayload.get(clientIdField);
        requireNonNull(oClientId, "Not found expected field %s in token payload".formatted(clientIdField));
        return UUID.fromString(oClientId.toString());
    }

}
