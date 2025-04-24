package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class PurposeSuspendSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public PurposeSuspendSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                        SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente sospende quella finalità in stato {string}")
    public void userSuspendPurposeWithState(String state) {
        String versionId = ("WAITING_FOR_APPROVAL".equals(state) || "REJECTED".equals(state))
                ? sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId()
                : sharedStepsContext.getPurposeCommonContext().getVersionId();

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().suspendPurposeVersion(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        UUID.fromString(versionId)
                )
        );
    }
}
