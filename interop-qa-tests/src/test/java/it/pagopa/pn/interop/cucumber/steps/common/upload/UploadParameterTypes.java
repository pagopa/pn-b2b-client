package it.pagopa.pn.interop.cucumber.steps.common.upload;

import io.cucumber.java.ParameterType;

public class UploadParameterTypes {

    @ParameterType("positivo|negativo")
    public ExpectedOutcome expectedOutcome(String value) {
        return ExpectedOutcome.fromItalian(value);
    }

}

