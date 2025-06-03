package it.pagopa.pn.interop.cucumber.steps.m2m;

import static java.util.Objects.requireNonNull;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.service.M2MTokenService.M2MRole;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.JWTUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.Map;
import java.util.UUID;

public class M2MAuthSteps {
    @ParameterType("m2m|m2m-admin")
    public static M2MRole m2mRole(String m2mRole) {
        return M2MRole.fromValue(m2mRole.toUpperCase());
    }

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;

    public M2MAuthSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'utente è un {string} di {string} con ruolo M2M {m2mRole}")
    public void authenticateM2MUser(String selfcareRole, String tenant, M2MRole m2MRole) {
        String token = identityService.getToken(tenant, m2MRole.toString());

        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setRole(Role.fromValue(selfcareRole.toUpperCase()));
        sharedStepsContext.setTenantType(tenant);

        UUID clientId = getClientId(token);
        sharedStepsContext.getClientCommonContext().addClient(clientId);
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
        UUID clientId = UUID.fromString(oClientId.toString());
        return clientId;
    }

}
