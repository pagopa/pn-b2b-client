package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;
import java.util.Map;

@Data
public class FirstStepFunctionResponseWrapper {

    private int statusCode;
    private String body;   // JSON string → livello successivo

    @Data
    public static class Inner {
        private int statusCode;
        private Map<String, String> headers;
        private String body; // JSON string → payload finale
    }

    @Data
    public static class Payload {
        private String message;
        private String executionArn;
        private String startDate;
    }
}

