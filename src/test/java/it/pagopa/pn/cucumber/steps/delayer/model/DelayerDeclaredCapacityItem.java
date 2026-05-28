package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class DelayerDeclaredCapacityItem implements Serializable {
    private String unifiedDeliveryDriverGeokey;
    private String deliveryDate;
    private String geoKey;
    private String unifiedDeliveryDriver;
    private Integer usedCapacity;
    private Integer capacity;

    // Campi non documentati nel readme
    private List<String> products;


}
