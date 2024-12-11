package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.purpose.PurposeCommonStep;
import it.pagopa.pn.interop.cucumber.steps.purpose.domain.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientPurposeRemoveStep {

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final PurposeCommonContext purposeCommonContext;
    private final DataPreparationService dataPreparationService;
    private final HttpCallExecutor httpCallExecutor;

    public ClientPurposeRemoveStep(IAuthorizationClient authorizationClient,
            ClientCommonSteps clientCommonSteps,
            PurposeCommonContext purposeCommonContext,
            DataPreparationService dataPreparationService,
            HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.purposeCommonContext = purposeCommonContext;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("{string} ha già associato la finalità a quel client")
    public void addPurposeToClient(String tenantType) {
        httpCallExecutor
                .performCall(() -> dataPreparationService.addPurposeToClient(clientCommonSteps.getClients().get(0),
                        clientCommonSteps.getPurposeId().getPurposeId()));
    }

    @Given("{string} ha già archiviato quella finalità")
    public void archivePurpose(String tenantType) {
        httpCallExecutor
                .performCall(() -> dataPreparationService.archivePurpose(clientCommonSteps.getClients().get(0),
                        purposeCommonContext.getVersionId()));
    }

    @When("l'utente richiede la disassociazione della finalità dal client")
    public void getClientUsers() {
        httpCallExecutor.performCall(() -> authorizationClient.removeClientPurpose("",
                clientCommonSteps.getClients().get(0), clientCommonSteps.getPurposeId().getPurposeId()));
    }

}
