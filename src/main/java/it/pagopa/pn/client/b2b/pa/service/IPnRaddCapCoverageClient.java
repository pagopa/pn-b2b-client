package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.CreateRegistryRequestV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.GetRegistryResponseV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.RegistryV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.UpdateRegistryRequestV2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface IPnRaddCapCoverageClient {

    RegistryV2 addRegistry(String partnerId, CreateRegistryRequestV2 createRegistryRequestV2) throws RestClientException;

    void deleteRegistry(String partnerId, String locationId);

    GetRegistryResponseV2 retrieveRegistries(String xPagopaPnCxId, Integer limit, String lastKey) throws RestClientException;

    RegistryV2 updateRegistry(String partnerId, String locationId, UpdateRegistryRequestV2 updateRegistryRequestV2);

    ResponseEntity<Void> deleteRegistryWithHttpInfo(String partnerId, String locationId);

    //todo t cap new api

    //creazione coperture

    //aggiornamento copertura

    //cancellazione copertura

    //lettura copertura

    //verifica copertura
}
