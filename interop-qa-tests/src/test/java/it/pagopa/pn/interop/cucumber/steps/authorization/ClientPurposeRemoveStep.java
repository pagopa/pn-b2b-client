package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;

import java.util.UUID;

public class ClientPurposeRemoveStep {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;

    public ClientPurposeRemoveStep(ClientTokenConfigurator clientTokenConfigurator,
                                   SharedStepsContext sharedStepsContext,
                                   BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @And("{string} associa la finalità al client creato con successo")
    @Given("{string} ha già associato la finalità a quel client")
    public void addPurposeToClient(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        httpCallExecutor
                .performCall(() -> dataPreparationService.addPurposeToClient(sharedStepsContext.getClientCommonContext().getFirstClient(),
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())));
    }

    @Given("{string} ha già archiviato quella finalità")
    public void archivePurpose(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        PurposeCommonContext purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
        httpCallExecutor.performCall(() -> dataPreparationService.archivePurpose(UUID.fromString(purposeCommonContext.getPurposeId()),
                UUID.fromString(purposeCommonContext.getVersionId())));
    }

    @When("l'utente richiede la disassociazione della finalità dal client")
    public void removePurposeFromClient() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.removeClientPurpose(
                sharedStepsContext.getClientCommonContext().getFirstClient(), UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())));
    }

    @When("l'utente {string} di {string} richiede la disassociazione della finalità dal client")
    public void removePurposeFromClient(String role, String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        httpCallExecutor.performCall(() -> authorizationClient.removeClientPurpose(
                sharedStepsContext.getClientCommonContext().getFirstClient(),
                UUID.fromString(sharedStepsContext.getPurposeId())
        ));
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }


    @When("l'utente {string} di {string} richiede la disassociazione della finalità dal client con successo")
    public void successfullyRemovePurposeFromClient(String role, String tenant) {
        removePurposeFromClient(role, tenant);

        if (httpCallExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException("La disassociazione della finalità dal client non ha avuto successo");
        }
    }

}
