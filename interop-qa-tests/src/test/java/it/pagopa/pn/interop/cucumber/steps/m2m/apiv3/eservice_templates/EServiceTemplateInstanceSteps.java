package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservice_templates;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IM2MV3EServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceInstanceLabelUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateEServiceTemplateInstanceDescriptorQuotasSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.instance.EServiceTemplateInstanceUtility;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class EServiceTemplateInstanceSteps {
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final IM2MV3EServiceTemplateClient templateClient;
    private UpdateEServiceTemplateInstanceSeed lastInstanceUpdateSeed;
    private final EServiceTemplateInstanceUtility instanceUtility;
    private EServiceInstanceLabelUpdateSeed lastInstanceLabelUpdateSeed;
    private UUID lastInstanceLabelUpdateEServiceId;
    private UpdateEServiceDescriptorTemplateInstanceSeed lastDescriptorUpdateSeed;
    private UpdateEServiceTemplateInstanceDescriptorQuotasSeed lastDescriptorQuotasUpdateSeed;

    /** Uses the explicit v3 client, authenticated by the existing M2M authentication steps. */
    public EServiceTemplateInstanceSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.templateClient = clientTokenConfigurator.getM2mV3EServiceTemplateClient();
        this.instanceUtility = new EServiceTemplateInstanceUtility(sharedStepsContext);
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

    /** Updates a published instance label through M2M v3 without changing the authentication. */
    @When("l'utente m2m v3 tenta la modifica del campo instanceLabel dell'istanza dell'e-service template {string} in stato PUBLISHED con {string}")
    public void updateEServiceTemplateInstanceLabel(String eserviceId, String instanceLabel) {
        lastInstanceLabelUpdateEServiceId = instanceUtility.resolveEServiceTemplateInstanceId(eserviceId);
        lastInstanceLabelUpdateSeed = new EServiceInstanceLabelUpdateSeed()
            .instanceLabel(instanceUtility.parseSuffix(instanceLabel));
        httpCallExecutor.performCallSavingBodyResponse(
            () -> templateClient.updateEServiceTemplateInstanceLabel(lastInstanceLabelUpdateEServiceId, lastInstanceLabelUpdateSeed));
    }

    /** Checks the returned instance identity, label and resulting name without further API calls. */
    @Then("l'e-service restituito dalla modifica dell'etichetta M2M v3 è coerente con il suffisso richiesto")
    public void checkUpdatedEServiceLabelResponse() {
        EServiceTemplateInfo template = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        assertThat(lastInstanceLabelUpdateSeed).as("Richiesta di modifica dell'etichetta").isNotNull();
        String expectedName = instanceUtility.expectedEServiceInstanceName(
            template.getName(), lastInstanceLabelUpdateSeed.getInstanceLabel());

        assertThat(httpCallExecutor.getResponse())
            .as("E-service restituito dalla modifica dell'etichetta M2M v3")
            .isInstanceOfSatisfying(EService.class, updated -> assertSoftly(softly -> {
                softly.assertThat(updated.getId()).as("ID dell'istanza modificata")
                    .isEqualTo(lastInstanceLabelUpdateEServiceId);
                softly.assertThat(updated.getTemplateId()).as("Template di origine").isEqualTo(template.getId());
                softly.assertThat(updated.getInstanceLabel()).as("Etichetta dell'istanza")
                    .isEqualTo(lastInstanceLabelUpdateSeed.getInstanceLabel());
                softly.assertThat(updated.getName()).as("Nome risultante dell'istanza").isEqualTo(expectedName);
            }));
    }

    /** Updates a draft instance descriptor through M2M v3, preserving its HTTP response. */
    @When("l'utente m2m v3 tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template")
    public void updateDraftDescriptorTemplateInstance() {
        UUID eserviceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        lastDescriptorUpdateSeed = new UpdateEServiceDescriptorTemplateInstanceSeed()
            .addAudienceItem(RandomStringUtils.insecure().nextAlphanumeric(12))
            .dailyCallsPerConsumer(5)
            .dailyCallsTotal(10)
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);
        httpCallExecutor.performCallSavingBodyResponse(
            () -> templateClient.updateDraftDescriptorTemplateInstance(eserviceId, descriptorId, lastDescriptorUpdateSeed));
    }

    /** Checks the returned draft descriptor against the target, template version and request. */
    @Then("il descriptor restituito dalla modifica M2M v3 è coerente con i campi richiesti")
    public void checkUpdatedDraftDescriptorResponse() {
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        UUID templateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        assertThat(lastDescriptorUpdateSeed).as("Richiesta di modifica del descriptor").isNotNull();

        assertThat(httpCallExecutor.getResponse())
            .as("Descriptor restituito dalla modifica M2M v3")
            .isInstanceOfSatisfying(EServiceDescriptor.class, updated -> assertSoftly(softly -> {
                softly.assertThat(updated.getId()).as("ID del descriptor modificato").isEqualTo(descriptorId);
                softly.assertThat(updated.getState()).as("Stato del descriptor").isEqualTo(EServiceDescriptorState.DRAFT);
                softly.assertThat(updated.getTemplateVersionId()).as("Versione del template di origine")
                    .isEqualTo(templateVersionId);
                softly.assertThat(updated.getAudience()).as("Audience")
                    .isEqualTo(lastDescriptorUpdateSeed.getAudience());
                softly.assertThat(updated.getDailyCallsPerConsumer()).as("Soglia giornaliera per fruitore")
                    .isEqualTo(lastDescriptorUpdateSeed.getDailyCallsPerConsumer());
                softly.assertThat(updated.getDailyCallsTotal()).as("Soglia giornaliera totale")
                    .isEqualTo(lastDescriptorUpdateSeed.getDailyCallsTotal());
                softly.assertThat(updated.getAgreementApprovalPolicy()).as("Politica di approvazione")
                    .isEqualTo(lastDescriptorUpdateSeed.getAgreementApprovalPolicy());
            }));
    }

    /** Updates daily call quotas through M2M v3, preserving the HTTP status and body. */
    @When("l'utente m2m v3 tenta la modifica delle soglie di chiamate del descriptor dell'istanza dell'e-service template")
    public void updateTemplateInstanceDescriptorQuotas() {
        UUID eserviceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        lastDescriptorQuotasUpdateSeed = new UpdateEServiceTemplateInstanceDescriptorQuotasSeed()
            .dailyCallsPerConsumer(10)
            .dailyCallsTotal(20);
        httpCallExecutor.performCallSavingBodyResponse(
            () -> templateClient.updateTemplateInstanceDescriptorQuotas(eserviceId, descriptorId, lastDescriptorQuotasUpdateSeed));
    }

    /**
     * Checks the returned descriptor quotas and confirms that its lifecycle state is unchanged.
     *
     * @param expectedState descriptor state established by the BFF preconditions
     */
    @Then("il descriptor restituito dalla modifica delle soglie M2M v3 è coerente con i valori richiesti e resta in stato {eServiceDescriptorState}")
    public void checkUpdatedDescriptorQuotasResponse(
        it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState expectedState
    ) {
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        UUID templateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        assertThat(lastDescriptorQuotasUpdateSeed).as("Richiesta di modifica delle soglie").isNotNull();

        assertThat(httpCallExecutor.getResponse())
            .as("Descriptor restituito dalla modifica delle soglie M2M v3")
            .isInstanceOfSatisfying(EServiceDescriptor.class, updated -> assertSoftly(softly -> {
                softly.assertThat(updated.getId()).as("ID del descriptor modificato").isEqualTo(descriptorId);
                softly.assertThat(updated.getTemplateVersionId()).as("Versione del template di origine")
                    .isEqualTo(templateVersionId);
                softly.assertThat(updated.getState()).as("Stato del descriptor invariato")
                    .isEqualTo(EServiceDescriptorState.fromValue(expectedState.getValue()));
                softly.assertThat(updated.getDailyCallsPerConsumer()).as("Soglia giornaliera per fruitore")
                    .isEqualTo(lastDescriptorQuotasUpdateSeed.getDailyCallsPerConsumer());
                softly.assertThat(updated.getDailyCallsTotal()).as("Soglia giornaliera totale")
                    .isEqualTo(lastDescriptorQuotasUpdateSeed.getDailyCallsTotal());
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
