package it.pagopa.pn.interop.cucumber.steps.m2m.client.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.BaseResolver;
import it.pagopa.pn.interop.cucumber.utility.StepParser;

import java.util.UUID;

public class ClientResolver extends BaseResolver {

    private final SharedStepsContext sharedStepsContext;

    public ClientResolver(SharedStepsContext sharedStepsContext) {
        super(sharedStepsContext);
        this.sharedStepsContext = sharedStepsContext;
    }

    public UUID resolveClientId(String raw) {
        return resolveOrParse(
                raw,
                StepParser::uuidOrRandomOrNull,
                () -> sharedStepsContext.getClientCommonContext().getFirstClient(),
                () -> sharedStepsContext.getClientCommonContext().getFirstClient(),
                UUID::randomUUID,
                null
        );
    }
}
