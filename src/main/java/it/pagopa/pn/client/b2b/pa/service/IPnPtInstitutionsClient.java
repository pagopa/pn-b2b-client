package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtDelegationStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitutionsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Map;

public interface IPnPtInstitutionsClient {

    PtInstitutionsResponse getPtInstitutions(String search, PtDelegationStatus status, Integer limit, String lastKey) throws RestClientException;

    // chiamata senza client tipizzato: permette di inviare valori non validi (es. limit non numerico, status inesistente)
    // e di omettere l'header x-pagopa-pn-cx-id, che il client generato bloccherebbe prima dell'invio
    ResponseEntity<String> getPtInstitutionsRaw(Map<String, String> queryParams, boolean sendCxId) throws RestClientException;

    void setPt(String ptName);

    String getPtSetted();

    // cx-id dell'Ente PT, coincide con il suo institutionId Selfcare
    String getPtCxId(String ptName);
}
