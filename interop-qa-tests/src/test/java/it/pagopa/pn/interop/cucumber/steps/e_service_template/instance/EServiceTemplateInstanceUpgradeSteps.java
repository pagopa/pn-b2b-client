package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateInstanceUpgradeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final IEServiceClient eServiceClient;

    private UUID lastEServiceIdUpdatedFromTemplate;
    private UUID lastEServiceDescriptorIdUpdatedFromTemplate;

    public EServiceTemplateInstanceUpgradeSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
    }

    @When("l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione")
    public void updateEServiceInstanceToLatestVersion() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        upgradeEServiceInstance(eServiceId);
    }

    @When("l'utente tenta l'aggiornamento di un'istanza inesistente dell'e-service template")
    public void updateNonExistentEServiceInstance() {
        upgradeEServiceInstance(UUID.randomUUID());
    }

    @When("l'utente tenta l'aggiornamento di un'istanza dell'e-service template specificando un identificativo vuoto")
    public void updateEmptyEServiceInstance() {
        upgradeEServiceInstance(null);
    }

    @Then("il nuovo e-service riferito all'ultima versione dell'e-service template è stato creato correttamente")
    public void checkEServiceCreatedFromLatestTemplateVersion() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(),
                        lastEServiceDescriptorIdUpdatedFromTemplate
                    ),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()),
                "Il nuovo e-service non è stato aggiornato correttamente"
            );

            @SuppressWarnings("all")
            ProducerEServiceDescriptor eServiceUpdatedDescriptor = ((ResponseEntity<ProducerEServiceDescriptor>) httpCallExecutor.getResponse()).getBody();

            assertSoftly(softly -> {
                softly.assertThat(lastEServiceDescriptorIdUpdatedFromTemplate)
                    .as("Check presenza descriptor associato all'istanza aggiornata")
                    .isEqualTo(eServiceUpdatedDescriptor.getId()); // NPE impossibile, in quanto da condizione di polling il body non può essere null
                softly.assertThat(eServiceUpdatedDescriptor)
                    .as("Check corretto stato dell'istanza aggiornata")
                    .extracting(ProducerEServiceDescriptor::getState)
                    .isEqualTo(EServiceDescriptorState.DRAFT);
            });
        } catch (PollingPredicateException e) {
            fail("Il nuovo e-service non è stato aggiornato correttamente");
        }
    }

    private void upgradeEServiceInstance(UUID uuid) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.upgradeEServiceInstanceWithHttpInfo(
                uuid),
            ResponseEntity::getStatusCode);

        if (!httpCallExecutor.getResponseStatus().isError()) {
            ResponseEntity<CreatedResource> response = (ResponseEntity<CreatedResource>) httpCallExecutor.getResponse();
            this.lastEServiceIdUpdatedFromTemplate = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
            this.lastEServiceDescriptorIdUpdatedFromTemplate = response.getBody().getId();
        }

        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorIdCreatedFromTemplate(
                this.lastEServiceDescriptorIdUpdatedFromTemplate
        );
        sharedStepsContext.getEServicesCommonContext().setOldDescriptorId(
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(this.lastEServiceDescriptorIdUpdatedFromTemplate);
        sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceCreatedFromTemplate().setDescriptorId(
                this.lastEServiceDescriptorIdUpdatedFromTemplate
        );
    }
}
