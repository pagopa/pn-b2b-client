package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;

public class ClientPurposeAddSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    public ClientPurposeAddSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente associa la finalità al client con successo")
    public void userSuccessfullyRetrievesFinalization() {
        userRetrievesFinalization();
        if(httpCallExecutor.getClientResponse().isError()) {
            throw new IllegalStateException("Errore manifestatosi durante l'associazione della finalità al client. Consultare logs per maggior dettagli.");
        }
    }

    @When("l'utente richiede l'associazione della finalità al client")
    public void userRetrievesFinalization() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        userRetrievesFinalization(clientId);
    }

    @When("l'utente richiede l'associazione della finalità a un client inesistente")
    public void userRetrievesFinalizationForNonExistentClient() {
        UUID clientId = UUID.randomUUID();
        userRetrievesFinalization(clientId);
    }

    private void userRetrievesFinalization(UUID clientId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.addClientPurpose(
                    clientId,
                    new PurposeAdditionDetailsSeed().purposeId(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())))
        );
    }
}
