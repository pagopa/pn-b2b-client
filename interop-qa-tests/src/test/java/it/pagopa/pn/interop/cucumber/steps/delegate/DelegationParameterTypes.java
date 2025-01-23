package it.pagopa.pn.interop.cucumber.steps.delegate;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATING;

import io.cucumber.java.ParameterType;

public class DelegationParameterTypes {

    @ParameterType("delegato|delegante")
    public DelegationRole delegationRole(String delegationRole) {
        return switch (delegationRole) {
            case "delegato" -> DELEGATE;
            case "delegante" -> DELEGATING;
            default ->
                throw new IllegalArgumentException("Invalid delegation role: " + delegationRole);
        };
    }
}
