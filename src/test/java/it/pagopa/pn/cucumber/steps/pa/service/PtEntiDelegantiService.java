package it.pagopa.pn.cucumber.steps.pa.service;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtDelegationStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitution;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.external.registries.pt.model.PtInstitutionsResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationResponse;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnPtInstitutionsClient;
import it.pagopa.pn.client.b2b.pa.service.IPnSelfcareDelegationsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final PollingConfig DELEGATION_POLLING = PollingConfig.of(Duration.ofSeconds(10), Duration.ofSeconds(5));
    private static final Duration DELEGATION_CHANGE_PADDING = Duration.ofSeconds(1);
    private static final String PA_NON_ADERENTE = "Comune di Castagnero";

    private final IPnPtInstitutionsClient ptInstitutionsClient;
    private final IPnSelfcareDelegationsClient selfcareDelegationsClient;
    private final SenderInfoProvider senderInfoProvider;
    private final DynamoDbService dynamoDbService;

    @Value("${pn.external.pt.pa-non-aderente.id}")
    private String paNonAderenteId;

    private final Map<String, String> delegationIds = new HashMap<>();
    private final Map<String, String> previousDelegationIds = new HashMap<>();

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
        padDelegationChange();
        String delegationId = selfcareDelegationsClient.createDelegation(buildDelegationRequest(institutionFromName, ptName, paId, ptId)).getId();
        String key = delegationKey(paName, ptName);
        if (delegationIds.containsKey(key))
            previousDelegationIds.put(key, delegationIds.get(key));
        delegationIds.put(key, delegationId);
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
            padDelegationChange();
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

    /**
     * Delega conferita prima dell'ultima nello scenario corrente (null se nello scenario ne è stata conferita una sola).
     */
    public String getPreviousDelegationId(String paName, String ptName) {
        return previousDelegationIds.get(delegationKey(paName, ptName));
    }

    /**
     * Storico delle variazioni della delega PA→PT letto da pn-PtDelegations, dalla più recente.
     */
    public List<PtDelegationRecord> getDelegationHistory(String paName, String ptName) {
        String paId = getPaInstitutionId(paName);
        List<PtDelegationRecord> history = dynamoDbService.callAllPages(DynamoTableName.PT_DELEGATIONS, Map.of(
                        ":v_ptId", AttributeValue.builder().s(ptInstitutionsClient.getPtCxId(ptName)).build()))
                .stream()
                .map(PtDelegationRecord::from)
                .filter(delegationRecord -> paId.equals(delegationRecord.institutionId()))
                .sorted(Comparator.comparing(PtDelegationRecord::lastChangeAt, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
                .toList();
        log.info("{} - storico delega {} -> {}: {}", DynamoTableName.PT_DELEGATIONS.getValue(), paName, ptName, history);
        return history;
    }

    /**
     * Attende che le ultime due variazioni della delega PA→PT abbiano gli stati attesi e le restituisce, dalla più recente.
     */
    public List<PtDelegationRecord> awaitLastTwoDelegationRecords(String paName, String ptName, String newerStatus, String olderStatus) {
        List<PtDelegationRecord> history = Poller.pollUntil(
                () -> getDelegationHistory(paName, ptName),
                records -> records.size() >= 2
                        && newerStatus.equals(records.get(0).status())
                        && olderStatus.equals(records.get(1).status()),
                DELEGATION_POLLING,
                lastValue -> "Le ultime due variazioni della delega %s -> %s non hanno stato %s e %s: %s"
                        .formatted(paName, ptName, newerStatus, olderStatus, lastValue));
        return history.subList(0, 2);
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

    /**
     * Garantisce che due variazioni consecutive della stessa delega abbiano lastChangeAt diversi su pn-PtDelegations.
     */
    private void padDelegationChange() {
        try {
            Thread.sleep(DELEGATION_CHANGE_PADDING.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Attesa tra le variazioni della delega interrotta", e);
        }
    }

    private void awaitDelegationStatus(String paId, String ptName, String delegationId, PtDelegationStatus status) {
        Poller.pollUntil(
                () -> readInstitutionsIgnoringErrors(ptName),
                institutions -> institutions.stream().anyMatch(institution -> paId.equals(institution.getInstitutionId())
                        && delegationId.equals(institution.getDelegationId())
                        && status.equals(institution.getStatus())),
                DELEGATION_POLLING,
                lastValue -> "La delega %s non risulta %s nell'elenco dell'Ente PT %s".formatted(delegationId, status, ptName));
    }

    /**
     * Un errore di lettura non interrompe il polling: il tentativo conta come condizione non soddisfatta.
     */
    private List<PtInstitution> readInstitutionsIgnoringErrors(String ptName) {
        try {
            return readAllPages(ptName, null, null, MAX_LIMIT).institutions();
        } catch (RuntimeException e) {
            log.warn("Lettura elenco Enti deleganti di {} fallita, nuovo tentativo: {}", ptName, e.getMessage());
            return List.of();
        }
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
