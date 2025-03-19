package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientCreateStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClientCreate;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
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
        String token = identityService.getToken(tenantType, role);
        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.setUserToken(token);
        sharedStepsContext.setTenantType(tenantType);
    }

    @Given("l'utente è un {string} dell'ente {delegationRole}")
    public void setRole(String role, DelegationRole delegationRole) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        setRole(role, tenant);
    }

    @When("l'utente richiede la creazione di un client {string}")
    public void createClient(String clientKind) {
        if ("CONSUMER".equals(clientKind)) {
            httpCallExecutor.performCall(() -> authorizationClientCreate.createConsumerClient(sharedStepsContext.getXCorrelationId(), createClientSeed()));
        } else {
            httpCallExecutor.performCall(() -> authorizationClientCreate.createApiClient(sharedStepsContext.getXCorrelationId(), createClientSeed()));
        }
    }

    private ClientSeed createClientSeed() {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(String.format("client %d", new Random().nextInt(1000)));
        clientSeed.setDescription("Descrizione client");
        clientSeed.setMembers(List.of());
        return clientSeed;
    }
}
