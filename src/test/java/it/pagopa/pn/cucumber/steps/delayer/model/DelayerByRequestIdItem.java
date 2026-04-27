package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DelayerByRequestIdItem implements Serializable {
    private String pk;
    private String sk;
    private String attempt;
    private String cap;
    private String createdAt;
    private String deliveryDate;
    private String iun;
    private String notificationSentAt;
    private String prepareRequestDate;
    private String priority;
    private String productType;
    private String province;
    private String recipientId;
    private String requestId;
    private String senderPaId;
    private String tenderId;
    private String unifiedDeliveryDriver;
}
