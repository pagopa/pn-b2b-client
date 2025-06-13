package it.pagopa.pn.interop.cucumber.steps.m2m.purpose;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposeVersionsListRequest;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposesListRequest;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.enums.PurposeOperation;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

public class PurposesSteps {
    @ParameterType("ACTIVE|DRAFT|SUSPENDED|WAITING_FOR_APPROVAL|ARCHIVED|REJECTED")
    public PurposeVersionState m2mPurposeVersionState(String state) {
        return PurposeVersionState.fromValue(state);
    }

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IM2MPurposeClient purposeClient;
    private final IPurposeApiClient bffPurposeClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final int newDailyCalls = 50;

    public PurposesSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.purposeClient = clientTokenConfigurator.getM2mPurposeClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.bffPurposeClient = clientTokenConfigurator.getPurposeApiClient();
    }

    @SuppressWarnings("java:S6204")
    @When("l'utente tenta di recuperare una lista di {int} finalità create")
    public void agreementsListAttempt(int agreementsQuantity) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        httpCallExecutor.performCall(() -> purposeClient.getPurposes(
            PurposesListRequest.builder()
                .offset(0)
                .limit(agreementsQuantity)
                .eservicesIds(List.of(eServiceId))
                .build()
        ));
    }

    @Then("sono state visualizzate correttamente {int} finalità create")
    public void purposesSuccessfullyGot(int expectedSize) {
        HttpStatus clientResponse = httpCallExecutor.getClientResponse();
        if(clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        Purposes purposes = (Purposes) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = purposes.getResults().stream().map(Purpose::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getPurposeCommonContext().getPurposesIdsAsUUID();
        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        }) ;
    }

    @When("l'utente tenta di creare una nuova versione della finalità aggiornando la stima di carico")
    public void createPurposeVersion() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        PurposeCommonContext purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
        PurposeVersionSeed purposeVersionSeed = new PurposeVersionSeed().dailyCalls(newDailyCalls);
        httpCallExecutor.performCall(
            () -> purposeClient.createPurposeVersion(
                UUID.fromString(purposeCommonContext.getPurposeId()),
                purposeVersionSeed
            )
        );

        if(httpCallExecutor.getClientResponse().is2xxSuccessful()) {
            PurposeVersion createdVersion = (PurposeVersion) httpCallExecutor.getResponse();
            purposeCommonContext.addCurrentVersionId(createdVersion.getId());
        }
    }

    @Then("la nuova versione della finalità è stata creata correttamente")
    public void purposeVersionSuccessfullyCreated() {
        httpCallExecutor.performCall(() -> purposeClient.getVersion(
            sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
            sharedStepsContext.getPurposeCommonContext().getCurrentVersionIdAsUUID()));

        assertThat(httpCallExecutor.getClientResponse().is2xxSuccessful())
            .as("Check GET created purpose response status")
            .withFailMessage("Non è stato possibile reperire la purpose version creata. "
                + "Visionare i log delle chiamate per maggiori dettagli.")
            .isTrue();

        checkCreatedVersion();
    }

    private void checkCreatedVersion() {
        PurposeVersion version = (PurposeVersion) httpCallExecutor.getResponse();
        assertThat(version.getDailyCalls())
            .as("Check purpose version created")
            .isEqualTo(this.newDailyCalls);
    }

    @When("l'utente crea una nuova versione della finalità con successo aggiornando la stima di carico")
    public void successfullyCreateNewVersion() {
        createPurposeVersion();
        purposeVersionSuccessfullyCreated();
    }

    @When("l'utente tenta di visualizzare la lista delle versioni della finalità")
    public void purposeVersionsListAttempt() {
        listPurposeVersions(sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID());
    }

    @Then("sono state visualizzate correttamente {int} versioni della finalità")
    public void purposeVersionsSuccessfullyGot(int expectedSize) {
        HttpStatus clientResponse = httpCallExecutor.getClientResponse();
        if(clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        PurposeVersions versions = (PurposeVersions) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = versions.getResults().stream().map(PurposeVersion::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getPurposeCommonContext().getPurposeCurrentVersionsIdsAsUUID();

        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        }) ;
    }

    @When("l'utente tenta di visualizzare la lista delle versioni di una finalità inesistente")
    public void nonExistentPurposeVersionsListAttempt() {
        listPurposeVersions(UUID.randomUUID());
    }

    private void listPurposeVersions(UUID purposeId) {
        httpCallExecutor.performCall(() -> purposeClient.getVersions(
            PurposeVersionsListRequest.builder()
                .offset(0)
                .limit(20)
                .purposeId(purposeId)
                .build()
        ));
    }

    @When("l'utente tenta di visualizzare la nuova versione della finalità")
    public void getPurposeVersionAttempt() {
        getPurposeVersion(
            sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
            sharedStepsContext.getPurposeCommonContext().getCurrentVersionIdAsUUID()
        );

    }

    @When("l'utente tenta di visualizzare una versione inesistente di una finalità inesistente")
    public void nonExistentPurposeVersionGetAttempt() {
        getPurposeVersion(
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }

    @When("l'utente tenta di visualizzare una versione inesistente della finalità esistente")
    public void purposeNonExistentVersionGetAttempt() {
        getPurposeVersion(
            sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
            UUID.randomUUID()
        );
    }

    private void getPurposeVersion(UUID purposeId, UUID versionId) {
        httpCallExecutor.performCall(() -> purposeClient.getVersion(purposeId, versionId));
    }

    @Then("la nuova versione della finalità è stata visualizzata correttamente")
    public void purposeVersionSuccessfullyGot() {
        checkCreatedVersion();
    }

    @When("l'utente tenta l'attivazione della finalità")
    public void activatePurposeAttempt() {
        activatePurpose(sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID());
    }

    @When("l'utente tenta la sospensione della finalità")
    public void suspendPurposeAttempt() {
        suspendPurpose(sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID());
    }

    @When("l'utente tenta la sospensione di una finalità inesistente")
    public void nonExistentPurposeSuspendAttempt() {
        suspendPurpose(UUID.randomUUID());
    }

    @When("l'utente tenta l'attivazione di una finalità inesistente")
    public void activateNonExistentPurpose() {
        activatePurpose(UUID.randomUUID());
    }

    private void activatePurpose(UUID purposeIdAsUUID) {
        httpCallExecutor.performCall(() -> purposeClient.activatePurpose(purposeIdAsUUID));
    }

    private void suspendPurpose(UUID purposeIdAsUUID) {
        httpCallExecutor.performCall(() -> purposeClient.suspendPurpose(purposeIdAsUUID));
    }

    // TODO step sostituibile con purposeStateSuccessfullyChanged
    @Then("la finalità è stata attivata correttamente")
    public void purposeSuccessfullyActivated() {
        purposeStateSuccessfullyChanged(PurposeVersionState.ACTIVE);
    }

    @Then("la finalità è in stato {m2mPurposeVersionState}")
    @Then("purpose in stato {m2mPurposeVersionState}")
    public void purposeStateSuccessfullyChanged(PurposeVersionState expectedState) {
        if (httpCallExecutor.getClientResponse().is2xxSuccessful()) {
            Purpose purpose = (Purpose) httpCallExecutor.getResponse();
            PurposeVersionState returnedState;

            switch (expectedState) {
                case WAITING_FOR_APPROVAL -> returnedState = purpose.getWaitingForApprovalVersion().getState();
                case REJECTED -> returnedState = purpose.getRejectedVersion().getState();
                default -> returnedState = purpose.getCurrentVersion().getState();
            }

            assertThat(expectedState)
                    .as("Verifica finalità restituita")
                    .isEqualTo(returnedState);
        }

        UUID purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID();
        pollingService.makePolling(() -> httpCallExecutor.performCall(() ->
                        // purposeClient.getPurpose(purposeId)), TODO 11/06/2025 ripristinare non appena risolto il bug della relativa API m2m che ne impedisce l'utilizzo
                        bffPurposeClient.getPurpose(purposeId)),
                res -> {
                    if (!res.is2xxSuccessful()) return false;

                    it.pagopa.interop.generated.openapi.clients.bff.model.Purpose purpose =
                            (it.pagopa.interop.generated.openapi.clients.bff.model.Purpose) httpCallExecutor.getResponse();

                    it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState actualState;

                    switch (expectedState) {
                        case WAITING_FOR_APPROVAL -> actualState = purpose.getWaitingForApprovalVersion().getState();
                        case REJECTED -> actualState = purpose.getRejectedVersion().getState();
                        default -> actualState = purpose.getCurrentVersion().getState();
                    }

                    return actualState.equals(
                            it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState.fromValue(expectedState.getValue()));
                },
                "La correttezza della finalità restituita dalla API di attivazione non è stata confermata dalla API di lettura. Visualizzare i log per maggiori dettagli.");
    }

    @When("l'utente tenta di riattivare purpose")
    public void unsuspendPurpose() {
        performPurposeAction(PurposeOperation.UNSUSPEND, EntityIdType.DEFAULT_ID);
    }

    @When("l'utente tenta di riattivare purpose con un id {entityIdType}")
    public void unsuspendPurposeByIdType(EntityIdType entityIdType) {
        performPurposeAction(PurposeOperation.UNSUSPEND, entityIdType);
    }

    @When("l'utente tenta di approvare purpose")
    public void approvePurpose() {
        performPurposeAction(PurposeOperation.APPROVE, EntityIdType.DEFAULT_ID);
    }

    @When("l'utente tenta di approvare purpose con un id {entityIdType}")
    public void approvePurposeByIdType(EntityIdType entityIdType) {
        performPurposeAction(PurposeOperation.APPROVE, entityIdType);
    }

    @When("l'utente tenta di archiviare purpose")
    public void archivePurpose() {
        performPurposeAction(PurposeOperation.ARCHIVE, EntityIdType.DEFAULT_ID);
    }

    @When("l'utente tenta di archiviare purpose con un id {entityIdType}")
    public void archivePurposeByIdType(EntityIdType entityIdType) {
        performPurposeAction(PurposeOperation.APPROVE, entityIdType);
    }

    private void performPurposeAction(PurposeOperation action, EntityIdType entityIdType) {
        UUID id = generateId(entityIdType);
        httpCallExecutor.performCall(() -> {
            Purpose out;
            switch (action) {
                case APPROVE -> out = purposeClient.approvePurpose(id);
                case UNSUSPEND -> out = purposeClient.unsuspendPurpose(id);
                case ARCHIVE -> out = purposeClient.archivePurpose(id);
                default -> throw new UnsupportedOperationException("Not expected purpose action '%s'".formatted(action));
            }
            return out;
        });
    }

    private UUID generateId(EntityIdType entityIdType) {
        return switch (entityIdType) {
            case NULL_ID -> null;
            case NON_EXISTENT_ID -> UUID.randomUUID();
            case DEFAULT_ID -> {
                var purposeContext = sharedStepsContext.getPurposeCommonContext();
                List<String> ids = purposeContext.getPurposesIds();

                Assertions.assertThat(ids)
                        .as("La lista degli ID dei purpose deve contenere esattamente un elemento")
                        .hasSize(1);

                String purposeId = ids.get(0);
                yield UUID.fromString(purposeId);
            }
        };
    }
}
