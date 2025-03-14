package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving documents of E-service templates */
@Data
public class EServiceTemplateDocumentSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;
    private final EasyRandom easyRandom;

    private UpdateEServiceTemplateVersionDocumentSeed lastDocumentUpdateSeed;

    public EServiceTemplateDocumentSteps(ClientTokenConfigurator clientTokenConfigurator,
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
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
    }

    @Given("l'utente effettua l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template con successo")
    public void addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersionSuccessfully(kind);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template")
    public void addDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(kind);
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
        testAssistant.addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, templateContext.getLastAddedDocument().prettyName());
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a un e-service template inesistente")
    public void addDocumentToNonExistentEServiceTemplate(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), kind);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a una versione inesistente dell'e-service template")
    public void addDocumentToNonExistentEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), kind);
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

    @When("l'utente tenta il reperimento del documento dalla versione dell'e-service template")
    public void getDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, templateContext.getLastAddedDocument().id());
    }

    @When("l'utente tenta il reperimento del documento dalla versione dell'e-service template indicando un identificativo vuoto")
    public void getUnspecifiedDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, null);
    }

    @When("l'utente tenta il reperimento di un documento da un e-service template inesistente")
    public void getDocumentFromNonExistentEServiceTemplate() {
        getDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), templateContext.getLastAddedDocument().id());
    }

    @When("l'utente tenta il reperimento di un documento inesistente dalla versione dell'e-service template")
    public void getNonExistentDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, UUID.randomUUID());
    }

    private void getDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UUID documentId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la modifica del documento dell'e-service template")
    public void editDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        lastDocumentUpdateSeed = easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class);
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, templateContext.getLastAddedDocument().id(), lastDocumentUpdateSeed);
    }

    @When("l'utente tenta la modifica del documento dell'e-service template indicando una specifica vuota")
    public void editDocumentWithEmptySpecFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, templateContext.getLastAddedDocument().id(), new UpdateEServiceTemplateVersionDocumentSeed());
    }

    @When("l'utente tenta la modifica di un documento da un e-service template inesistente")
    public void editDocumentFromNonExistentEServiceTemplate() {
        editDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica del documento da una versione inesistente dell'e-service template")
    public void editDocumentFromNonExistentEServiceTemplateVersion() {
        editDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), templateContext.getLastAddedDocument().id(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento inesistente nell'e-service template")
    public void editNonExistentDocumentFromEServiceTemplateVersion() {
        editDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), templateContext.getLastTemplateManaged().lastVersionId(), UUID.randomUUID(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento inserendo il nome di un altro documento")
    public void editDocumentFromEServiceTemplateVersionWithSameName() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                ResponseEntity::getStatusCode),
            res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getDocs().size() >= 2,
            "Condizioni di polling non rispettate. NOTA: questo step prevede l'esistenza di almeno 2 documenti nell'e-service template"
        );

        @SuppressWarnings("unchecked, DataFlowIssue")
        List<EServiceDoc> docs = ((ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse()).getBody().getDocs();

        UUID documentId = docs.get(0).getId();
        UpdateEServiceTemplateVersionDocumentSeed updateSeed = easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class)
            .prettyName(docs.get(1).getPrettyName());
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, documentId, updateSeed);
    }

    @Then("la modifica del documento dell'e-service template è stata effettuata correttamente")
    public void checkDocumentEditedFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        UUID documentId = templateContext.getLastAddedDocument().id();

        try {
            pollingService.makePolling(
                // 05/03/2025 Viene chiamata sola questa API perché l'unica che contiene info utili per la verifica della modifica effettuata
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        Optional<EServiceDoc> foundDoc = res.getBody().getDocs().stream()
                            .filter(d -> d.getId().equals(documentId)).findFirst();
                        return foundDoc.isPresent() && this.areConsistent(lastDocumentUpdateSeed, foundDoc.get());
                    }
                    return false;
                },
                "Lo stato del documento restituito dalla API GET dei documenti non corrisponde a quello atteso"
            );
        } catch (PollingPredicateException e) {
            fail("Il documento non è stato modificato correttamente dalla versione dell'e-service template: " + e.getMessage());
        }
    }

    private void editDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UUID documentId, UpdateEServiceTemplateVersionDocumentSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId,
                seed),
            ResponseEntity::getStatusCode);
    }

    @Given("l'utente effettua la cancellazione del documento dall'e-service template con successo")
    public void deleteDocumentFromEServiceTemplateVersionSuccessfully() {
        deleteDocumentFromEServiceTemplateVersion();
        checkDocumentDeletedFromEServiceTemplateVersion();
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

    @Then("la cancellazione del documento dell'e-service template è stata effettuata correttamente")
    public void checkDocumentDeletedFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        UUID documentId = templateContext.getLastAddedDocument().id();

        try {
            pollingService.makePolling(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return res.getBody().getDocs().stream().noneMatch(d -> d.getId().equals(documentId));
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

    private boolean areConsistent(UpdateEServiceTemplateVersionDocumentSeed updateSeed, EServiceDoc doc) {
        return updateSeed.getPrettyName().equals(doc.getPrettyName());
    }
}
