package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi
@Data
public class EServiceTemplateVersionCreateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateVersionCreateSteps(ClientTokenConfigurator clientTokenConfigurator,
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

    @Given("l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo")
    public void createAnotherEServiceTemplateVersionSuccessfully() {
        createAnotherEServiceTemplateVersion();
        checkEServiceTemplateVersionCreated();
    }

    @When("l'utente tenta la creazione di una ulteriore versione in un e-service template inesistente")
    public void createAnotherEServiceTemplateVersionInNonExistentEServiceTemplate() {
        createAnotherEServiceTemplateVersion(UUID.randomUUID());
    }

    @When("l'utente tenta la creazione di una ulteriore versione nell'e-service template")
    public void createAnotherEServiceTemplateVersion() {
        createAnotherEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId());
    }

    @When("l'utente aggiunge all'e-service template una versione in stato {eServiceTemplateVersionState} con successo")
    public void addEServiceTemplateVersion(EServiceTemplateVersionState state) {
        createAnotherEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId());
        checkEServiceTemplateVersionCreated();
        testAssistant.mutateLastVersionState(state);
        checkEServiceTemplateVersionCreated(state);
    }

    @Then("la creazione di una ulteriore versione nell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionCreated() {
        testAssistant.checkEServiceTemplateVersion(
                x -> true,
                "La versione dell'e-service template non è stata creata correttamente"
        );
    }

    @Then("la creazione di una ulteriore versione in stato {eServiceTemplateVersionState} nell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionCreated(EServiceTemplateVersionState state) {
        testAssistant.checkEServiceTemplateVersion(version -> version.getState().equals(state), "La versione dell'e-service template non è stata creata correttamente in stato " + state);
    }

    private void createAnotherEServiceTemplateVersion(UUID eServiceTemplateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.createEServiceTemplateVersionWithHttpInfo(
                eServiceTemplateId),
            ResponseEntity::getStatusCode);

        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged();
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            UUID idOfNewVersion = ((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody()
                .getId();
            sharedStepsContext.getEServiceTemplateStepContext().addTemplateManaged(
                lastTemplateManaged.withLastVersionId(idOfNewVersion));
        } else {
            sharedStepsContext.getEServiceTemplateStepContext().addTemplateManaged(
                lastTemplateManaged.withLastVersionId(null));
        }
    }
}
