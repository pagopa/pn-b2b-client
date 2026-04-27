package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DelayerUsedSenderLimitItem implements Serializable {

    private String pk;
    private String deliveryDate;
    private Integer numberOfShipment;
    private String paId;
    private String productType;
    private String province;
    private Integer senderLimit;
}
