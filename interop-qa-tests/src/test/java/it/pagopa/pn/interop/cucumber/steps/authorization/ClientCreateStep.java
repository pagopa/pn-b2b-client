package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientCreateStep {
    private static final Random RANDOM = new Random();
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClientCreate;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    public ClientCreateStep(ClientTokenConfigurator clientTokenConfigurator,
                            SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClientCreate = clientTokenConfigurator.getAuthorizationClient();
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("l'utente è un {string} di {string}")
    public void setRole(String role, String tenantType) {
        setRole(1, role, tenantType);
        sharedStepsContext.setTenantType(tenantType);
    }

    @Given("l'utente è il numero {int} ad avere ruolo {string} di {string}")
    public void setRole(int userIndex, String role, String tenantType) {
        // La numerazione di userIndex parte da 1 dal pdv del chiamante
        String token = identityService.getToken(tenantType, role, --userIndex);

        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setRole(Role.fromValue(role.toUpperCase()));
        sharedStepsContext.setTenantType(tenantType);
    }

    // Di esclusiva utilità per test e debug locali
    @And("stampa token di autenticazione")
    public void printAuthToken() {
        System.out.println("Token di autenticazione attuale: " + clientTokenConfigurator.getLastToken());
    }

    @Given("l'utente è un {string} dell'ente {delegationRole}")
    public void setRole(String role, DelegationRole delegationRole) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        setRole(role, tenant);
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
