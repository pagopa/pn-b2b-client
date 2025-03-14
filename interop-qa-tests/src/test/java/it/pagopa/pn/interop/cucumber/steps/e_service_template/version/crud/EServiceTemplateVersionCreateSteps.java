package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateInfoMapper;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi
@Data
public class EServiceTemplateVersionCreateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;
    private final EServiceTemplateInfoMapper templateInfoMapper;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateVersionCreateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext,
        EServiceTemplateInfoMapper templateInfoMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
        this.templateInfoMapper = templateInfoMapper;
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
        createAnotherEServiceTemplateVersion(templateContext.getLastTemplateManaged().id());
    }

    @When("l'utente aggiunge all'e-service template una versione in stato {eServiceTemplateVersionState}")
    public void addEServiceTemplateVersion(EServiceTemplateVersionState state) {
        createAnotherEServiceTemplateVersion(templateContext.getLastTemplateManaged().id());
        testAssistant.mutateLastVersionState(state);
        checkEServiceTemplateVersionCreated();
    }

    @Then("la creazione di una ulteriore versione nell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionCreated() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getVersions().stream().anyMatch(v -> v.getId().equals(templateContext.getLastTemplateManaged().lastVersionId())),
                "La versione dell'e-service template non è stata creata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata creata correttamente");
        }
    }

    private void createAnotherEServiceTemplateVersion(UUID eServiceTemplateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.createEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId),
            ResponseEntity::getStatusCode);

        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            UUID idOfNewVersion = ((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody()
                .getId();
            templateContext.setLastTemplateManaged(this.templateInfoMapper.withVersionId(templateContext.getLastTemplateManaged(), idOfNewVersion));
        }
    }
}
