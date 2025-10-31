package it.pagopa.pn.interop.cucumber.steps.m2m.delegate;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationState;

public class DelegationParameterTypes {
    @ParameterType("WAITING_FOR_APPROVAL|ACTIVE|REJECTED|REVOKED")
    public DelegationState delegationState(String delegationState) {
        return DelegationState.valueOf(delegationState);
    }
}
