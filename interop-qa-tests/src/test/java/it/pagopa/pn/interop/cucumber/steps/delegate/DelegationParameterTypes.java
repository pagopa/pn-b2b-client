package it.pagopa.pn.interop.cucumber.steps.delegate;

import static it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.AUTOMATIC;
import static it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.MANUAL;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATING;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;

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

    @ParameterType("manuale|automatico")
    public AgreementApprovalPolicy agreementApprovalPolicy(String agreementApprovalPolicy) {
        return switch (agreementApprovalPolicy) {
            case "manuale" -> MANUAL;
            case "automatico" -> AUTOMATIC;
            default ->
                    throw new IllegalArgumentException("Invalid approve policy: " + agreementApprovalPolicy);
        };
    }
}
