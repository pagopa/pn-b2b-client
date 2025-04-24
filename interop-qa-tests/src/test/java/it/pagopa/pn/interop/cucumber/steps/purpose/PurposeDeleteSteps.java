package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class PurposeDeleteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public PurposeDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                     SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede la cancellazione della finalità")
    public void userRequirePurposeCancellation() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().deletePurpose(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())
                )
        );
    }
}
