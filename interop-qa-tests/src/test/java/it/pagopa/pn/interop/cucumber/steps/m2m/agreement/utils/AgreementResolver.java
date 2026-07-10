package it.pagopa.pn.interop.cucumber.steps.m2m.agreement.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;

import java.util.UUID;

public class AgreementResolver extends AbstractResolver {

    public AgreementResolver(SharedStepsContext sharedStepsContext) {
        super(sharedStepsContext);
    }

    public UUID resolveAgreementId(String raw) {
        return resolveOrParse(
                raw,
                UUID::fromString,
                sharedStepsContext::getAgreementId,
                sharedStepsContext::getAgreementId,
                UUID::randomUUID,
                null
        );
    }

    public UUID resolveDelegationId(String raw) {
        return resolveOrParse(
                raw,
                UUID::fromString,
                () -> sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                () -> sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                UUID::randomUUID,
                null
        );
    }
}
