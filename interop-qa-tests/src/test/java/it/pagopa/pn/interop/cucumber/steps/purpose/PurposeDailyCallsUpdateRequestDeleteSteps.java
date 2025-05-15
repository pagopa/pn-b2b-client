package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class PurposeDailyCallsUpdateRequestDeleteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public PurposeDailyCallsUpdateRequestDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                     SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede la cancellazione della richiesta di aggiornamento della stima di carico")
    public void requireDeleteDailyCallsUpdate() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().deletePurposeVersion(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId())
                )
        );
    }

}
