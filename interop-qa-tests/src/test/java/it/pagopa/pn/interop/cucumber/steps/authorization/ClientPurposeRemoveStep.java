package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class ClientPurposeRemoveStep {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final DataPreparationService dataPreparationService;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;

    public ClientPurposeRemoveStep(ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext,
            DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

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
    public void getClientUsers() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.removeClientPurpose(sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getClientCommonContext().getFirstClient(), UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())));
    }

}
