package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.util.Map;

@Data
public class SecondStepFunctionResponseWrapper {

    private int statusCode;
    private DelayerPayload body;

}

