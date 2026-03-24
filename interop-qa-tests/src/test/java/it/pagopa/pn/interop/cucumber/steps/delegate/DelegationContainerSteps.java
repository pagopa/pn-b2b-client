package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DelegationContainerSteps {
    private final DelegationCommonStep delegationCommonStep;
    private final DelegationCreateStep delegationCreateStep;
    private final DelegationAcceptStep delegationAcceptStep;

    @Given("l'ente {string} ha una delega in erogazione attiva verso l'ente {string}")
    public void tenantHasAlreadyCreatedAndUpdatedEService(String delegatorTenant, String delegateTenant) {
        delegationCreateStep.tenantGrantsProducerDelegationAvailability(delegateTenant);
        delegationCreateStep.createDelegateSuccessfully(delegatorTenant, delegateTenant);
        delegationAcceptStep.producerDelegationIsAcceptedByTenant(delegateTenant);
        delegationCommonStep.tenantRemoveDelegationAvailability(delegateTenant);
    }
}
