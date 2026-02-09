package it.pagopa.pn.interop.cucumber.steps.m2m.client.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ClientResolver extends AbstractResolver {

    private final SharedStepsContext sharedStepsContext;

    public UUID resolveClientId(String raw) {
        return resolveOrParse(
                raw,
                StepParser::uuidOrRandomOrNull,
                () -> sharedStepsContext.getClientCommonContext().getLastClient(),
                () -> sharedStepsContext.getClientCommonContext().getLastClient(),
                UUID::randomUUID,
                null
        );
    }

    public UUID resolveEserviceId(String raw) {
        return resolveOrParse(
                raw,
                StepParser::uuidOrRandomOrNull,
                () -> sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                () -> sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                UUID::randomUUID,
                null
        );
    }
}
