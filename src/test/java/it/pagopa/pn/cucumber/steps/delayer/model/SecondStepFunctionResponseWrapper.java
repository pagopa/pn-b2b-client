package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecondStepFunctionResponseWrapper {

    private int statusCode;
    private DelayerPayload body;

}

