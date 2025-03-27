package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceTemplate;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateInfo;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Contains e-service template steps not inherent to any other specific category */
@Data
public class EServiceTemplateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;

    public EServiceTemplateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext
        ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @When("l'utente tenta la visualizzazione dell'elenco producers degli e-service templates")
    public void getEServiceTemplatesProducers() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getCreatorEServiceTemplates(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("l'elenco producers degli e-service templates contiene i {int} elementi inseriti")
    public void checkEServiceTemplatesProducersCount(int expectedCount) {
        List<ProducerEServiceTemplate> producers = this.getFromProducersBy(sharedStepsContext.getEServiceTemplateStepContext().getTemplatesManaged());
        assertThat(producers).hasSize(expectedCount);
    }

    private List<ProducerEServiceTemplate> getFromProducersBy(List<EServiceTemplateInfo> templatesManaged) {
        return templatesManaged.stream()
            .map(t -> eServiceTemplateClient.getCreatorEServiceTemplates(
                sharedStepsContext.getXCorrelationId(),
                0,
                50,
                t.name()))
            .map(response -> response.getBody().getResults())
            .flatMap(List::stream)
            .toList();
    }

    @When("l'utente tenta la visualizzazione dell'elenco dei creatori di e-service templates attivi")
    public void getActiveEServiceTemplatesCreators() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplateCreators(
                sharedStepsContext.getXCorrelationId(),
                0,
                50,
                null),
            ResponseEntity::getStatusCode);
    }

    @Then("l'ente {string} è presente nell'elenco dei creatori di servizi attivi")
    public void checkActiveEServiceTemplatesCreators(String tenant) {
        List<CompactOrganization> creators = ((ResponseEntity<CompactOrganizations>) httpCallExecutor.getResponse()).getBody().getResults();
        UUID expectedTenantId = sharedStepsContext.getIdentityService().getOrganizationId(tenant);
        assertThat(creators)
            .extracting(CompactOrganization::getId)
            .contains(expectedTenantId);
    }

    private String getUserToken() {
        return requireNonNull(
            sharedStepsContext.getUserToken(),
            "Il token dell'utente non è stato precedentemente impostato");
    }
}
