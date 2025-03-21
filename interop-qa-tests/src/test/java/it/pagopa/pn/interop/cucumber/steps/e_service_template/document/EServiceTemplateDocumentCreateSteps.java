package it.pagopa.pn.interop.cucumber.steps.e_service_template.document;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/** Cucumber steps involving documents of E-service templates */
@Data
public class EServiceTemplateDocumentCreateSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;

    private UpdateEServiceTemplateVersionDocumentSeed lastDocumentUpdateSeed;

    public EServiceTemplateDocumentCreateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
    }

    @Given("l'utente effettua l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template con successo")
    public void addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersionSuccessfully(kind, 0);
    }

    @Given("l'utente effettua l'aggiunta di un altro documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template con successo")
    public void addAnotherDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersionSuccessfully(kind, 1);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template")
    public void addDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(kind, 0);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template specificando un contenuto vuoto")
    public void addUnspecifiedDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        ByteArrayResource emptyByteArray = new ByteArrayResource(new byte[]{});
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, emptyByteArray);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template specificando lo stesso nome")
    public void addDocumentToEServiceTemplateVersionWithSameName(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        testAssistant.addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, templateContext.getLastAddedDocument().prettyName(), 0);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a un e-service template inesistente")
    public void addDocumentToNonExistentEServiceTemplate(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), kind, 0);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a una versione inesistente dell'e-service template")
    public void addDocumentToNonExistentEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), kind, 0);
    }

    @Then("l'aggiunta del documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template è stata effettuata correttamente")
    public void checkDocumentAddedToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.checkDocumentAddedToEServiceTemplateVersion(kind);
    }

    private void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, Resource resource) {
        testAssistant.addDocumentToEserviceTemplateVersion(
            eServiceTemplateId,
            eServiceTemplateVersionId,
            kind,
            testAssistant.buildPrettyName(kind),
            sharedStepsContext.getUserToken(),
            resource);
    }
}
