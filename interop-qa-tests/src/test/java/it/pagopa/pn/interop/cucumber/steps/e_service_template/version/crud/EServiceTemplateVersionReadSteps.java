package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
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
public class EServiceTemplateVersionReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;
    private final EServiceTemplateInfoMapper templateInfoMapper;
    private final DescriptorAttributesMapper descriptorAttributesMapper;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateVersionReadSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext,
        EServiceTemplateInfoMapper templateInfoMapper,
        DescriptorAttributesMapper descriptorAttributesMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
        this.templateInfoMapper = templateInfoMapper;
        this.descriptorAttributesMapper = descriptorAttributesMapper;
    }

    @When("l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template")
    public void getEServiceTemplateVersionDetails() {
        getEServiceTemplateVersionDetails(templateContext.getLastTemplateManaged().id(), templateContext.getLastTemplateManaged().lastVersionId());
    }

    @When("l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template indicando un identificativo vuoto")
    public void getUnspecifiedEServiceTemplateVersionDetails() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        getEServiceTemplateVersionDetails(templateContext.getLastTemplateManaged().id(), null);
    }

    @When("l'utente tenta la visualizzazione dei dettagli di una versione di un e-service template inesistente")
    public void getNonExistentEServiceTemplateVersionDetails() {
        getEServiceTemplateVersionDetails(UUID.randomUUID(), UUID.randomUUID());
    }



    private void getEServiceTemplateVersionDetails(UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }

    @Then("i dettagli della versione dell'e-service template sono coerenti con quelli inseriti")
    public void checkEServiceTemplateVersionDetailsConsistent() {
        EServiceTemplateVersionDetails version = ((ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse()).getBody();
        assertThat(areConsistent(this.templateContext.getLastTemplateVersionUpdateSeed(), version))
            .withFailMessage("I dettagli della versione dell'e-service template ottenuti '%s' non sono coerenti con quelli inseriti '%s'", version, this.templateContext.getLastTemplateVersionUpdateSeed())
            .isTrue();
    }

    private boolean areConsistent(UpdateEServiceTemplateVersionSeed lastUpdate, EServiceTemplateVersionDetails retrievedTemplate) {
        DescriptorAttributes descriptorAttributes = retrievedTemplate.getAttributes();
        EServiceTemplateAttributesSeed mappedAttributes = this.descriptorAttributesMapper.mapAttributesToSeeds(
            descriptorAttributes);
        return lastUpdate.getAttributes().equals(mappedAttributes) &&
            lastUpdate.getDescription().equals(retrievedTemplate.getDescription()) &&
            lastUpdate.getAgreementApprovalPolicy().equals(retrievedTemplate.getAgreementApprovalPolicy()) &&
            lastUpdate.getVoucherLifespan().equals(retrievedTemplate.getVoucherLifespan()) &&
            lastUpdate.getDailyCallsTotal().equals(retrievedTemplate.getDailyCallsTotal()) &&
            lastUpdate.getDailyCallsPerConsumer().equals(retrievedTemplate.getDailyCallsPerConsumer());
    }
}
