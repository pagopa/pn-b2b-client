package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplatePatchRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionPatchRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionQuotasPatchRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.assistant.EServiceTemplatePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.assistant.EServiceTemplateVersionQuotasPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import java.util.Random;
import java.util.UUID;

public class EserviceTemplateSteps {
    private final SharedStepsContext sharedStepsContext;
    private final M2MDataPreparationService dataPreparationService;
    private final IM2MEServiceTemplateClient m2mEServiceTemplateClient;
    private final DelayService delayService;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplatePatchOperationsAssistant patchAssistant;
    private final EServiceTemplateVersionPatchOperationsAssistant versionPatchAssistant;
    private final EServiceTemplateVersionQuotasPatchOperationsAssistant versionQuotasPatchAssistant;

    public EserviceTemplateSteps(
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService,
        ClientTokenConfigurator clientTokenConfigurator,
        DelayService delayService,
        EServiceTemplatePatchOperationsAssistant patchAssistant,
        EServiceTemplateVersionPatchOperationsAssistant versionPatchAssistant,
        EServiceTemplateVersionQuotasPatchOperationsAssistant versionQuotasPatchAssistant
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
    }

    @And("viene effettuata la creazione dei template e-service:")
    public void createEserviceTemplate() {
        //
        EServiceTemplateSeed eServiceTemplateSeed = new EServiceTemplateSeed();

        // Esegue le creazione
        CreatedEServiceTemplateVersion version = dataPreparationService.createEServiceTemplate(eServiceTemplateSeed);

        // Aggiorna il context
    }

    @When("l'utente tenta di recuperare i metadati dei documenti associati all'e-service template")
    public void getTemplateDocumentsMetadata() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .id();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .lastVersionId();
        getDocuments(eServiceTemplateId, versionId);
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di un e-service template inesistente")
    public void getNonExistentTemplateDocumentsMetadata() {
        UUID randomUUID = UUID.randomUUID();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .lastVersionId();
        getDocuments(randomUUID, versionId);
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di una versione di un e-service template inesistente")
    public void getNonExistentTemplateVersionDocumentsMetadata() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .id();
        UUID randomUUID = UUID.randomUUID();
        getDocuments(eServiceTemplateId, randomUUID);
    }

    private void getDocuments(UUID eServiceId, UUID descriptorId) {
        delayService.delay();
        httpCallExecutor.performCall(() -> m2mEServiceTemplateClient.getDocuments(eServiceId, descriptorId));
    }

    @When("l'utente tenta di effettuare la riattivazione dell'e-service template")
    public void unsuspendEServiceTemplate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .id();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .lastVersionId();
        unsuspendEServiceTemplate(eServiceTemplateId, versionId);
    }

    @When("l'utente tenta di effettuare la riattivazione di un e-service template inesistente")
    public void unsuspendNonExistentTemplate() {
        UUID randomUUID = UUID.randomUUID();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .lastVersionId();
        unsuspendEServiceTemplate(randomUUID, versionId);
    }

    @When("l'utente tenta di effettuare la riattivazione della versione di une-service template inesistente")
    public void unsuspendNonExistentTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .id();
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
            .id();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .lastVersionId();
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

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service template specificando un sottoinsieme di informazioni")
    public void patchEServiceTemplateSubset() {
        UUID uuid = UUID.randomUUID();
        EServiceTemplatePatchRequest request = EServiceTemplatePatchRequest.builder()
            .name("some minimal patched name - " + uuid)
            .description("some minimal patched description - " + uuid)
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

    @When("l'utente tenta di effettuare la modifica parziale dell'ultima versione dell'e-service template specificando un sottoinsieme di informazioni")
    public void patchEServiceTemplateVersionSubset() {
        UUID uuid = UUID.randomUUID();
        EServiceTemplateVersionPatchRequest request = EServiceTemplateVersionPatchRequest.builder()
            .voucherLifespan(new Random().nextInt(10, 10000))
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
}
