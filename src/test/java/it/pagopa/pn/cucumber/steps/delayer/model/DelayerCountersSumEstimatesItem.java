package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DelayerCountersSumEstimatesItem implements Serializable {
    private String pk;
    private String sk;
    private Integer numberOfShipments;
    private Integer firstWeekNumberOfShipments;
    private Integer secondWeekNumberOfShipments;
}
