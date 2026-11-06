package it.pagopa.pn.cucumber.steps.pa.service;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtDelegationStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitution;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitutionsResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationResponse;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.IPnPtInstitutionsClient;
import it.pagopa.pn.client.b2b.pa.service.IPnSelfcareDelegationsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestisce le deleghe PA→PT su Selfcare (creazione e revoca) e la lettura dell'elenco degli Enti deleganti
 * di un Partner Tecnologico. Ogni metodo seleziona esplicitamente l'Ente PT su cui opera.
 */
@Slf4j
@Service
@ScenarioScope
@RequiredArgsConstructor
public class PtEntiDelegantiService {

    private final IPnPtInstitutionsClient ptInstitutionsClient;
    private final IPnSelfcareDelegationsClient selfcareDelegationsClient;
    private final SenderInfoProvider senderInfoProvider;

    // id delle deleghe create nello scenario, per coppia Ente PA|Ente PT
    private final Map<String, String> createdDelegationIds = new HashMap<>();

    public record AllPages(List<PtInstitution> institutions, PtInstitutionsResponse lastPage) {
    }

    /**
     * Crea la delega dell'Ente PA verso l'Ente PT; se la delega è già attiva (409) riusa quella esistente.
     */
    public String createDelegation(String paName, String ptName) {
        String paId = getPaInstitutionId(paName);
        String ptId = ptInstitutionsClient.getPtCxId(ptName);
        DelegationRequest request = new DelegationRequest()
                .from(paId)
                .to(ptId)
                .institutionFromName(paName)
                .institutionToName(ptName)
                .productId(selfcareDelegationsClient.getProductId())
                .type(DelegationRequest.TypeEnum.PT);
        String delegationId;
        try {
            delegationId = selfcareDelegationsClient.createDelegation(request).getId();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().value() != HttpStatus.CONFLICT.value()) {
                throw httpStatusCodeException;
            }
            delegationId = findActiveDelegationId(paId, ptId);
        }
        if (delegationId != null) {
            createdDelegationIds.put(delegationKey(paName, ptName), delegationId);
            log.info("Delega {} creata: {} -> {}", delegationId, paName, ptName);
        }
        return delegationId;
    }

    /**
     * Revoca la delega dell'Ente PA verso l'Ente PT creata nello scenario o, in mancanza, quella attiva su Selfcare.
     * Restituisce null se non esiste alcuna delega attiva da revocare.
     */
    public String revokeDelegation(String paName, String ptName) {
        String delegationId = createdDelegationIds.remove(delegationKey(paName, ptName));
        if (delegationId == null) {
            delegationId = findActiveDelegationId(getPaInstitutionId(paName), ptInstitutionsClient.getPtCxId(ptName));
        }
        if (delegationId != null) {
            selfcareDelegationsClient.deleteDelegation(delegationId);
            log.info("Delega {} revocata: {} -> {}", delegationId, paName, ptName);
        }
        return delegationId;
    }

    public String getPaInstitutionId(String paName) {
        return senderInfoProvider.getSenderId(paName);
    }

    public PtInstitutionsResponse getInstitutions(String ptName, String search, PtDelegationStatus status, Integer limit, String lastKey) {
        ptInstitutionsClient.setPt(ptName);
        return ptInstitutionsClient.getPtInstitutions(search, status, limit, lastKey);
    }

    /**
     * Richiesta senza client tipizzato; con ptName null la richiesta viene inviata senza indicare l'Ente PT.
     */
    public ResponseEntity<String> getInstitutionsRaw(String ptName, Map<String, String> queryParams) {
        if (ptName == null) {
            return ptInstitutionsClient.getPtInstitutionsRaw(queryParams, false);
        }
        ptInstitutionsClient.setPt(ptName);
        return ptInstitutionsClient.getPtInstitutionsRaw(queryParams, true);
    }

    public AllPages readAllPages(String ptName, String search, PtDelegationStatus status, Integer limit) {
        List<PtInstitution> institutions = new ArrayList<>();
        PtInstitutionsResponse response = null;
        String lastKey = null;
        do {
            response = getInstitutions(ptName, search, status, limit, lastKey);
            institutions.addAll(response.getItems());
            lastKey = response.getLastKey();
        } while (lastKey != null);
        return new AllPages(institutions, response);
    }

    private String findActiveDelegationId(String paId, String ptId) {
        return selfcareDelegationsClient.getDelegations(paId, ptId).stream()
                .filter(delegation -> DelegationResponse.StatusEnum.ACTIVE.equals(delegation.getStatus()))
                .map(DelegationResponse::getId)
                .findFirst()
                .orElse(null);
    }

    private static String delegationKey(String paName, String ptName) {
        return paName.toUpperCase() + "|" + ptName.toUpperCase();
    }
}
