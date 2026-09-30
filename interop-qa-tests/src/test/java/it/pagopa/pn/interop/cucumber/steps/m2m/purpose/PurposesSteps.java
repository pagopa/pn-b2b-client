package it.pagopa.pn.interop.cucumber.steps.m2m.purpose;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposePatchRequest;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposeVersionsListRequest;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposesListRequest;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.ReversePurposePatchRequest;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant.PurposePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant.ReversePurposePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.purpose.enums.PurposeOperation;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class PurposesSteps {
    @ParameterType("ACTIVE|DRAFT|SUSPENDED|WAITING_FOR_APPROVAL|ARCHIVED|REJECTED")
    public PurposeVersionState m2mPurposeVersionState(String state) {
        return PurposeVersionState.fromValue(state);
    }

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IM2MPurposeClient purposeClient;
    private final IPurposeApiClient bffPurposeClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final int newDailyCalls = 50;

    private final PurposePatchOperationsAssistant purposePatchAssistant;
    private final ReversePurposePatchOperationsAssistant reversePurposePatchAssistant;

    public PurposesSteps(ClientTokenConfigurator clientTokenConfigurator,
                         SharedStepsContext sharedStepsContext,
                         PurposePatchOperationsAssistant purposePatchAssistant,
                         ReversePurposePatchOperationsAssistant reversePurposePatchAssistant) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.purposeClient = clientTokenConfigurator.getM2mPurposeClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.bffPurposeClient = clientTokenConfigurator.getPurposeApiClient();
        this.purposePatchAssistant = purposePatchAssistant;
        this.reversePurposePatchAssistant = reversePurposePatchAssistant;
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
        HttpStatus clientResponse = httpCallExecutor.getResponseStatus();
        if (clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        Purposes purposes = (Purposes) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = purposes.getResults().stream().map(Purpose::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getPurposeCommonContext().getPurposesIdsAsUUID();
        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        });
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

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            PurposeVersion createdVersion = (PurposeVersion) httpCallExecutor.getResponse();
            purposeCommonContext.addCurrentVersionId(createdVersion.getId());
        }
    }

    @Then("la nuova versione della finalità è stata creata correttamente")
    public void purposeVersionSuccessfullyCreated() {
        httpCallExecutor.performCall(() -> purposeClient.getVersion(
                sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
                sharedStepsContext.getPurposeCommonContext().getCurrentVersionIdAsUUID()));

        assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful())
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
        HttpStatus clientResponse = httpCallExecutor.getResponseStatus();
        if (clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        PurposeVersions versions = (PurposeVersions) httpCallExecutor.getResponse();
        List<UUID> visualizedIds = versions.getResults().stream().map(PurposeVersion::getId).toList();
        List<UUID> createdIds = sharedStepsContext.getPurposeCommonContext().getPurposeCurrentVersionsIdsAsUUID();

        assertSoftly(softly -> {
            softly.assertThat(visualizedIds).hasSize(expectedSize);
            softly.assertThat(createdIds).containsAll(visualizedIds);
        });
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
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
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
        performPurposeAction(PurposeOperation.UNSUSPEND, null);
    }

    @When("l'utente tenta di riattivare purpose con un id {entityIdType}")
    public void unsuspendPurposeByIdType(EntityIdType entityIdType) {
        performPurposeAction(PurposeOperation.UNSUSPEND, entityIdType);
    }

    @When("l'utente tenta di approvare purpose")
    public void approvePurpose() {
        performPurposeAction(PurposeOperation.APPROVE, null);
    }

    @When("l'utente tenta di approvare purpose con un id {entityIdType}")
    public void approvePurposeByIdType(EntityIdType entityIdType) {
        performPurposeAction(PurposeOperation.APPROVE, entityIdType);
    }

    @When("l'utente tenta di archiviare purpose")
    public void archivePurpose() {
        performPurposeAction(PurposeOperation.ARCHIVE, null);
    }

    @When("l'utente tenta di archiviare purpose con un id {entityIdType}")
    public void archivePurposeByIdType(EntityIdType entityIdType) {
        performPurposeAction(PurposeOperation.APPROVE, entityIdType);
    }

    @When("l'utente tenta di ottenere la richiesta di fruizione correlata alla finalità")
    public void getAgreementPurpose() {
        UUID purposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId();
        httpCallExecutor.performCall(() -> purposeClient.getPurposeAgreement(purposeId));
    }

    @When("l'utente tenta di ottenere la richiesta di fruizione correlata a una finalità inesistente")
    public void getNonExistentAgreementPurpose() {
        UUID purposeId = UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeClient.getPurposeAgreement(purposeId));
    }

    @Then("la richiesta di fruizione è stata correttamente visualizzata in stato {string}")
    public void agreementPurposeVisualized(String agreementState) {
        if (httpCallExecutor.getResponseStatus().isError()) {
            fail("Il GET dell'agreement correlato alla purpose ha generato il "
                            + "seguente errore: %s. Consultare i log per maggiori dettagli.",
                    httpCallExecutor.getResponseStatus());
        }

        Agreement returnedAgreement = (Agreement) httpCallExecutor.getResponse();
        assertSoftly(softly -> {
            softly.assertThat(returnedAgreement.getState())
                    .as("Verifica stato dell'agreement restituito")
                    .isEqualTo(AgreementState.fromValue(agreementState));
            softly.assertThat(returnedAgreement.getEserviceId())
                    .as("Verifica eServiceId dell'agreement restituito")
                    .isEqualTo(sharedStepsContext.getEServicesCommonContext().getEserviceId());
            softly.assertThat(returnedAgreement.getDescriptorId())
                    .as("Verifica e-service descriptorId dell'agreement restituito")
                    .isEqualTo(sharedStepsContext.getEServicesCommonContext().getDescriptorId());
            softly.assertThat(OffsetDateTime.parse(returnedAgreement.getCreatedAt()))
                    .as("Verifica data di creazione dell'agreement restituito")
                    .isCloseTo(sharedStepsContext.getAgreementCommonContext().getAgreementCreationTime(), within(10, SECONDS));
        });
    }

    // FIXME usato solo per un test locale, rimuovere
    @Given("vengono settate {string} come purposeId e {string} come versionId")
    public void setPurposeVersion(String purposeId, String versionId) {
        sharedStepsContext.getPurposeCommonContext().setPurposesIds(List.of(purposeId));
        sharedStepsContext.getPurposeCommonContext().setCurrentVersionIds(List.of(versionId));
    }

    @When("l'utente tenta di ottenere il documento dell'analisi del rischio correlato alla finalità")
    public void downloadPurposeDocument() {
        UUID purposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId();
        UUID versionId = sharedStepsContext.getPurposeCommonContext().getCurrentVersionIdAsUUID();
        httpCallExecutor.performCall(() -> purposeClient.downloadPurposeVersionDocument(purposeId, versionId));

        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(() -> purposeClient.downloadPurposeVersionDocument(purposeId, versionId)),
                HttpStatus::is2xxSuccessful,
                "Errore durante il recupero dell'analisi del rischio"
        );
    }

    @When("l'utente tenta di ottenere il documento dell'analisi del rischio correlato a una finalità inesistente")
    public void downloadNonExistentPurposeDocument() {
        UUID purposeId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeClient.downloadPurposeVersionDocument(purposeId, versionId));
    }

    @Then("il file restituito non è vuoto")
    public void nonEmptyFile() {
        FileDownloadMultipart fileMultipart = (FileDownloadMultipart) httpCallExecutor.getResponse();
        assertSoftly(softly -> {
            softly.assertThat(fileMultipart.getId()).as("File id check").isNotNull();
            softly.assertThat(fileMultipart.getFilename()).as("Filename check").isNotBlank();
            softly.assertThat(fileMultipart.getFile().length()).as("File size check").isGreaterThan(0L);
        });
    }

    @When("l'utente tenta di effettuare la modifica parziale della finalità")
    public void patchPurpose() {
        PurposePatchRequest request = purposePatchAssistant.buildDefaultPatchRequest();
        purposePatchAssistant.patchResource(request);
    }

    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale della finalità")
    public void patchPurpose(String tenant, M2MRole m2mRole) {
        PurposePatchRequest request = purposePatchAssistant.buildDefaultPatchRequest();
        purposePatchAssistant.patchResource(request, tenant, m2mRole);
    }

    @When("l'utente tenta di effettuare la modifica parziale della finalità con token non valido")
    public void patchPurposeWithNotValidToken() {
        purposePatchAssistant.patchResourceWithInvalidToken(purposePatchAssistant.buildDefaultPatchRequest());
    }

    @When("viene aggiornato il draft purpose con purposeId {string} e title {string}, description {string}, isFreeOfCharge {string}, freeOfChargeReason {string}, riskAnalysisForm {string}, dailyCalls {string}")
    public void patchDraftPurpose(
            String purposeId,
            String title,
            String description,
            String isFreeOfCharge,
            String freeOfChargeReason,
            String riskAnalysisForm,
            String dailyCalls
    ) {
        UUID purposeIdValue = buildPurposeIdValue(purposeId);

        // default valido (include un RiskAnalysisFormSeed valido)
        PurposePatchRequest req = purposePatchAssistant.buildDefaultPatchRequest();

        // override SEMPRE (anche a null)
        req.setTitle(nullOrBlankOrValue(title));
        req.setDescription(nullOrBlankOrValue(description));
        req.setIsFreeOfCharge(nullableBoolean(isFreeOfCharge));

        req.setFreeOfChargeReason(nullOrBlankOrValue(freeOfChargeReason));
        req.setDailyCalls(nullableInteger(dailyCalls));

        // riskAnalysisForm: token-based minimale
        // - %null -> null
        // - %actual -> lascia quello del default
        // - %invalid -> riskAnalysis errata
        String raf = nullOrBlankOrValue(riskAnalysisForm);
        if (raf == null || raf.isBlank()) {
            req.setRiskAnalysisForm(null);
        } else if ("%actual".equalsIgnoreCase(raf) || "actual".equalsIgnoreCase(raf)) {
            // non fare nulla: resta quello del default builder
        } else if ("%invalid".equalsIgnoreCase(raf) || "invalid".equalsIgnoreCase(raf)) {
            // se vuoi random vero, puoi richiamare buildDefaultPatchRequest() e prendere solo il RAF
            req.setRiskAnalysisForm(purposePatchAssistant.buildDefaultPatchRequest(false).getRiskAnalysisForm());
        } else {
            req.setRiskAnalysisForm(null);
        }

        this.httpCallExecutor.performCall(
                () -> purposeClient.patchPurpose(purposeIdValue, req)
        );
    }

    private UUID buildPurposeIdValue(String purposeId) {
        UUID purposeIdValue;
        // FIXME logica da centralizzare in StepParser
        if (purposeId.contains("actual")) {
            purposeIdValue = sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID();
        } else {
            purposeIdValue = uuidOrRandomOrNull(purposeId);
        }
        return purposeIdValue;
    }

    @Then("la finalità è stata parzialmente modificata correttamente")
    public void verificaPatchedPurpose() {
        purposePatchAssistant.checkPatchedResource();
    }

    @Then("la finalità non ha subito modifiche")
    public void verificaUnpatchedPurpose() {
        purposePatchAssistant.checkUnpatchedResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale di una finalità inesistente")
    public void patchNonExistentPurpose() {
        purposePatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale della finalità specificando un sottoinsieme di informazioni")
    public void patchPurposeSubset() {
        PurposePatchRequest request = PurposePatchRequest.builder()
                .title("patched title - " + UUID.randomUUID())
                .build();
        purposePatchAssistant.patchResource(request);
    }

    @Then("la finalità restituita è coerente con le modifiche effettuate")
    public void checkPatchResult() {
        purposePatchAssistant.checkPatchOperationResult();
    }

    @When("l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa")
    public void patchReversePurpose() {
        ReversePurposePatchRequest request = reversePurposePatchAssistant.buildDefaultPatchRequest();
        reversePurposePatchAssistant.patchResource(request);
    }

    @When("viene aggiornata la finalità ad erogazione inversa con purposeId {string} e title {string}, description {string}, isFreeOfCharge {string}, freeOfChargeReason {string}, dailyCalls {string}")
    public void patchReversePurposeDraft(
            String purposeId,
            String title,
            String description,
            String isFreeOfCharge,
            String freeOfChargeReason,
            String dailyCalls
    ) {
        UUID purposeIdValue = buildPurposeIdValue(purposeId);

        // default valido
        ReversePurposePatchRequest req = reversePurposePatchAssistant.buildDefaultPatchRequest();

        // override SEMPRE (anche se null/blank/invalid -> diventa null e l'API può rispondere 400)
        req.setTitle(nullOrBlankOrValue(title));
        req.setDescription(nullOrBlankOrValue(description));
        req.setIsFreeOfCharge(nullableBoolean(isFreeOfCharge));
        req.setFreeOfChargeReason(nullOrBlankOrValue(freeOfChargeReason));
        req.setDailyCalls(nullableInteger(dailyCalls));

        // esegui chiamata
        httpCallExecutor.performCall(() -> purposeClient.patchReversePurpose(purposeIdValue, req));
    }


    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa")
    public void patchReversePurposeUsing(String tenant, M2MRole m2mRole) {
        ReversePurposePatchRequest request = reversePurposePatchAssistant.buildDefaultPatchRequest();
        reversePurposePatchAssistant.patchResource(request, tenant, m2mRole);
    }

    @When("l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa con token non valido")
    public void patchReversePurposeWithNotValidToken() {
        ReversePurposePatchRequest patchRequest = reversePurposePatchAssistant.buildDefaultPatchRequest();
        reversePurposePatchAssistant.patchResourceWithInvalidToken(patchRequest);
    }

    @When("l'utente tenta di effettuare la modifica parziale di una finalità ad erogazione inversa inesistente")
    public void patchNonExistentReversePurpose() {
        reversePurposePatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale della finalità dell'e-service ad erogazione inversa specificando un sottoinsieme di informazioni")
    public void patchReversePurposeSubset() {
        ReversePurposePatchRequest request = ReversePurposePatchRequest.builder()
                .title("patched title - " + UUID.randomUUID())
                .build();
        reversePurposePatchAssistant.patchResource(request);
    }

    private void performPurposeAction(PurposeOperation action, EntityIdType entityIdType) {
        UUID id = generateId(entityIdType);
        httpCallExecutor.performCall(() -> {
            Purpose out;
            switch (action) {
                case APPROVE -> out = purposeClient.approvePurpose(id);
                case UNSUSPEND -> out = purposeClient.unsuspendPurpose(id);
                case ARCHIVE -> out = purposeClient.archivePurpose(id);
                default ->
                        throw new UnsupportedOperationException("Not expected purpose action '%s'".formatted(action));
            }
            return out;
        });
    }

    private UUID generateId(EntityIdType entityIdType) {
        if (entityIdType == null) {
            var purposeContext = sharedStepsContext.getPurposeCommonContext();
            List<String> ids = purposeContext.getPurposesIds();

            Assertions.assertThat(ids)
                    .as("La lista degli ID dei purpose deve contenere esattamente un elemento")
                    .hasSize(1);

            String purposeId = ids.get(0);
            return UUID.fromString(purposeId);
        }

        return switch (entityIdType) {
            case NULL_ID -> null;
            case INVALID_ID ->
                    UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }
}
