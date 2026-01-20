package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

@Data
public class ExecutionStatusResponse {
    private String executionArn;
    private String status;
    private String startDate;
    private String stopDate;
    private String error;
    private String cause;
}

