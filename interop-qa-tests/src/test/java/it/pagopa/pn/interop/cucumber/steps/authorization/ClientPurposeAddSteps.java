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
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @And("^\\[si fa pulizia dei client e delle finalità create per il test\\]$")
    public void cleanupClientsAndPurposesCreatedForTest() {
        String tenantType = sharedStepsContext.getTenantType();
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        List<UUID> createdClientIds = listScenarioClientIds();

        for (UUID clientId : createdClientIds) {
            httpCallExecutor.performCall(() -> authorizationClient.getClient(clientId));
            if (httpCallExecutor.getResponseStatus() == HttpStatus.NOT_FOUND) {
                continue;
            }

            Client client = (Client) httpCallExecutor.getResponse();
            List<ClientPurpose> clientPurposes = client.getPurposes() == null ? List.of() : client.getPurposes();

            for (ClientPurpose clientPurpose : clientPurposes) {
                dataPreparationService.deletePurposeFromClient(clientId, clientPurpose.getPurposeId());
            }

            dataPreparationService.deleteClient(clientId);
        }

        Set<UUID> createdPurposeIds = new LinkedHashSet<>(sharedStepsContext.getPurposeCommonContext().getPurposesIdsAsUUID());
        for (UUID purposeId : createdPurposeIds) {
            deletePurposeWithPolling(purposeId);
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
            if (results == null || results.isEmpty()) {
                break;
            }

            clientIds.addAll(results.stream().map(CompactClient::getId).collect(Collectors.toList()));

            if (results.size() < limit) {
                break;
            }
            offset += limit;
        }

        return clientIds;
    }

    private void deletePurposeWithPolling(UUID purposeId) {
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().deletePurpose(purposeId));
        if (httpCallExecutor.getResponseStatus() == HttpStatus.NOT_FOUND) {
            return;
        }
        if (httpCallExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException("Errore durante la cancellazione della finalità " + purposeId);
        }

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().getPurpose(purposeId)),
                status -> status == HttpStatus.NOT_FOUND,
                "There was an error while deleting the purpose " + purposeId
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
