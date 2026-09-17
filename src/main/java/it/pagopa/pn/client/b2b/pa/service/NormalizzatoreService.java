package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncResponse;
import org.springframework.http.ResponseEntity;

public interface NormalizzatoreService {

    NormalizzazioneSyncResponse normalizzazioneSync(
            String pnAddressManagerCxId,
            String xApiKey,
            NormalizzazioneSyncRequest request
    );

    ResponseEntity<NormalizzazioneSyncResponse> normalizzazioneSyncWithHttpInfo(
            String pnAddressManagerCxId,
            String xApiKey,
            NormalizzazioneSyncRequest request
    );
}