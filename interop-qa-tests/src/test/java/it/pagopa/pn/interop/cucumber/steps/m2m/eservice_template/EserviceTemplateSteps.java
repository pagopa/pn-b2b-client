package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateDescriptionPatchRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplatePatchRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionCreationRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionPatchRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionQuotasPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.assistant.EServiceTemplatePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.helpers.EServiceTemplateSeedFactory;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.mapper.EServiceTemplateMapper;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionQuotasPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class EserviceTemplateSteps {
    public enum EServiceTemplateVersionsSnapshotType { VECCHIO, NUOVO }

    @ParameterType("vecchio|nuovo")
    public EServiceTemplateVersionsSnapshotType eServiceTemplateVersionsSnapshotType(String type) {
        return EServiceTemplateVersionsSnapshotType.valueOf(type.toUpperCase());
    }

    private final SharedStepsContext sharedStepsContext;
    private final M2MDataPreparationService dataPreparationService;
    private final IM2MEServiceTemplateClient m2mEServiceTemplateClient;
    private final DelayService delayService;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplatePatchOperationsAssistant patchAssistant;
    private final EServiceTemplateVersionPatchOperationsAssistant versionPatchAssistant;
    private final EServiceTemplateVersionQuotasPatchOperationsAssistant versionQuotasPatchAssistant;
    private final EServiceTemplateMapper templateMapper;
    private final EServiceTemplateSeedFactory eServiceTemplateSeedFactory;

    private EServiceTemplateVersions oldVersionsSnapshot;
    private EServiceTemplateVersions newVersionsSnapshot;
    private EServiceTemplateVersionCreationRequest lastVersionCreationRequest;

    public EserviceTemplateSteps(
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService,
        ClientTokenConfigurator clientTokenConfigurator,
        DelayService delayService,
        EServiceTemplatePatchOperationsAssistant patchAssistant,
        EServiceTemplateVersionPatchOperationsAssistant versionPatchAssistant,
        EServiceTemplateVersionQuotasPatchOperationsAssistant versionQuotasPatchAssistant,
        EServiceTemplateMapper templateMapper,
        EServiceTemplateSeedFactory eServiceTemplateSeedFactory
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.m2mEServiceTemplateClient = clientTokenConfigurator.getM2mEServiceTemplateClient();
        this.delayService = delayService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.patchAssistant = patchAssistant;
        this.versionPatchAssistant = versionPatchAssistant;
        this.versionQuotasPatchAssistant = versionQuotasPatchAssistant;
        this.templateMapper = templateMapper;
        this.eServiceTemplateSeedFactory = eServiceTemplateSeedFactory;
    }

    private void setCreatedEServiceTemplateInCommonContext(EServiceTemplate eServiceTemplate) {
        EServiceTemplateInfo eServiceTemplateInfo = new EServiceTemplateInfo(
                eServiceTemplate.getName(),
                eServiceTemplate.getIntendedTarget(),
                eServiceTemplate.getDescription(),
                null,
                null,
                eServiceTemplate.getId(),
                null,
                null
        );
        sharedStepsContext.getEServiceTemplateStepContext().getTemplatesManaged().add(eServiceTemplateInfo);
    }

    @When("l'utente tenta la creazione dell'e-service template con la configurazione predefinita")
    public void createEServiceTemplateWithDefaultConfiguration() {
        EServiceTemplateSeed eServiceTemplateSeed = eServiceTemplateSeedFactory.defaultEServiceTemplateSeed();
        httpCallExecutor.performCall(() -> dataPreparationService.createEServiceTemplate(eServiceTemplateSeed));

        if (httpCallExecutor.getResponseStatus() == HttpStatus.CREATED || httpCallExecutor.getResponseStatus() == HttpStatus.OK) {
            EServiceTemplate eServiceTemplate = (EServiceTemplate) httpCallExecutor.getResponse();
            this.setCreatedEServiceTemplateInCommonContext(eServiceTemplate);
        }
    }

    @When("l'utente tenta la creazione del template e-service con la seguente configurazione:")
    public void createEServiceTemplate(DataTable dataTable) {

        EServiceTemplateSeed eServiceTemplateSeed = eServiceTemplateSeedFactory.defaultEServiceTemplateSeed();

        Map<String, String> data = dataTable.asMap(String.class, String.class);
        if (data.containsKey("description-length")) {
            int descriptionLength = Integer.parseInt(data.get("description-length"));
            eServiceTemplateSeed.description((new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue());
        }

        httpCallExecutor.performCall(() -> dataPreparationService.createEServiceTemplate(eServiceTemplateSeed));

        if (httpCallExecutor.getResponseStatus() == HttpStatus.CREATED || httpCallExecutor.getResponseStatus() == HttpStatus.OK) {
            EServiceTemplate eServiceTemplate = (EServiceTemplate) httpCallExecutor.getResponse();
            this.setCreatedEServiceTemplateInCommonContext(eServiceTemplate);
        }
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template in stato {eServiceTemplateVersionStateM2M} con una descrizione di {int} caratteri")
    public void updateEServiceTemplateDescription(EServiceTemplateVersionState eServiceTemplateVersionState, int descriptionLength) {

        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastTemplateManaged()
                .getId();
        String description = (new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue();

        switch (eServiceTemplateVersionState) {
            case DRAFT -> {
                EServiceTemplatePatchRequest request = this.patchAssistant.buildDefaultPatchRequest();
                request.setDescription(description);
                httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.patchEServiceTemplate(eServiceTemplateId, request));
            }
            case PUBLISHED -> {
                EServiceTemplateDescriptionPatchRequest request = EServiceTemplateDescriptionPatchRequest.builder()
                        .description(description)
                        .build();
                httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.patchEServiceTemplateDescription(eServiceTemplateId, request));
            }
            default -> throw new IllegalArgumentException("L'e-service template deve essere in uno stato valido: DRAFT o PUBLISHED");
        }
    }

    @When("l'utente tenta di recuperare i metadati dei documenti associati all'e-service template")
    public void getTemplateDocumentsMetadata() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getId();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getLastVersionId();
        getDocuments(eServiceTemplateId, versionId);
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di un e-service template inesistente")
    public void getNonExistentTemplateDocumentsMetadata() {
        UUID randomUUID = UUID.randomUUID();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getLastVersionId();
        getDocuments(randomUUID, versionId);
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di una versione di un e-service template inesistente")
    public void getNonExistentTemplateVersionDocumentsMetadata() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getId();
        UUID randomUUID = UUID.randomUUID();
        getDocuments(eServiceTemplateId, randomUUID);
    }

    private void getDocuments(UUID eServiceId, UUID versionId) {
        delayService.delay();
        httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.getDocuments(eServiceId, versionId));
    }

    @When("l'utente tenta di effettuare la riattivazione dell'e-service template")
    public void unsuspendEServiceTemplate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getId();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getLastVersionId();
        unsuspendEServiceTemplate(eServiceTemplateId, versionId);
    }

    @When("l'utente tenta di effettuare la riattivazione di un e-service template inesistente")
    public void unsuspendNonExistentTemplate() {
        UUID randomUUID = UUID.randomUUID();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getLastVersionId();
        unsuspendEServiceTemplate(randomUUID, versionId);
    }

    @When("l'utente tenta di effettuare la riattivazione della versione di un e-service template inesistente")
    public void unsuspendNonExistentTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getId();
        UUID randomUUID = UUID.randomUUID();
        unsuspendEServiceTemplate(eServiceTemplateId, randomUUID);
    }

    private void unsuspendEServiceTemplate(UUID templateId, UUID versionId) {
        delayService.delay();
        httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.unsuspend(templateId, versionId));
    }

    @Then("la versione corrente dell'e-service template è in stato {m2mEServiceTemplateVersionState}")
    public void checkTemplateInState(EServiceTemplateVersionState desiredState) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getId();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .getLastVersionId();
        pollingService.makePolling(
            () -> m2mEServiceTemplateClient.getEserviceTemplateVersion(eServiceTemplateId, versionId),
            eServiceTemplateVersion -> eServiceTemplateVersion.getState().equals(desiredState),
            "Lo stato della versione dell'e-service template non è conforme all'atteso '%s'. Visionare logs per maggiori dettagli.".formatted(desiredState)
            );
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service template")
    public void patchEServiceTemplate() {
        EServiceTemplatePatchRequest request = this.patchAssistant.buildDefaultPatchRequest();
        patchAssistant.patchResource(request);
    }

    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale dell'e-service template")
    public void patchEService(String tenant, M2MRole m2mRole) {
        EServiceTemplatePatchRequest request = this.patchAssistant.buildDefaultPatchRequest();
        patchAssistant.patchResource(request, tenant, m2mRole);
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service template specificando un sottoinsieme di informazioni")
    public void patchEServiceTemplateSubset() {
        String id = RandomStringUtils.insecure().nextAlphanumeric(5);
        EServiceTemplatePatchRequest request = EServiceTemplatePatchRequest.builder()
            .name("minimal patched name - " + id)
            .description("minimal patched descr - " + id)
            .technology(EServiceTechnology.REST)
            .build();
        patchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale di un e-service template inesistente")
    public void patchNonExistentEServiceTemplate() {
        patchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service template con token non valido")
    public void patchEServiceTemplateWithNotValidToken() {
        EServiceTemplatePatchRequest request = patchAssistant.buildDefaultPatchRequest();
        patchAssistant.patchResourceWithInvalidToken(request);
    }

    @Then("l'e-service template restituito è coerente con le modifiche effettuate")
    public void checkEServiceTemplatePatchResult() {
        patchAssistant.checkPatchOperationResult();
    }

    @Then("l'e-service template è stato parzialmente modificato correttamente")
    public void checkEServiceTemplateAfterPatch() {
        patchAssistant.checkPatchedResource();
    }

    @Then("l'e-service template non ha subito modifiche")
    public void checkEServiceTemplateAfterNonPatch() {
        patchAssistant.checkUnpatchedResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template")
    public void patchEServiceTemplateVersion() {
        EServiceTemplateVersionPatchRequest request = this.versionPatchAssistant.buildDefaultPatchRequest();
        versionPatchAssistant.patchResource(request);
    }

    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template")
    public void patchEServiceTemplateVersion(String tenant, M2MRole m2mRole) {
        EServiceTemplateVersionPatchRequest request = this.versionPatchAssistant.buildDefaultPatchRequest();
        versionPatchAssistant.patchResource(request, tenant, m2mRole);
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template specificando un sottoinsieme di informazioni")
    public void patchEServiceTemplateVersionSubset() {
        UUID uuid = UUID.randomUUID();
        EServiceTemplateVersionPatchRequest request = EServiceTemplateVersionPatchRequest.builder()
            .voucherLifespan(new Random().nextInt(60, 10000))
            .description("some minimal patched description - " + uuid)
            .build();
        versionPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale di una versione inesistente di un e-service template inesistente")
    public void patchNonExistentEServiceTemplateVersion() {
        versionPatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template con token non valido")
    public void patchEServiceTemplateVersionWithNotValidToken() {
        EServiceTemplateVersionPatchRequest request = versionPatchAssistant.buildDefaultPatchRequest();
        versionPatchAssistant.patchResourceWithInvalidToken(request);
    }

    @Then("l'ultima versione dell'e-service template restituita è coerente con le modifiche effettuate")
    public void checkEServiceTemplateVersionPatchResult() {
        versionPatchAssistant.checkPatchOperationResult();
    }

    @Then("l'ultima versione dell'e-service template è stata parzialmente modificata correttamente")
    public void checkEServiceTemplateVersionAfterPatch() {
        versionPatchAssistant.checkPatchedResource();
    }

    @Then("l'ultima versione dell'e-service template non ha subito modifiche")
    public void checkEServiceTemplateVersionAfterNonPatch() {
        versionPatchAssistant.checkUnpatchedResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template")
    public void patchEServiceTemplateVersionQuotas() {
        EServiceTemplateVersionQuotasPatchRequest request = this.versionQuotasPatchAssistant.buildDefaultPatchRequest();
        versionQuotasPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template specificando un sottoinsieme di informazioni")
    public void patchEServiceTemplateVersionQuotasSubset() {
        EServiceTemplateVersionQuotasPatchRequest request = EServiceTemplateVersionQuotasPatchRequest.builder()
            .dailyCallsPerConsumer(13)
            .build();
        versionQuotasPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote di una versione inesistente di un e-service template inesistente")
    public void patchNonExistentEServiceTemplateVersionQuotas() {
        versionQuotasPatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale delle quote dell'ultima versione dell'e-service template con token non valido")
    public void patchEServiceTemplateVersionQuotasWithNotValidToken() {
        EServiceTemplateVersionQuotasPatchRequest request = versionQuotasPatchAssistant.buildDefaultPatchRequest();
        versionQuotasPatchAssistant.patchResourceWithInvalidToken(request);
    }

    @When("l'utente tenta di effettuare la cancellazione dell'e-service template")
    public void deleteEServiceTemplate() {
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        deleteEServiceTemplate(templateId);
    }

    @When("l'utente tenta di effettuare la cancellazione di un e-service template inesistente")
    public void deleteNonExistentEServiceTemplate() {
        UUID templateId = UUID.randomUUID();
        deleteEServiceTemplate(templateId);
    }

    private void deleteEServiceTemplate(UUID templateId) {
        httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.deleteEServiceTemplate(templateId));
    }

    @Then("l'e-service template non esiste( più)")
    public void checkEServiceTemplateNotFound() {
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        checkEServiceTemplateExistence(templateId, notFound);
    }

    @Then("l'e-service template esiste( ancora)")
    public void checkEServiceTemplateFound() {
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        HttpStatus notFound = HttpStatus.OK;
        checkEServiceTemplateExistence(templateId, notFound);
    }

    private void checkEServiceTemplateExistence(UUID templateId, HttpStatus notFound) {
        pollingService.makePolling(
            () -> httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.getEserviceTemplate(
                templateId)),
            responseStatus -> responseStatus.equals(notFound),
            "Risultato atteso '%s', ottenuto invece '%s'. Visualizzare logs per maggiori dettagli.".formatted(
                notFound, httpCallExecutor.getResponseStatus())
        );
    }

    /* DEV. NOTE 07/10/2025: questo step ha solo utilità tecnica, per poter permettere futuri
     * confronti attuati negli step successivi. */
    @And("[si prende nota del {eServiceTemplateVersionsSnapshotType} stato delle versioni dell'e-service template]")
    public void getEServiceTemplateVersions(EServiceTemplateVersionsSnapshotType type) {
        delayService.delay();
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        EServiceTemplateVersions eserviceTemplateVersions = m2mEServiceTemplateClient.getEserviceTemplateVersions(
            templateId);
        switch (type) {
            case VECCHIO -> this.oldVersionsSnapshot = eserviceTemplateVersions;
            case NUOVO -> this.newVersionsSnapshot = eserviceTemplateVersions;
            default -> throw new IllegalArgumentException("Non previsto un comportamento per il valore " + type);
        }
    }

    @And("l'utente tenta di recuperare le versioni dell'e-service template")
    public void getEServiceTemplateVersions() {
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        getEServiceTemplateVersion(templateId);
    }

    @And("l'utente tenta di recuperare le versioni dell'e-service template indicando un template id inesistente")
    public void getNonExistentEServiceTemplateVersions() {
        UUID templateId = UUID.randomUUID();
        getEServiceTemplateVersion(templateId);
    }

    private void getEServiceTemplateVersion(UUID templateId) {
        delayService.delay();
        httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.getEserviceTemplateVersions(
                templateId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            this.newVersionsSnapshot = (EServiceTemplateVersions) httpCallExecutor.getResponse();
        }
    }

    @When("l'utente m2m tenta la creazione di una ulteriore versione nell'e-service template")
    public void createEServiceTemplateVersion() {
        EServiceTemplateVersionCreationRequest request = buildVersionCreationRequest();
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        httpCallExecutor.performCallSavingBodyResponse(() -> m2mEServiceTemplateClient.createEserviceTemplateVersion(templateId, request));
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            this.lastVersionCreationRequest = request;
        }
    }

    private static EServiceTemplateVersionCreationRequest buildVersionCreationRequest() {
        return EServiceTemplateVersionCreationRequest.builder()
            .dailyCallsTotal(10)
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            .dailyCallsPerConsumer(5)
            .description("A description for this new version - " + UUID.randomUUID())
            .voucherLifespan(100)
            .build();
    }

    @When("l'utente m2m tenta la creazione di una ulteriore versione di un e-service template inesistente")
    public void createEServiceTemplateVersionInUnexistentTemplate() {
        EServiceTemplateVersionCreationRequest request = buildVersionCreationRequest();
        httpCallExecutor.performCallSavingBodyResponse(
            () -> m2mEServiceTemplateClient.createEserviceTemplateVersion(UUID.randomUUID(), request));
    }

    @Then("la nuova versione dell'e-service template è stata restituita correttamente")
    public void checkReturnedEServiceTemplateVersion() {
        EServiceTemplateVersion returnedVersion = (EServiceTemplateVersion) httpCallExecutor.getResponse();
        Integer previousVersionNumber = getLastVersionNumber(this.oldVersionsSnapshot);
        checkEServiceTemplateVersionConsistency(returnedVersion, this.lastVersionCreationRequest, previousVersionNumber);
    }

    @Then("l'ultima versione dell'e-service template è stata creata correttamente")
    public void checkCreatedEServiceTemplateVersion() {
        EServiceTemplateVersion createdVersion = getLastVersion(this.newVersionsSnapshot);
        Integer previousVersionNumber = getLastVersionNumber(this.oldVersionsSnapshot);

        checkEServiceTemplateVersionConsistency(createdVersion, this.lastVersionCreationRequest, previousVersionNumber);
    }

    private static EServiceTemplateVersion getLastVersion(
        EServiceTemplateVersions oldVersionsSnapshot1) {
        List<EServiceTemplateVersion> versions = oldVersionsSnapshot1.getResults();
        return versions.stream()
            .reduce((a, b) -> a.getVersion() > b.getVersion() ? a : b)
            .orElseThrow(() -> new IllegalStateException("L'ultima versione creata non risulta presente tra quelle ottenute"));
    }

    private static Integer getLastVersionNumber(EServiceTemplateVersions oldVersionsSnapshot1) {
        EServiceTemplateVersion version = getLastVersion(
            oldVersionsSnapshot1);
        return version.getVersion();
    }

    private void checkEServiceTemplateVersionConsistency(
        EServiceTemplateVersion version,
        EServiceTemplateVersionCreationRequest creationRequest,
        Integer previousVersionNumber
    ) {
        EServiceTemplateVersionCreationRequest versionMapped = templateMapper.mapToRequest(
            version);

        assertSoftly(softly -> {
            softly.assertThat(versionMapped)
                .as("Verifica che la versione dell'e-service template sia coerente con le specifiche")
                .isEqualTo(creationRequest);

            EServiceTemplateVersionState expectedState = EServiceTemplateVersionState.DRAFT;
            softly.assertThat(version.getState())
                .as("Verifica che lo stato della versione sia " + expectedState)
                .isEqualTo(expectedState);

            softly.assertThat(version.getVersion())
                .as("Verifica che il numero di versione sia immediatamente successivo a quello della precedente")
                .isEqualTo(previousVersionNumber + 1);
        });
    }

    @Then("la versione {int} dell'e-service template non ha subito modifiche")
    public void checkPreviousEServiceTemplateVersion(int versionNumber) {
        int oldSnapshotSize = oldVersionsSnapshot.getResults().size();
        int newSnapshotSize = newVersionsSnapshot.getResults().size();
        if(oldSnapshotSize < versionNumber || newSnapshotSize < versionNumber) {
            throw new IllegalArgumentException("L'indice di versione indicata eccede una delle "
                + "snapshot a disposizione. Old snapshot size: %d. New snapshot size: %d"
                .formatted(oldSnapshotSize, newSnapshotSize));
        }

        assertThat(getTemplateVersionByVersionNumber(newVersionsSnapshot.getResults(), versionNumber))
            .as("Verifica che la versione di indice %d dell'e-service template non abbia subito modifiche")
            .isEqualTo(getTemplateVersionByVersionNumber(oldVersionsSnapshot.getResults(), versionNumber));
    }

    private EServiceTemplateVersion getTemplateVersionByVersionNumber(List<EServiceTemplateVersion> versions, int versionNumber) {
        return versions.stream()
            .filter(version -> version.getVersion().equals(versionNumber))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("La versione cercata non risulta presente tra quelle ottenute"));
    }

    @Then("le versioni dell'e-service template sono un totale di {int}")
    public void checkEServiceTemplateVersionsQuantity(int versionsQuantity) {
        assertThat(newVersionsSnapshot.getResults())
            .as("Verifica che il numero di versioni totali dell'e-service template sia %d".formatted(versionsQuantity))
            .hasSize(versionsQuantity);
    }
}
