package it.pagopa.pn.interop.cucumber.steps.m2m;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.M2MDPopTokenService;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.JWTUtils;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class M2MAuthSteps {
    @ParameterType("m2m|m2m-admin")
    public static M2MRole m2mRole(String m2mRole) {
        return M2MRole.fromValue(m2mRole.toUpperCase());
    }

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;

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
        UUID clientId = getClientId(token);

        M2MDPopTokenService.PreparedClient preparedClient = identityService.getPreparedClient(clientId);
        sharedStepsContext.getClientCommonContext().addClient(preparedClient);

        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setRole(Role.fromValue(selfcareRole.toUpperCase()));
        sharedStepsContext.setTenantType(tenant);
    }

    @Given("viene impostato per l'utente un token m2m scaduto")
    public void setExpiredM2MAuth() {
        String expiredToken = "eyJhbGciOiJSUzI1NiIsInVzZSI6InNpZyIsInR5cCI6ImF0K2p3dCIsImtpZCI6IjE3ZDNmM2MwLTU3MzAtNDVhOS1iZThhLTY1NWU3N2JmMzU1NSJ9.ewogICJqdGkiOiAiZmEyMTkyMDMtYTgxNy00MzZjLWExYTktZWI2ZWFjYjk0Y2RhIiwKICAiaXNzIjogInFhLmludGVyb3AucGFnb3BhLml0IiwKICAiYXVkIjogInFhLmludGVyb3AucGFnb3BhLml0L20ybSIsCiAgImNsaWVudF9pZCI6ICJlOGU0YjAwNC1jNDUwLTRjOTEtYWQ3Yy1mZDQyZWU5YTAwNTUiLAogICJzdWIiOiAiZThlNGIwMDQtYzQ1MC00YzkxLWFkN2MtZmQ0MmVlOWEwMDU1IiwKICAiaWF0IjogMTc0ODkzNzE0MiwKICAibmJmIjogMTc0ODkzNzE0MiwKICAiZXhwIjogMTc0ODkzNzE0MSwKICAib3JnYW5pemF0aW9uSWQiOiAiZTc5YTI0Y2QtOGVkYy00NDFlLWFlOGQtZTg3YzNhZWEwMDU5IiwKICAicm9sZSI6ICJtMm0tYWRtaW4iLAogICJhZG1pbklkIjogImYwN2RkYjhmLTE3ZjktNDdkNC1iMzFlLTM1ZDFhYzEwZTUyMSIKfQ.HfhAKKu06x7uCGAYl7M8Pbzm6EfQFiqgFlKD-0bqraz0UFCJKRi91rFiaurWiRA-4lQiX5S6apuKcSvOZ6_DYGQcwgkrIhRCQ-dtohxRR_zyR-mTImkEfJow-t3eAkuKMRN8jxeNl8eWJ-lWbTKNkIxlzmmSaueH-ga-uDC6hNj6hOP6WukFCIN5yq-Gthr_NZzMcHZdHaHCKxcpIbjmrRvJJYTfztQZgJqC_N6Uv3_fKUzdJtZLswjqr5vUW1_DOYhEez2Iv5tOycMKQLn9N0Q474lPJ3TAiAHpOFpbSKZhj_IwWKjV5z37Gc04H6-csLYJrumvDyz6H0hf_ofNNA";
        clientTokenConfigurator.setBearerToken(expiredToken);
        sharedStepsContext.setUserToken(expiredToken);
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
