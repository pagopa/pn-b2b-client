package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;

public class AgreementSuspensionSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementSuspensionSteps(ClientTokenConfigurator clientTokenConfigurator,
                                    SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede una operazione di sospensione di quella richiesta di fruizione")
    public void requireSuspendAgreement() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        suspendAgreement(false);
    }

    @When("l'utente {string} di {string} richiede una operazione di sospensione di quella richiesta di fruizione con successo")
    public void successfullyRequireSuspendAgreement(String role, String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        suspendAgreement(false);
        if(sharedStepsContext.getHttpCallExecutor().getResponseStatus().isError()) {
            throw new IllegalStateException("La sospensione della richiesta di fruizione non è stata eseguita con successo");
        }
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'ente {delegationRole} richiede una operazione di sospensione di quella richiesta di fruizione")
    public void delegateRequireSuspendAgreement(DelegationRole delegationRole) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, null));
        suspendAgreement(true);
    }

    private void suspendAgreement(boolean isDelegate) {
        DelegationRef delegationRef = (isDelegate) ? new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId()) : null;
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().suspendAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), delegationRef)
        );

        if (sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getPollingService().makePolling(
                    () -> clientTokenConfigurator.getAgreementClient().getAgreementById(sharedStepsContext.getAgreementCommonContext().getAgreementId()),
                    res -> res.getState() == AgreementState.SUSPENDED,
                    "The agreement was not suspended!"
            );
        }
    }

}
