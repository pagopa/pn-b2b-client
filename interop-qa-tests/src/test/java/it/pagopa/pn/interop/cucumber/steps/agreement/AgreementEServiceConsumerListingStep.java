package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class AgreementEServiceConsumerListingStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementEServiceConsumerListingStep(ClientTokenConfigurator clientTokenConfigurator,
                                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    //TODO da rivedere se nel campo keyword bisogna passare anche xCorrelationID

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione")
    public void requireEserviceListingOperation() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(
                        sharedStepsContext.getXCorrelationId(), 0, 50, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione limitata a {int}")
    public void requireEserviceListingOperationWithLimit(int limit) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(
                        sharedStepsContext.getXCorrelationId(), 0, limit, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione con offset {int}")
    public void requireEserviceListingOperationWithOffset(int offset) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(
                        sharedStepsContext.getXCorrelationId(), offset, 50, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione con keyword {string}")
    public void requireEserviceListingOperationWithKeyword(String keyword) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(
                        sharedStepsContext.getXCorrelationId(), 0, 50, keyword)
        );
    }
}
