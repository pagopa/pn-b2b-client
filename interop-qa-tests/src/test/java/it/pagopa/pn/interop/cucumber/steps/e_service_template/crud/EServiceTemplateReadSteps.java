package it.pagopa.pn.interop.cucumber.steps.e_service_template.crud;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template */
@Data
public class EServiceTemplateReadSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;

    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateReadSteps(ClientTokenConfigurator clientTokenConfigurator,
        DataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
    }

    @When("l'utente tenta la visualizzazione dei dettagli dell'e-service template")
    public void getEServiceTemplateDetails() {
        getEServiceTemplateDetails(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id());
    }

    @When("l'utente tenta la visualizzazione dei dettagli di un e-service template inesistente")
    public void getNonExistentEServiceTemplateDetails() {
        getEServiceTemplateDetails(UUID.randomUUID());
    }

    @When("l'utente tenta la visualizzazione dei dettagli dell'e-service template indicando un identificativo vuoto")
    public void getUnspecifiedEServiceTemplateDetails() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        getEServiceTemplateDetails(null);
    }

    @Then("i dettagli dell'e-service template contengono esattamente {int} versioni")
    public void checkEServiceTemplateDetailsContainVersions(int expectedVersionCount) {
        EServiceTemplateDetails template = ((ResponseEntity<EServiceTemplateDetails>) httpCallExecutor.getResponse()).getBody();
        assertThat(template.getVersions()).hasSize(expectedVersionCount);
    }

    private void getEServiceTemplateDetails(UUID eServiceTemplateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId),
            ResponseEntity::getStatusCode);
    }
}
