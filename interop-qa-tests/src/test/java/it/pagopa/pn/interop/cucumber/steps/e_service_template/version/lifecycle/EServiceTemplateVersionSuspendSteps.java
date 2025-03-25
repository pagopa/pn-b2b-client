package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.lifecycle;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving publishing, suspension and reactivation of E-service template versions */
@Data
public class EServiceTemplateVersionSuspendSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;

    public EServiceTemplateVersionSuspendSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
    }

    @Given("l'utente effettua la sospensione della versione dell'e-service template con successo")
    public void suspendEServiceTemplateVersionSuccessfully() {
        suspendEServiceTemplateVersion();
        checkEServiceTemplateVersionSuspended();
    }

    @When("l'utente tenta la sospensione della versione dell'e-service template")
    public void suspendEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId();
        suspendEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la sospensione della versione dell'e-service template indicando un identificativo vuoto")
    public void suspendUnspecifiedEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        suspendEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id(), null);
    }


    @When("l'utente tenta la sospensione della versione di un e-service template inesistente")
    public void suspendNonExistentEServiceTemplate() {
        suspendEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la sospensione di una versione inesistente nell'e-service template")
    public void suspendNonExistentEServiceTemplateVersion() {
        suspendEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id(), UUID.randomUUID());
    }

    @Then("la sospensione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionSuspended() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId();
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

    @When("l'utente effettua la sospensione dell'e-service template")
    public void suspendEServiceTemplate() {
        testAssistant.suspendEServiceTemplate();
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
}