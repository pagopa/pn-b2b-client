package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateInstanceDescriptorUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;
    private final IEServiceClient eServiceClient;

    private UpdateEServiceDescriptorTemplateInstanceSeed lastUpdateEServiceDescriptorTemplateInstanceSeed;

    public EServiceTemplateInstanceDescriptorUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
    }
    
    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template")
    public void editEServiceTemplateInstanceDescriptor() {
        UUID eServiceTemplateInstanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceTemplateInstanceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorCreatedFromTemplate().getId();

        lastUpdateEServiceDescriptorTemplateInstanceSeed = easyRandom.nextObject(
            UpdateEServiceDescriptorTemplateInstanceSeed.class);
        editEServiceTemplateInstanceDescriptor(eServiceTemplateInstanceId, eServiceTemplateInstanceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica di un descriptor inesistente dell'istanza dell'e-service template")
    public void editNonExistentEServiceTemplateInstanceDescriptor() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = UUID.randomUUID();
        lastUpdateEServiceDescriptorTemplateInstanceSeed = easyRandom.nextObject(UpdateEServiceDescriptorTemplateInstanceSeed.class);
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateInstanceDescriptorWithEmptySpec() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorCreatedFromTemplate().getId();
        UpdateEServiceDescriptorTemplateInstanceSeed emptySeed = new UpdateEServiceDescriptorTemplateInstanceSeed();
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, emptySeed);
    }

    @Then("il descriptor dell'istanza dell'e-service template è stato modificato correttamente")
    public void checkEServiceTemplateInstanceDescriptorEdited() {
        UUID eServiceTemplateInstanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceTemplateInstanceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorCreatedFromTemplate().getId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateInstanceId,
                        eServiceTemplateInstanceDescriptorId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return this.areConsistent(res.getBody(), lastUpdateEServiceDescriptorTemplateInstanceSeed);
                    }
                    return false;
                },
                "Il descriptor dell'istanza dell'e-service template non è stato modificato correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("Il descriptor dell'istanza dell'e-service template non è stato modificato correttamente");
        }
    }
    
    private void editEServiceTemplateInstanceDescriptor(
        UUID eServiceId,
        UUID eServiceDescriptorId,
        UpdateEServiceDescriptorTemplateInstanceSeed seed
    ) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.updateDraftDescriptorTemplateInstanceWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceId,
                eServiceDescriptorId,
                seed),
            ResponseEntity::getStatusCode);
    }

    private boolean areConsistent(ProducerEServiceDescriptor descriptor, UpdateEServiceDescriptorTemplateInstanceSeed seed) {
        return seed.getAudience().equals(descriptor.getAudience()) &&
            seed.getAgreementApprovalPolicy().equals(descriptor.getAgreementApprovalPolicy()) &&
            seed.getDailyCallsPerConsumer().equals(descriptor.getDailyCallsPerConsumer()) &&
            seed.getDailyCallsTotal().equals(descriptor.getDailyCallsTotal());
    }
}
