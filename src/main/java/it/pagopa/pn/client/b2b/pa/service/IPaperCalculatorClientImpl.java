package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateResponse;
import org.springframework.http.ResponseEntity;

public interface IPaperCalculatorClientImpl {

    ResponseEntity<ShipmentCalculateResponse> calculateCostWithHttpInfo(String tenderId, ShipmentCalculateRequest shipmentCalculateRequest);
}
