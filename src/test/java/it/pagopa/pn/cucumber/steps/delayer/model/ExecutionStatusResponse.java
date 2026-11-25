package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionStatusResponse {
    private String executionArn;
    private String status;
    private String startDate;
    private String stopDate;
    private String error;
    private String cause;
}

