package it.pagopa.pn.interop.cucumber.steps.e_service_template.document;

import static it.pagopa.pn.interop.cucumber.steps.e_service_template.document.DocumentUpdateStrategy.from;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving documents of E-service templates */
@Data
public class EServiceTemplateDocumentUpdateSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;

    private UpdateEServiceTemplateVersionDocumentSeed lastDocumentUpdateSeed;

    public EServiceTemplateDocumentUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
    }

    @When("l'utente tenta la modifica del documento dell'e-service template")
    public void editDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        lastDocumentUpdateSeed = easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class);
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, sharedStepsContext.getEServiceTemplateStepContext().getLastAddedDocument().id(), lastDocumentUpdateSeed);
    }

    @When("l'utente tenta la modifica del documento dell'e-service template indicando una specifica vuota")
    public void editDocumentWithEmptySpecFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, sharedStepsContext.getEServiceTemplateStepContext().getLastAddedDocument().id(), new UpdateEServiceTemplateVersionDocumentSeed());
    }

    @When("l'utente tenta la modifica di un documento da un e-service template inesistente")
    public void editDocumentFromNonExistentEServiceTemplate() {
        editDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica del documento da una versione inesistente dell'e-service template")
    public void editDocumentFromNonExistentEServiceTemplateVersion() {
        editDocumentFromEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), UUID.randomUUID(), sharedStepsContext.getEServiceTemplateStepContext().getLastAddedDocument().id(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento inesistente nell'e-service template")
    public void editNonExistentDocumentFromEServiceTemplateVersion() {
        editDocumentFromEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(), UUID.randomUUID(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento di tipo {eServiceTemplateDocumentKind} inserendo il nome di un altro documento di tipo {eServiceTemplateDocumentKind}")
    public void editDocumentFromEServiceTemplateVersionWithSameName(EServiceTemplateDocumentKind kind1, EServiceTemplateDocumentKind kind2) {
        DocumentUpdateStrategy updateStrategy = from(kind1, kind2);
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                ResponseEntity::getStatusCode),
            res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && updateStrategy.hasExpectedDocuments(res.getBody()),
            "Condizioni di polling non rispettate. NOTA: questo step prevede l'esistenza di almeno 2 documenti nell'e-service template"
        );

        @SuppressWarnings("unchecked")
        EServiceTemplateVersionDetails templateVersion = ((ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse()).getBody();
        UUID documentId = updateStrategy.getDocumentToUpdate(templateVersion);
        UpdateEServiceTemplateVersionDocumentSeed updateSeed = updateStrategy.buildDocumentUpdateSeed(templateVersion);
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, documentId, updateSeed);
    }

    @When("l'utente tenta la modifica di un documento di tipo {eServiceTemplateDocumentKind}")
    public void editAsyncExchangeCallbackInterfaceFromEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                                eServiceTemplateId,
                                eServiceTemplateVersionId),
                        ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful(), // && nonNull(res.getBody()) && updateStrategy.hasExpectedDocuments(res.getBody()),
                "Condizioni di polling non rispettate. NOTA: questo step prevede l'esistenza di almeno 2 documenti nell'e-service template"
        );

        @SuppressWarnings("unchecked")
        EServiceTemplateVersionDetails templateVersion = ((ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse()).getBody();

        assert templateVersion != null;
        UUID documentId = switch (kind) {
            case INTERFACE -> {
                yield templateVersion.getInterface().getId();
            }
            case ASYNC_EXCHANGE_CALLBACK_INTERFACE -> {
                yield templateVersion.getAsyncExchangeCallbackInterface().getId();
            }
            case DOCUMENT -> {
                yield templateVersion.getDocs().get(0).getId();
            }
            default -> throw new IllegalArgumentException("Unsupported document kind: " + kind);
        };

        UpdateEServiceTemplateVersionDocumentSeed seed = new UpdateEServiceTemplateVersionDocumentSeed();
        seed.setPrettyName("newAsyncExchangeCallbackInterface");
        lastDocumentUpdateSeed = seed;
        editDocumentFromEServiceTemplateVersion(
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId,
                seed
        );
    }

    @Then("la modifica del documento di tipo {eServiceTemplateDocumentKind} dell'e-service template è stata effettuata correttamente")
    public void checkDocumentEditedFromEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        UUID documentId = sharedStepsContext.getEServiceTemplateStepContext().getLastAddedDocument().id();

        try {
            pollingService.makePolling(
                // 05/03/2025 Viene chiamata sola questa API perché l'unica che contiene info utili per la verifica della modifica effettuata
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        Optional<EServiceDoc> foundDoc = getDoc(kind, res.getBody(), documentId);
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

    private Optional<EServiceDoc> getDoc(EServiceTemplateDocumentKind kind, EServiceTemplateVersionDetails version, UUID documentId) {
        return switch (kind) {
            case DOCUMENT -> version.getDocs().stream()
                .filter(d -> d.getId().equals(documentId)).findFirst();
            case INTERFACE -> Optional.ofNullable(version.getInterface());
            case ASYNC_EXCHANGE_CALLBACK_INTERFACE -> Optional.ofNullable(version.getAsyncExchangeCallbackInterface());
            default -> throw new IllegalArgumentException("Unsupported document kind: " + kind);
        };
    }

    private void editDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UUID documentId, UpdateEServiceTemplateVersionDocumentSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateDocumentWithHttpInfo(
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId,
                seed),
            ResponseEntity::getStatusCode);
    }

    private boolean areConsistent(UpdateEServiceTemplateVersionDocumentSeed updateSeed, EServiceDoc doc) {
        return updateSeed.getPrettyName().equals(doc.getPrettyName());
    }
}