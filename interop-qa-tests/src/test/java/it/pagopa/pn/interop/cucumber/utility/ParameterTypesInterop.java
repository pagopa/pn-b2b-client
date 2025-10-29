package it.pagopa.pn.interop.cucumber.utility;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateState;

public class ParameterTypesInterop {

    @ParameterType("contiene|non contiene")
    public static boolean contains(String value) {
        return value.equals("contiene");
    }

    @ParameterType("creato|esistente|inesistente")
    public static boolean exists(String value) {
        return !value.equals("inesistente");
    }

    @ParameterType("DRAFT|ACTIVE|SUSPENDED|ARCHIVED")
    public static PurposeTemplateState ptState(String state) {
        return PurposeTemplateState.fromValue(state);
    }
}
