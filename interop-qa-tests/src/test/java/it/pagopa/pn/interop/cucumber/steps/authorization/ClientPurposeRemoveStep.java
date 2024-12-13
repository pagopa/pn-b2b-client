package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.purpose.domain.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

import java.util.UUID;

public class ClientPurposeRemoveStep {

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final PurposeCommonContext purposeCommonContext;
    private final DataPreparationService dataPreparationService;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientPurposeRemoveStep(IAuthorizationClient authorizationClient,
            ClientCommonSteps clientCommonSteps,
            PurposeCommonContext purposeCommonContext,
            DataPreparationService dataPreparationService,
            HttpCallExecutor httpCallExecutor,
            CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.purposeCommonContext = purposeCommonContext;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    @Given("{string} ha già associato la finalità a quel client")
    public void addPurposeToClient(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor
                .performCall(() -> dataPreparationService.addPurposeToClient(clientCommonSteps.getClients().get(0),
                        UUID.fromString(purposeCommonContext.getPurposeId())));
    }

    @Given("{string} ha già archiviato quella finalità")
    public void archivePurpose(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor.performCall(() -> dataPreparationService.archivePurpose(UUID.fromString(purposeCommonContext.getPurposeId()),
                        UUID.fromString(purposeCommonContext.getVersionId())));
    }

    @When("l'utente richiede la disassociazione della finalità dal client")
    public void getClientUsers() {
        commonUtils.setBearerToken(commonUtils.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.removeClientPurpose("",
                clientCommonSteps.getClients().get(0), UUID.fromString(purposeCommonContext.getPurposeId())));
    }

}
