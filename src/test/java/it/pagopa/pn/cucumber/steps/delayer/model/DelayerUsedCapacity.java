package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DelayerUsedCapacity implements Serializable {
    private String unifiedDeliveryDriverGeokey;
    private String deliveryDate;
    private String geoKey;
    private String unifiedDeliveryDriver;
    private Integer usedCapacity;
    private Integer capacity;

    // se item assente
    private String message;
    private Integer declaredCapacity;
}