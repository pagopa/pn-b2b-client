package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.util.Map;

@Data
public class SecondStepFunctionResponseWrapper {

    private int statusCode;
    private String body;

    @Data
    public static class Payload {
        private String message;
        private String executionArn;
        private String startDate;
    }
}

