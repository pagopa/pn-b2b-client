package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DelayerStatusExecution implements Serializable {

    private String executionArn;
    private String status;
    private String startDate;
    private String stopDate;
    private String error;
    private String cause;
}
