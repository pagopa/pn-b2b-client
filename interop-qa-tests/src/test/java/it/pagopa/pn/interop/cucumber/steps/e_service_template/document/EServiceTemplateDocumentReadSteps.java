package it.pagopa.pn.interop.cucumber.steps.e_service_template.document;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving documents of E-service templates */
@Data
public class EServiceTemplateDocumentReadSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IM2MEServiceTemplateClient m2mEServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final DelayService delayService;

    private UpdateEServiceTemplateVersionDocumentSeed lastDocumentUpdateSeed;

    public EServiceTemplateDocumentReadSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        DelayService delayService
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.m2mEServiceTemplateClient = clientTokenConfigurator.getM2mEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.delayService = delayService;
    }

    @When("l'utente tenta il reperimento del documento dalla versione dell'e-service template")
    public void getDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId();
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, sharedStepsContext.getEServiceTemplateStepContext().getLastAddedDocument().id());
    }

    @When("l'utente tenta il reperimento del documento dalla versione dell'e-service template indicando un identificativo vuoto")
    public void getUnspecifiedDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId();

        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, null);
    }

    @When("l'utente tenta il reperimento di un documento da un e-service template inesistente")
    public void getDocumentFromNonExistentEServiceTemplate() {
        getDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta il reperimento di un documento inesistente dalla versione dell'e-service template")
    public void getNonExistentDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId();
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, UUID.randomUUID());
    }

    @When("l'utente tenta di recuperare i metadati dei documenti associati all'e-service template")
    public void getDocumentsMetadata() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .id();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .lastVersionId();
        getDocuments(eServiceTemplateId, versionId);
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di un e-service template inesistente")
    public void getNonExistentEServiceDocumentsMetadata() {
        UUID randomUUID = UUID.randomUUID();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged()
            .lastVersionId();
        getDocuments(randomUUID, versionId);
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di una versione di un e-service template inesistente")
    public void getNonExistentDescriptorDocumentsMetadata() {
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

    private void getDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UUID documentId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getDocumentWithHttpInfo(
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId),
            ResponseEntity::getStatusCode);
    }
}