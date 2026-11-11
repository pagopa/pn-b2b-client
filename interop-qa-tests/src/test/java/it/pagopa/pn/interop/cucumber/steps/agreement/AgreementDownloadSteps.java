package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class AgreementDownloadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementDownloadSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("l'attestazione di quella richiesta di fruizione è già stata generata")
    public void areementContractIsAlreadyAvailable() {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getAgreementClient()
                        .getAgreementById(sharedStepsContext.getAgreementCommonContext().getAgreementId()),
                Agreement::getIsContractPresent,
                "The agreement contract was not found!"
        );
    }

    @When("l'utente richiede una operazione di download dell'attestazione della richiesta di fruizione")
    public void getAgreementContract() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .getAgreementContract(sharedStepsContext.getAgreementCommonContext().getAgreementId())
        );
    }
}
