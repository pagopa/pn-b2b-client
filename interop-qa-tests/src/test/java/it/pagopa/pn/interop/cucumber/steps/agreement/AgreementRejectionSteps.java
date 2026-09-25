package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class AgreementRejectionSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementRejectionSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede una operazione di rifiuto di quella richiesta di fruizione con messaggio")
    public void rejectAgreementWithMessage() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().rejectAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(),
                        new AgreementRejectionPayload().reason("rejection reason: qa-testing"))
        );
    }

    @When("l'utente richiede una operazione di rifiuto di quella richiesta di fruizione senza messaggio")
    public void rejectAgreementWithoutMessage() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().rejectAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(),
                        new AgreementRejectionPayload().reason(""))
        );
    }
}
