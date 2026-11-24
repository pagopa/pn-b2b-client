package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DelayerStatusExecution implements Serializable {

    private String executionArn;
    private String status;
    private String startDate;
    private String stopDate;
    private String error;
    private String cause;
}
