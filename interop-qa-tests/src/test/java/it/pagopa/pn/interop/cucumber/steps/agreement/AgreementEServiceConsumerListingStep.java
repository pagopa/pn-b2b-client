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

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione")
    public void requireEserviceListingOperation() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(0, 50, String.valueOf(sharedStepsContext.getTestSeed()))
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione limitata a {int}")
    public void requireEserviceListingOperationWithLimit(int limit) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(0, limit, String.valueOf(sharedStepsContext.getTestSeed()))
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione con offset {int}")
    public void requireEserviceListingOperationWithOffset(int offset) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(offset, 50, String.valueOf(sharedStepsContext.getTestSeed()))
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services per cui ha una richiesta di fruizione con keyword {string}")
    public void requireEserviceListingOperationWithKeyword(String keyword) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceConsumers(0, 50,
                        String.format("e-service-%s-%s", sharedStepsContext.getTestSeed(), keyword))
        );
    }
}
