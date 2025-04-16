package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class AgreementEServiceProducerListingStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementEServiceProducerListingStep(ClientTokenConfigurator clientTokenConfigurator,
                                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva")
    public void requireEServiceListingOperation() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(
                        sharedStepsContext.getXCorrelationId(), 0, 50, null, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva limitata ai primi {int} e-services")
    public void requireEServiceListingOperationWithLimit(int limit) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(
                        sharedStepsContext.getXCorrelationId(), 0, limit, null, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva con offset {int}")
    public void requireEServiceListingOperationWithOffset(int offset) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(
                        sharedStepsContext.getXCorrelationId(), offset, 10, null, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services che hanno una richiesta di fruizione attiva filtrando per la keyword {string}")
    public void requireEServiceListingOperationWithOffset(String keyword) {
        String query = String.format("%s-%s", sharedStepsContext.getXCorrelationId(), keyword);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementEServiceProducers(
                        sharedStepsContext.getXCorrelationId(), 0, 10, query, null)
        );
    }
}
