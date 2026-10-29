package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservice_templates;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IM2MV3EServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class EServiceTemplateInstanceSteps {
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final IM2MV3EServiceTemplateClient templateClient;
    private UpdateEServiceTemplateInstanceSeed lastInstanceUpdateSeed;

    /** Uses the explicit v3 client, authenticated by the existing M2M authentication steps. */
    public EServiceTemplateInstanceSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.templateClient = clientTokenConfigurator.getM2mV3EServiceTemplateClient();
    }

    /** Creates an instance with the required request body and no optional configuration. */
    @When("l'utente m2m v3 tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpec() {
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        httpCallExecutor.performCallSavingBodyResponse(
            () -> templateClient.createEServiceInstanceFromTemplate(templateId, new InstanceEServiceSeed()));
    }

    /** Updates the instance flags through M2M v3 and preserves the HTTP status and body. */
    @When("l'utente m2m v3 tenta la modifica dei campi dell'istanza dell'e-service template")
    public void updateEServiceTemplateInstance() {
        UUID eserviceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        lastInstanceUpdateSeed = new UpdateEServiceTemplateInstanceSeed()
            .isClientAccessDelegable(true)
            .isConsumerDelegable(true)
            .isSignalHubEnabled(true);
        httpCallExecutor.performCallSavingBodyResponse(
            () -> templateClient.updateEServiceTemplateInstance(eserviceId, lastInstanceUpdateSeed));
    }

    /** Checks the update response against the target instance and the submitted flags. */
    @Then("l'e-service restituito dalla modifica M2M v3 è coerente con i campi richiesti")
    public void checkUpdatedEServiceResponse() {
        UUID eserviceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        assertThat(lastInstanceUpdateSeed).as("Richiesta di modifica dell'istanza").isNotNull();

        assertThat(httpCallExecutor.getResponse())
            .as("E-service restituito dalla modifica M2M v3")
            .isInstanceOfSatisfying(EService.class, updated -> assertSoftly(softly -> {
                softly.assertThat(updated.getId()).as("ID dell'istanza modificata").isEqualTo(eserviceId);
                softly.assertThat(updated.getTemplateId()).as("Template di origine").isEqualTo(templateId);
                softly.assertThat(updated.getIsClientAccessDelegable()).as("Delega tecnica")
                    .isEqualTo(lastInstanceUpdateSeed.getIsClientAccessDelegable());
                softly.assertThat(updated.getIsConsumerDelegable()).as("Delega amministrativa")
                    .isEqualTo(lastInstanceUpdateSeed.getIsConsumerDelegable());
                softly.assertThat(updated.getIsSignalHubEnabled()).as("Signal Hub")
                    .isEqualTo(lastInstanceUpdateSeed.getIsSignalHubEnabled());
            }));
    }

    /** Checks the creation response against the BFF setup without performing further API calls. */
    @Then("l'e-service restituito dalla creazione M2M v3 è coerente con il template di origine")
    public void checkCreatedEServiceResponse() {
        EServiceTemplateInfo template = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID producerId = sharedStepsContext.getIdentityService().getOrganizationId(sharedStepsContext.getTenantType());

        assertThat(httpCallExecutor.getResponse())
            .as("E-service restituito dalla creazione M2M v3")
            .isInstanceOfSatisfying(EService.class, created -> assertSoftly(softly -> {
                softly.assertThat(created.getId()).as("ID dell'e-service creato").isNotNull();
                softly.assertThat(created.getProducerId()).as("Ente erogatore").isEqualTo(producerId);
                softly.assertThat(created.getTemplateId()).as("Template di origine").isEqualTo(template.getId());
                softly.assertThat(created.getName()).as("Nome senza suffisso").isEqualTo(template.getName());
                softly.assertThat(created.getDescription()).as("Descrizione ereditata")
                    .isEqualTo(template.getEServiceDescription());
                softly.assertThat(created.getTechnology()).as("Tecnologia ereditata")
                    .isEqualTo(EServiceTechnology.fromValue(template.getTechnology().getValue()));
                softly.assertThat(created.getMode()).as("Modalita ereditata")
                    .isEqualTo(EServiceMode.fromValue(template.getMode().getValue()));
            }));
    }
}
