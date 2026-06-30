package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DelayerSenderLimitItem implements Serializable {

    private String pk;
    private String deliveryDate;
    private Integer weeklyEstimate;
    private Integer monthlyEstimate;
    private Integer originalEstimate;
    private String paId;
    private String productType;
    private String province;
}
