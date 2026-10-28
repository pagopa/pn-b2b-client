package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Client;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientPurpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientPurposeAddSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;

    public ClientPurposeAddSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.identityService = sharedStepsContext.getIdentityService();
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente associa la finalità al client con successo")
    public void userSuccessfullyRetrievesFinalization() {
        userRetrievesFinalization();
        if (httpCallExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException("Errore manifestatosi durante l'associazione della finalità al client. Consultare logs per maggior dettagli.");
        }
    }

    @When("l'utente richiede l'associazione della finalità al client")
    public void userRetrievesFinalization() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        userRetrievesFinalization(clientId);
    }

    @When("l'utente richiede l'associazione della finalità a un client inesistente")
    public void userRetrievesFinalizationForNonExistentClient() {
        UUID clientId = UUID.randomUUID();
        userRetrievesFinalization(clientId);
    }

    @When("l'utente associa le ultime {int} finalità create al client con successo")
    public void associateLatestPurposesToFirstClient(int numberOfPurposes) {
        UUID firstClientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        if (sharedStepsContext.getClientCommonContext().getTrackedFirstClientId() == null) {
            sharedStepsContext.getClientCommonContext().setTrackedFirstClientId(firstClientId);
        }
        List<UUID> createdPurposeIds = sharedStepsContext.getPurposeCommonContext().getPurposesIdsAsUUID();

        if (numberOfPurposes <= 0 || createdPurposeIds.size() < numberOfPurposes) {
            throw new IllegalArgumentException("Numero di finalità da associare non valido rispetto a quelle create");
        }

        int startIndex = createdPurposeIds.size() - numberOfPurposes;
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        for (UUID purposeId : createdPurposeIds.subList(startIndex, createdPurposeIds.size())) {
            dataPreparationService.addPurposeToClient(firstClientId, purposeId);
        }
    }

    @When("l'utente associa l'ultima finalità agli ultimi {int} client creati con successo")
    public void associateLastPurposeToLastNClients(int numberOfClients) {
        List<UUID> clients = sharedStepsContext.getClientCommonContext().getClients();
        if (numberOfClients <= 0) {
            throw new IllegalArgumentException("Numero client non valido: deve essere maggiore di zero");
        }
        if (clients.size() < numberOfClients) {
            throw new IllegalArgumentException("Numero client insufficiente: attesi almeno " + numberOfClients + " client");
        }

        UUID lastPurposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId();
        int fromIndex = clients.size() - numberOfClients;
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        for (UUID clientId : clients.subList(fromIndex, clients.size())) {
            dataPreparationService.addPurposeToClient(clientId, lastPurposeId);
        }
    }

    @And("l'associazione tra finalita e client è presente")
    public void checkPurposeClientAssociationSuccessfullyCreated() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        pollingService.makePolling(
                () -> authorizationClient.getClient(clientId),
                client -> client.getPurposes().stream().anyMatch(purpose -> purpose.getPurposeId().equals(purposeId)),
                "La finalita non risulta associata al client come previsto."
        );
    }

    @And("l'associazione tra finalita e client non è presente")
    public void checkPurposeClientAssociationNotPresent() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        pollingService.makePolling(
                () -> authorizationClient.getClient(clientId),
                client -> client.getPurposes().stream().noneMatch(purpose -> purpose.getPurposeId().equals(purposeId)),
                "La finalita risulta ancora associata al client, ma non dovrebbe esserlo."
        );
    }

    @And("^\\[\"([^\"]+)\" elimina i client e archivia le finalità create per il test\\]$")
    public void cleanupClientsAndArchivePurposesCreatedForTest(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        List<UUID> createdClientIds = listScenarioClientIds();

        for (UUID clientId : createdClientIds) {
            httpCallExecutor.performCall(() -> authorizationClient.getClient(clientId));
            if (httpCallExecutor.getResponseStatus() == HttpStatus.NOT_FOUND) {
                continue;
            }

            if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                throw new IllegalStateException("Errore nel recupero del client " + clientId + " (status=" + httpCallExecutor.getResponseStatus() + ")");
            }

            Object response = httpCallExecutor.getResponse();
            if (!(response instanceof Client client)) {
                throw new IllegalStateException("Response inattesa per getClient(" + clientId + "): " + (response != null ? response.getClass() : "null"));
            }
            List<ClientPurpose> clientPurposes = client.getPurposes();

            for (ClientPurpose clientPurpose : clientPurposes) {
                dataPreparationService.deletePurposeFromClient(clientId, clientPurpose.getPurposeId());
            }

            dataPreparationService.deleteClient(clientId);
        }

        List<UUID> createdPurposeIds = sharedStepsContext.getPurposeCommonContext().getPurposesIdsAsUUID();
        for (UUID purposeId : createdPurposeIds) {
            archivePurposeWithPolling(purposeId);
        }
    }

    private List<UUID> listScenarioClientIds() {
        int offset = 0;
        int limit = 50;
        List<UUID> clientIds = new ArrayList<>();

        while (true) {
            CompactClients page = authorizationClient.getClients(
                    offset,
                    limit,
                    String.valueOf(sharedStepsContext.getTestSeed()),
                    null,
                    null
            );

            List<CompactClient> results = page.getResults();
            if (IterableUtils.isEmpty(results)) {
                break;
            }

            clientIds.addAll(results.stream().map(CompactClient::getId).toList());

            if (results.size() < limit) {
                break;
            }
            offset += limit;
        }

        return clientIds;
    }

    private void archivePurposeWithPolling(UUID purposeId) {
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        Purpose purpose;
        try {
            purpose = purposeApiClient.getPurpose(purposeId);
        } catch (Exception e) {
            return; // finalità non trovata o non accessibile: nessuna azione necessaria
        }

        UUID activeVersionId = purpose.getVersions().stream()
                .filter(v -> PurposeVersionState.ACTIVE.equals(v.getState()))
                .map(PurposeVersion::getId)
                .findFirst()
                .orElse(null);

        if (activeVersionId == null) {
            return; // nessuna versione attiva da archiviare
        }

        purposeApiClient.archivePurposeVersion(purposeId, activeVersionId);

        final UUID versionIdToCheck = activeVersionId;
        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                p -> p.getVersions().stream()
                        .filter(v -> v.getId().equals(versionIdToCheck))
                        .anyMatch(v -> PurposeVersionState.ARCHIVED.equals(v.getState())),
                "Errore durante l'archiviazione della finalità " + purposeId
        );
    }

    private void userRetrievesFinalization(UUID clientId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.addClientPurpose(
                        clientId,
                        new PurposeAdditionDetailsSeed().purposeId(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())))
        );
    }
}
