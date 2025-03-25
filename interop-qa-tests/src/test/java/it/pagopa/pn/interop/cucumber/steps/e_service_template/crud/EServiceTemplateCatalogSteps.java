package it.pagopa.pn.interop.cucumber.steps.e_service_template.crud;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateInfo;
import java.util.List;
import lombok.Data;
import org.assertj.core.api.Condition;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template */
@Data
public class EServiceTemplateCatalogSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateStepContext templateContext;

    public EServiceTemplateCatalogSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateStepContext templateContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.templateContext = templateContext;
    }

    @When("l'utente tenta la visualizzazione del catalogo degli e-service template")
    public void getEServiceTemplatesCatalog() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplatesCatalog(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("sono stati aggiunti esattamente {int} e-service templates in catalogo in stato {eServiceTemplateVersionState}")
    public void checkEServiceTemplatesCatalogContainsElementsInState(int expectedCount, EServiceTemplateVersionState expectedState) {
        /* TODO la precondizione di questo metodo sarebbe che lo status code sia positivo, che il body non sia null e che il catalogo non sia vuoto.
         * Migliorare questo e altri step così che venga sempre fatto un check preventivo, eventualmente aiutandosi
         * con un framework con le Precondition come Google Guava. Spunti: https://www.sw-engineering-candies.com/blog-1/comparison-of-ways-to-check-preconditions-in-java
         */

        List<CatalogEServiceTemplate> templatesInCatalog = this.getFromCatalogBy(templateContext.getTemplatesManaged());
        Condition<CatalogEServiceTemplate> ofExpectedState = new Condition<>(
            template -> template.getPublishedVersion().getState() == expectedState,
            "of state %s", expectedState);
        assertThat(templatesInCatalog)
            .hasSize(expectedCount)
            .are(ofExpectedState);
    }

    /* DEV. NOTE 25/03/2025: recupera tutti gli eservice template esistenti in catalogo
    * indicati in input. Potendo specificare soltanto un nome per volta e potendo specificare un
    * limite di risultati massimo di 50 non c'è altra opzione se non fare n chiamate diverse. */
    private List<CatalogEServiceTemplate> getFromCatalogBy(List<EServiceTemplateInfo> templatesManaged) {
        return templatesManaged.stream()
            .map(t ->
                eServiceTemplateClient.getEServiceTemplatesCatalog(
                    sharedStepsContext.getXCorrelationId(),
                    0,
                    50,
                    t.name(),
                    null))
            .map(response -> response.getBody().getResults())
            .flatMap(List::stream)
            .toList();
    }

}