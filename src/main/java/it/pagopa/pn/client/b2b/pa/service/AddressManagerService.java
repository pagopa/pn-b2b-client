package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.address_manager.model.NormalizeSyncRequest;
import org.springframework.http.ResponseEntity;

public interface AddressManagerService {
    ResponseEntity<?> normalizzazioneSyncWithHttpInfo(NormalizeSyncRequest request);
}
