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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Gestisce le deleghe PA→PT su Selfcare e la lettura dell'elenco degli Enti deleganti di un Partner Tecnologico.
 * Creazione e revoca attendono che la delega sia propagata fino a pn-external-registries.
 * La verifica delle deleghe attive legge i dati di pn-external-registries, non quelli di SelfCare,
 * tranne per la PA non aderente: SEND ne scarta la delega, che quindi si trova solo su SelfCare.
 */
@Slf4j
@Service
@ScenarioScope
@RequiredArgsConstructor
public class PtEntiDelegantiService {
    private static final int MAX_LIMIT = 50;
    private static final String PA_NON_ADERENTE = "Comune di Castagnero";

    private final IPnPtInstitutionsClient ptInstitutionsClient;
    private final IPnSelfcareDelegationsClient selfcareDelegationsClient;
    private final SenderInfoProvider senderInfoProvider;

    @Value("${pn.external.pt.pa-non-aderente.id}")
    private String paNonAderenteId;

    private final Map<String, String> delegationIds = new HashMap<>();

    public record AllPages(List<PtInstitution> institutions, PtInstitutionsResponse lastPage) {
    }

    /**
     * Crea sempre una nuova delega (revocando quella eventualmente attiva) e ne attende la propagazione.
     */
    public String createDelegation(String paName, String ptName) {
        String delegationId = conferDelegation(paName, ptName);
        awaitDelegationStatus(getPaInstitutionId(paName), ptName, delegationId, PtDelegationStatus.ACTIVE);
        return delegationId;
    }

    /**
     * Conferisce una nuova delega senza attenderne la propagazione, revocando prima quella eventualmente attiva.
     * Per la PA non aderente la delega attiva viene cercata su SelfCare, perché non arriva a pn-external-registries.
     */
    public String conferDelegation(String paName, String ptName) {
        return conferDelegation(paName, ptName, paName);
    }

    public String conferDelegation(String paName, String ptName, String institutionFromName) {
        String paId = getPaInstitutionId(paName);
        String ptId = ptInstitutionsClient.getPtCxId(ptName);
        String activeDelegationId = PA_NON_ADERENTE.equalsIgnoreCase(paName)
                ? findActiveSelfcareDelegationId(paId, ptId)
                : findActiveDelegationId(paId, ptName);
        if (activeDelegationId != null)
            selfcareDelegationsClient.deleteDelegation(activeDelegationId);
        String delegationId = selfcareDelegationsClient.createDelegation(buildDelegationRequest(institutionFromName, ptName, paId, ptId)).getId();
        delegationIds.put(delegationKey(paName, ptName), delegationId);
        log.info("Delega {} conferita: {} -> {}", delegationId, paName, ptName);
        return delegationId;
    }

    /**
     * Restituisce null se non esiste alcuna delega attiva da revocare.
     */
    public String revokeDelegation(String paName, String ptName) {
        String paId = getPaInstitutionId(paName);
        String delegationId = findActiveDelegationId(paId, ptName);
        if (delegationId != null) {
            selfcareDelegationsClient.deleteDelegation(delegationId);
            delegationIds.put(delegationKey(paName, ptName), delegationId);
            log.info("Delega {} revocata: {} -> {}", delegationId, paName, ptName);
            awaitDelegationStatus(paId, ptName, delegationId, PtDelegationStatus.REVOKED);
        }
        return delegationId;
    }

    public void ensureNoActiveDelegation(String paName, String ptName) {
        if (findActiveDelegationId(getPaInstitutionId(paName), ptName) != null) {
            revokeDelegation(paName, ptName);
        }
    }

    public String getDelegationId(String paName, String ptName) {
        return delegationIds.get(delegationKey(paName, ptName));
    }

    public String getPaInstitutionId(String paName) {
        return PA_NON_ADERENTE.equalsIgnoreCase(paName) ? paNonAderenteId : senderInfoProvider.getSenderId(paName);
    }

    public PtInstitutionsResponse getInstitutions(String ptName, String search, PtDelegationStatus status, Integer limit, String lastKey) {
        ptInstitutionsClient.setPt(ptName);
        return ptInstitutionsClient.getPtInstitutions(search, status, limit, lastKey);
    }

    /**
     * Con ptName null la richiesta viene inviata senza indicare l'Ente PT.
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

    private void awaitDelegationStatus(String paId, String ptName, String delegationId, PtDelegationStatus status) {
        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> assertThat(readAllPages(ptName, null, null, MAX_LIMIT).institutions())
                        .as("La delega %s non risulta %s nell'elenco dell'Ente PT %s", delegationId, status, ptName)
                        .anyMatch(institution -> paId.equals(institution.getInstitutionId())
                                && delegationId.equals(institution.getDelegationId())
                                && status.equals(institution.getStatus())));
    }

    private DelegationRequest buildDelegationRequest(String institutionFromName, String ptName, String paId, String ptId) {
        return new DelegationRequest()
                .from(paId)
                .to(ptId)
                .institutionFromName(institutionFromName)
                .institutionToName(ptName)
                .productId(selfcareDelegationsClient.getProductId())
                .type(DelegationRequest.TypeEnum.PT);
    }

    private String findActiveDelegationId(String paId, String ptName) {
        return readAllPages(ptName, null, PtDelegationStatus.ACTIVE, MAX_LIMIT).institutions().stream()
                .filter(institution -> paId.equals(institution.getInstitutionId()))
                .map(PtInstitution::getDelegationId)
                .findFirst()
                .orElse(null);
    }

    private String findActiveSelfcareDelegationId(String paId, String ptId) {
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
