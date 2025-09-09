package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import java.util.UUID;

public class EserviceTemplateSteps {
    private final SharedStepsContext sharedStepsContext;
    private final M2MDataPreparationService dataPreparationService;
    private final IM2MEServiceTemplateClient m2mEServiceTemplateClient;
    private final DelayService delayService;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;

    public EserviceTemplateSteps(
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService,
        ClientTokenConfigurator clientTokenConfigurator,
        DelayService delayService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.m2mEServiceTemplateClient = clientTokenConfigurator.getM2mEServiceTemplateClient();
        this.delayService = delayService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
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
}
