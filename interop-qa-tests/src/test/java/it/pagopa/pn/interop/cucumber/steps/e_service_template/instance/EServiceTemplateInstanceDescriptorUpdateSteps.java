package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateInstanceDescriptorUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;
    private final IEServiceClient eServiceClient;

    private UpdateEServiceDescriptorTemplateInstanceSeed lastUpdateEServiceDescriptorTemplateInstanceSeed;
    private UpdateEServiceTemplateInstanceDescriptorQuotas lastUpdateEServiceDescriptorTemplateInstanceQuotas;

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
    
    @When("l'utente tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template")
    public void editEServiceTemplateInstanceDescriptor() {
        UUID eServiceTemplateInstanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceTemplateInstanceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();

        lastUpdateEServiceDescriptorTemplateInstanceSeed = buildUpdateEServiceDescriptorTemplateInstanceSeed();

        editEServiceTemplateInstanceDescriptor(eServiceTemplateInstanceId, eServiceTemplateInstanceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    private UpdateEServiceDescriptorTemplateInstanceSeed buildUpdateEServiceDescriptorTemplateInstanceSeed() {
        return new UpdateEServiceDescriptorTemplateInstanceSeed()
            .addAudienceItem(easyRandom.nextObject(String.class))
            .dailyCallsPerConsumer(5)
            .dailyCallsTotal(10)
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);
    }

    @When("l'utente tenta la modifica di un descriptor in stato DRAFT inesistente dell'istanza dell'e-service template")
    public void editNonExistentEServiceTemplateInstanceDescriptor() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = UUID.randomUUID();
        lastUpdateEServiceDescriptorTemplateInstanceSeed = buildUpdateEServiceDescriptorTemplateInstanceSeed();
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica del descriptor in stato DRAFT dell'istanza dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateInstanceDescriptorWithEmptySpec() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        UpdateEServiceDescriptorTemplateInstanceSeed emptySeed = new UpdateEServiceDescriptorTemplateInstanceSeed();
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, emptySeed);
    }

    @When("l'utente aggiorna la descrizione dell'e-service template con le seguenti specifiche tecniche relative agli scambi asincroni:")
    public void updateEServiceTemplateWithAsyncTechSpec(AsyncExchangePropertiesInstanceSeed asyncExchangePropertiesInstanceSeed) {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        UpdateEServiceDescriptorTemplateInstanceSeed seed = new UpdateEServiceDescriptorTemplateInstanceSeed();
        seed.asyncExchangeProperties(asyncExchangePropertiesInstanceSeed);
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, seed);
    }

    @Then("il descriptor dell'istanza in stato DRAFT dell'e-service template è stato modificato correttamente")
    public void checkEServiceTemplateInstanceDescriptorInDraftEdited() {
        UUID instanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        Predicate<ProducerEServiceDescriptor> descriptorChecker = res -> this.areConsistent(res, lastUpdateEServiceDescriptorTemplateInstanceSeed);
        checkEServiceTemplateInstanceDescriptor(instanceId, descriptorId, descriptorChecker);
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template")
    public void editEServiceTemplateInstanceDescriptorQuotas() {
        UUID eServiceTemplateInstanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceTemplateInstanceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        lastUpdateEServiceDescriptorTemplateInstanceQuotas = buildUpdateEServiceDescriptorTemplateInstanceQuotas();
        editEServiceTemplateInstanceDescriptor(eServiceTemplateInstanceId, eServiceTemplateInstanceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceQuotas);
    }

    private UpdateEServiceTemplateInstanceDescriptorQuotas buildUpdateEServiceDescriptorTemplateInstanceQuotas() {
        return new UpdateEServiceTemplateInstanceDescriptorQuotas()
            .dailyCallsPerConsumer(10)
            .dailyCallsTotal(20);
    }

    @When("l'utente tenta la modifica di un descriptor inesistente dell'istanza dell'e-service template")
    public void editNonExistentEServiceTemplateInstanceDescriptorQuotas() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = UUID.randomUUID();
        lastUpdateEServiceDescriptorTemplateInstanceQuotas = buildUpdateEServiceDescriptorTemplateInstanceQuotas();
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceQuotas);
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template indicando un 'dailyCallsPerConsumer' maggiore di 'dailyCallsTotal'")
    public void editEServiceTemplateInstanceDescriptorQuotasWithInvalidSpec() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        lastUpdateEServiceDescriptorTemplateInstanceQuotas = buildUpdateEServiceDescriptorTemplateInstanceQuotas()
            .dailyCallsPerConsumer(30)
            .dailyCallsTotal(20);
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceQuotas);
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateInstanceDescriptorWithEmptyQuotasSpec() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID eServiceDescriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        UpdateEServiceTemplateInstanceDescriptorQuotas emptyQuotas = new UpdateEServiceTemplateInstanceDescriptorQuotas();
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, emptyQuotas);
    }

    @Then("il descriptor dell'istanza dell'e-service template è stato modificato correttamente")
    public void checkEServiceTemplateInstanceDescriptorEdited() {
        UUID instanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        Predicate<ProducerEServiceDescriptor> descriptorChecker = res -> this.areConsistent(res, lastUpdateEServiceDescriptorTemplateInstanceQuotas);
        checkEServiceTemplateInstanceDescriptor(instanceId, descriptorId, descriptorChecker);
    }

    private boolean areConsistent(ProducerEServiceDescriptor res,
        UpdateEServiceTemplateInstanceDescriptorQuotas instanceQuotas) {
        return instanceQuotas.getDailyCallsPerConsumer().equals(res.getDailyCallsPerConsumer()) &&
            instanceQuotas.getDailyCallsTotal().equals(res.getDailyCallsTotal());
    }

    private void checkEServiceTemplateInstanceDescriptor(UUID eServiceTemplateInstanceId,
        UUID eServiceTemplateInstanceDescriptorId,
        Predicate<ProducerEServiceDescriptor> descriptorChecker) {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        eServiceTemplateInstanceId,
                        eServiceTemplateInstanceDescriptorId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return descriptorChecker.test(res.getBody());
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
                eServiceId,
                eServiceDescriptorId,
                seed),
            ResponseEntity::getStatusCode);
    }

    private void editEServiceTemplateInstanceDescriptor(
        UUID eServiceId,
        UUID eServiceDescriptorId,
        UpdateEServiceTemplateInstanceDescriptorQuotas seed
    ) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.updateTemplateInstanceDescriptorWithHttpInfo(
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
