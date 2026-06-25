package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class ClientPurposeAddSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final SharedStepsContext sharedStepsContext;

    public ClientPurposeAddSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente associa la finalità al client con successo")
    public void userSuccessfullyRetrievesFinalization() {
        userRetrievesFinalization();
        if (httpCallExecutor.getResponseStatus().isError()) {
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

    @And("l'associazione tra finalita e client è presente")
    public void checkPurposeClientAssociationSuccessfullyCreated() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        pollingService.makePolling(
                () -> authorizationClient.getClient(clientId),
                client -> client.getPurposes().stream().anyMatch(purpose -> purpose.getPurposeId().equals(purposeId)),
                "La finalita non risulta associata al client come previsto."
        );
    }

    @And("l'associazione tra finalita e client non è presente")
    public void checkPurposeClientAssociationNotPresent() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        pollingService.makePolling(
                () -> authorizationClient.getClient(clientId),
                client -> client.getPurposes().stream().noneMatch(purpose -> purpose.getPurposeId().equals(purposeId)),
                "La finalita risulta ancora associata al client, ma non dovrebbe esserlo."
        );
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
