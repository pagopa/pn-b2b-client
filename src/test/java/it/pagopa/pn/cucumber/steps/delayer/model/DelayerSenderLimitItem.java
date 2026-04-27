package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.io.Serializable;

@Data
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
