package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateRequestParameter {
    private ShipmentCalculateRequest.ProductEnum product;
    private String geokey;
    private Integer numPages;
    private Boolean isReversePrinter;
    private Integer weight;
}
