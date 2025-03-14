package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving publishing, suspension and reactivation of E-service template versions */
@Data
public class EServiceTemplateVersionLifecycleSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;

    public EServiceTemplateVersionLifecycleSteps(ClientTokenConfigurator clientTokenConfigurator,
        DataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
    }

    @When("l'utente tenta la pubblicazione della versione dell'e-service template")
    public void publishEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        publishEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la pubblicazione di una versione di un e-service template inesistente")
    public void publishNonExistentEServiceTemplate() {
        publishEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la pubblicazione di una versione di un e-service template indicando un identificativo vuoto")
    public void publishUnspecifiedEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        publishEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), null);
    }

    @When("l'utente tenta la pubblicazione di una versione inesistente di un e-service template")
    public void publishNonExistentEServiceTemplateVersion() {
        publishEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID());
    }

    @Then("la pubblicazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionPublished() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && res.getBody().getState() == EServiceTemplateVersionState.PUBLISHED,
                "La versione dell'e-service template non è stata pubblicata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata pubblicata correttamente");
        }
    }

    @Given("l'utente effettua la sospensione della versione dell'e-service template con successo")
    public void suspendEServiceTemplateVersionSuccessfully() {
        suspendEServiceTemplateVersion();
        checkEServiceTemplateVersionSuspended();
    }

    @When("l'utente tenta la sospensione della versione dell'e-service template")
    public void suspendEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        suspendEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la sospensione della versione dell'e-service template indicando un identificativo vuoto")
    public void suspendUnspecifiedEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        suspendEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), null);
    }


    @When("l'utente tenta la sospensione della versione di un e-service template inesistente")
    public void suspendNonExistentEServiceTemplate() {
        suspendEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la sospensione di una versione inesistente nell'e-service template")
    public void suspendNonExistentEServiceTemplateVersion() {
        suspendEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID());
    }

    @Then("la sospensione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionSuspended() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && res.getBody().getState() == EServiceTemplateVersionState.SUSPENDED,
                "La versione dell'e-service template non è stata sospesa correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata sospesa correttamente");
        }
    }

    @When("l'utente effettua la pubblicazione dell'e-service template")
    public void publishEServiceTemplate() {
        testAssistant.publishEServiceTemplate();
    }

    @When("l'utente effettua la sospensione dell'e-service template")
    public void suspendEServiceTemplate() {
        testAssistant.suspendEServiceTemplate();
    }


    @When("l'utente effettua la riattivazione dell'e-service template")
    public void activateEServiceTemplate() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.activateEServiceTemplate(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId());
    }

    @When("l'utente tenta la riattivazione della versione dell'e-service template")
    public void reactivateEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        reactivateEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la riattivazione della versione dell'e-service template indicando un identificativo vuoto")
    public void reactivateUnspecifiedEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        reactivateEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), null);
    }

    @When("l'utente tenta la riattivazione di una versione di un e-service template inesistente")
    public void reactivateNonExistentEServiceTemplate() {
        reactivateEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la riattivazione di una versione inesistente nell'e-service template")
    public void reactivateNonExistentEServiceTemplateVersion() {
        reactivateEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID());
    }

    // TODO gli step delle classi andrebbero ordinati per Given -> When -> Then, rinominando gli And in modo da rendere chiaro il contesto

    @Then("la riattivazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionReactivated() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && res.getBody().getState() == EServiceTemplateVersionState.PUBLISHED,
                "La versione dell'e-service template non è stata riattivata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata riattivata correttamente");
        }
    }

    @Then("l'e-service template è in stato di {eServiceTemplateVersionState}")
    public void checkEServiceTemplateState(EServiceTemplateVersionState expectedState) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        /* Attende qualora eventuali chiamate precedenti (creazione, pubblicazione, sospensine...)
         * non abbiano ancora completato il proprio corso */
        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceTemplateClient.getEServiceTemplateVersion(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId)),
            res -> res != HttpStatus.NOT_FOUND,
            "There was an error while retrieving the e-service template"
        );

        EServiceTemplateVersionDetails retrievedTemplateVersion = (EServiceTemplateVersionDetails) this.httpCallExecutor.getResponse();
        EServiceTemplateVersionState actualState = retrievedTemplateVersion.getState();

        assertThat(actualState)
            .as("Lo stato dell'e-service template deve corrispondere a quanto atteso dal test")
            .isEqualTo(expectedState);
    }

    private void suspendEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.suspendEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }

    private void publishEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.publishEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId));
    }

    private void reactivateEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.activateEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }
}
