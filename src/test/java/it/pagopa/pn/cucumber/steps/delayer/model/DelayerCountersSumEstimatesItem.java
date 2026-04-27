package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DelayerCountersSumEstimatesItem implements Serializable {
    private String pk;
    private String sk;
    private Integer numberOfShipments;
}
