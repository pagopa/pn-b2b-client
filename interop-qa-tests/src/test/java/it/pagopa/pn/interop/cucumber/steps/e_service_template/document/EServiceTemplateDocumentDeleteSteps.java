package it.pagopa.pn.interop.cucumber.steps.e_service_template.document;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving documents of E-service templates */
@Data
public class EServiceTemplateDocumentDeleteSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;

    private UpdateEServiceTemplateVersionDocumentSeed lastDocumentUpdateSeed;

    public EServiceTemplateDocumentDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
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

    @Given("l'utente effettua la cancellazione del documento di tipo {eServiceTemplateDocumentKind} dall'e-service template con successo")
    public void deleteDocumentFromEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind) {
        deleteDocumentFromEServiceTemplateVersion();
        checkDocumentDeletedFromEServiceTemplateVersion(kind);
    }

    @When("l'utente tenta la cancellazione del documento dell'e-service template")
    public void deleteDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        UUID documentId = templateContext.getLastAddedDocument().id();
        deleteDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @When("l'utente tenta la cancellazione del documento dell'e-service template indicando un identificato vuoto")
    public void deleteUnspecifiedDocumentFromEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        deleteDocumentFromEServiceTemplateVersion(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId(),
            null);
    }

    @When("l'utente tenta la cancellazione di un documento inesistente nell'e-service template")
    public void deleteNonExistentDocumentFromEServiceTemplateVersion() {
        deleteDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), templateContext.getLastTemplateManaged().lastVersionId(), UUID.randomUUID());
    }

    @When("l'utente tenta la cancellazione del documento da una versione inesistente nell'e-service template")
    public void deleteDocumentFromNonExistentEServiceTemplateVersion() {
        deleteDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), templateContext.getLastAddedDocument().id());
    }

    @When("l'utente tenta la cancellazione di un documento da un e-service template inesistente")
    public void deleteDocumentFromNonExistentEServiceTemplate() {
        deleteDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    @Then("la cancellazione del documento di tipo {eServiceTemplateDocumentKind} dell'e-service template è stata effettuata correttamente")
    public void checkDocumentDeletedFromEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        UUID documentId = templateContext.getLastAddedDocument().id();
        Predicate<EServiceTemplateVersionDetails> noDocument = kind == EServiceTemplateDocumentKind.DOCUMENT
            ? version -> version.getDocs().stream().noneMatch(d -> d.getId().equals(documentId))
            : version -> isNull(version.getInterface()) || isNull(version.getInterface().getId());
        try {
            pollingService.makePolling(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return noDocument.test(res.getBody());
                    }
                    return false;
                },
                "Il documento risulta ancora presente nell'e-service template"
            );
        } catch (PollingPredicateException e) {
            fail("Il documento non è stato cancellato correttamente dalla versione dell'e-service template: " + e.getMessage());
        }
    }

    private void deleteDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.deleteDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId),
            ResponseEntity::getStatusCode);
    }
}