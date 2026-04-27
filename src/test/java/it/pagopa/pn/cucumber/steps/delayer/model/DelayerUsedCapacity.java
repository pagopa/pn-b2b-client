package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DelayerUsedCapacity implements Serializable {
    private String unifiedDeliveryDriverGeokey;
    private String deliveryDate;
    private String geoKey;
    private String unifiedDeliveryDriver;
    private int usedCapacity;
    private int capacity;

    // se item assente
    private String message;
}
