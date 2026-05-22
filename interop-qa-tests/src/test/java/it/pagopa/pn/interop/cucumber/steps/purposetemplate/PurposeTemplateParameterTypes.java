package it.pagopa.pn.interop.cucumber.steps.purposetemplate;

import io.cucumber.java.ParameterType;

public class PurposeTemplateParameterTypes {

    @ParameterType("e-service concreto|e-service template")
    public String resourceKind(String value) {
        return value;
    }
}
