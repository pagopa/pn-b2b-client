package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import it.pagopa.pn.interop.cucumber.steps.m2m.M2MAuthSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

@Slf4j
public class ClientCreateStep {
    private static final Random RANDOM = new Random();
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClientCreate;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final ApiProfile apiProfile;

    @Autowired
    public ClientCreateStep(ClientTokenConfigurator clientTokenConfigurator,
                            SharedStepsContext sharedStepsContext,
                            ApiProfile apiProfile) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClientCreate = clientTokenConfigurator.getAuthorizationClient();
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.sharedStepsContext = sharedStepsContext;
        this.apiProfile = apiProfile;
    }

    @Given("l'utente è un {string} di {string}")
    public void setRole(String role, String tenantType) {
        doSetRole(1, role, tenantType, clientTokenConfigurator, identityService, sharedStepsContext);
        sharedStepsContext.setTenantType(tenantType);
    }

    @Given("l'utente è un amministratore del profilo API {apiSet} dell'ente {string}")
    public void setRole(ApiProfile.ApiSet apiSet, String tenant) {
        setProfileRole(apiSet == ApiProfile.ApiSet.M2M ? "m2m-admin" : "admin", apiSet, tenant);
    }

    @Given("l'utente ha ruolo {string} nel profilo API {apiSet} dell'ente {string}")
    public void setProfileRole(String role, ApiProfile.ApiSet apiSet, String tenant) {
        validateProfileRole(role, apiSet);
        if (apiSet == ApiProfile.ApiSet.BFF) {
            doSetRole(1, role, tenant, clientTokenConfigurator, identityService, sharedStepsContext);
        } else {
            M2MAuthSteps.doAuthenticateM2MUser("admin", tenant, M2MAuthSteps.m2mRole(role),
                    clientTokenConfigurator, sharedStepsContext, identityService, apiProfile);
        }
    }

    static void validateProfileRole(String role, ApiProfile.ApiSet apiSet) {
        if (apiSet == null || role == null) {
            throw new IllegalArgumentException("Ruolo e profilo API devono essere valorizzati");
        }
        boolean m2mRole = "m2m".equalsIgnoreCase(role) || "m2m-admin".equalsIgnoreCase(role);
        if (m2mRole != (apiSet == ApiProfile.ApiSet.M2M)) {
            throw new IllegalArgumentException("Ruolo " + role + " non compatibile con il profilo API " + apiSet);
        }
        if (apiSet == ApiProfile.ApiSet.BFF) {
            // Validazione sintattica prima di acquisire token o modificare il contesto.
            Role.fromValue(role.toUpperCase(java.util.Locale.ROOT));
        }
    }

    @Given("l'utente è il numero {int} ad avere ruolo {string} di {string}")
    public void setRole(int userIndex, String role, String tenantType) {
        doSetRole(userIndex, role, tenantType, clientTokenConfigurator, identityService, sharedStepsContext);
    }

    public static void doSetRole(int userIndex, String role, String tenantType,
                                 ClientTokenConfigurator clientTokenConfigurator,
                                 IdentityService identityService,
                                 SharedStepsContext sharedStepsContext) {
        // La numerazione di userIndex parte da 1 dal pdv del chiamante
        String token = identityService.getToken(tenantType, role, --userIndex);

        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setRole(Role.fromValue(role.toUpperCase()));
        sharedStepsContext.setTenantType(tenantType);
        sharedStepsContext.setApiSet(ApiProfile.ApiSet.BFF);
    }

    // Di esclusiva utilità per test e debug locali
    @And("stampa token di autenticazione")
    public void printAuthToken() {
        System.out.println("Token di autenticazione attuale: " + clientTokenConfigurator.getLastToken());
    }

    @Given("l'utente è un {string} dell'ente {delegationRole}")
    public void setRole(String role, DelegationRole delegationRole) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        doSetRole(1, role, tenant, clientTokenConfigurator, identityService, sharedStepsContext);
    }

    @When("l'utente richiede la creazione di un client {string}")
    public void createClient(String clientKind) {
        if ("CONSUMER".equals(clientKind)) {
            httpCallExecutor.performCall(() -> authorizationClientCreate.createConsumerClient(createClientSeed()));
        } else {
            httpCallExecutor.performCall(() -> authorizationClientCreate.createApiClient(createClientSeed()));
        }
    }

    private ClientSeed createClientSeed() {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(String.format("client %d", RANDOM.nextInt(1000)));
        clientSeed.setDescription("Descrizione client");
        clientSeed.setMembers(List.of());
        return clientSeed;
    }
}
