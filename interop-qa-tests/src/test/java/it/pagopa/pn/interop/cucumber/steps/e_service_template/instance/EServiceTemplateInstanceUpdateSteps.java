package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateInstanceUpdateSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;
    private final IEServiceClient eServiceClient;

    private UpdateEServiceTemplateInstanceSeed lastUpdateEServiceTemplateInstanceSeed;

    public EServiceTemplateInstanceUpdateSteps(DataPreparationService dataPreparationService,
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
    }

    @When("l'utente tenta la modifica dei campi dell'istanza dell'e-service template")
    public void editEServiceInstanceFields() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        lastUpdateEServiceTemplateInstanceSeed = easyRandom.nextObject(
            UpdateEServiceTemplateInstanceSeed.class);
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica dei campi di un'istanza inesistente dell'e-service template")
    public void editNotExistentEServiceInstanceFields() {
        UUID eServiceId = UUID.randomUUID();
        lastUpdateEServiceTemplateInstanceSeed = easyRandom.nextObject(
            UpdateEServiceTemplateInstanceSeed.class);
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica dei campi dell'istanza dell'e-service template indicando una specifica vuota")
    public void editEServiceInstanceUnspecifiedFields() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        lastUpdateEServiceTemplateInstanceSeed = new UpdateEServiceTemplateInstanceSeed();
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @Then("i campi dell'istanza dell'e-service template sono stati modificati correttamente")
    public void checkEServiceInstanceFieldsEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id()),
                    ResponseEntity::getStatusCode),
                res ->
                    res.getStatusCode().is2xxSuccessful() &&
                        nonNull(res.getBody()) &&
                        res.getBody().getResults().stream().anyMatch(instance -> instance.getId().equals(sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate())) &&
                        res.getBody().getResults().stream().anyMatch(instance -> instance.getInstanceLabel().equals(lastUpdateEServiceTemplateInstanceSeed.getInstanceLabel())),
                "L'istanza non è presente nell'elenco delle istanze dell'e-service template oppure non è stata modificata correttamente: non è stato trovato un'istanza con l'id " +
                    sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate() +
                    " o l'istanza non ha l'etichetta " + lastUpdateEServiceTemplateInstanceSeed.getInstanceLabel()
            );

            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getProducerEServiceDetailsWithHttpInfo(
                        sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate()),
                    ResponseEntity::getStatusCode),
                res ->
                    res.getStatusCode().is2xxSuccessful() &&
                        nonNull(res.getBody()) &&
                        this.areConsistent(res.getBody(), lastUpdateEServiceTemplateInstanceSeed),
                "L'istanza non è stata modificata correttamente: uno dei campi dell'istanza - a eccezione di 'instanceLabel' - non è stato modificato correttamente."
            );
        } catch (IllegalArgumentException e) {
            /* DEV. NOTE: non si è lasciato che l’eccezione si propagasse perché l’errore così generato
             * avrebbe suggerito che fosse sopraggiunto un errore imprevisto, quando in realtà
             * rientra tra i possibili flussi esecutivi del test. */
            fail(e.getMessage());
        }
    }

    private boolean areConsistent(ProducerEServiceDetails instanceDetails, UpdateEServiceTemplateInstanceSeed updateSeed) {
        /* Essendo alcuni campi specificati come opzionali al livello API, si tiene conto dei valori NULL
        * interpretando NULL = false attraverso il metodo 'isTrue(...)' */
        return  isTrue(instanceDetails.getIsClientAccessDelegable()) == isTrue(updateSeed.getIsClientAccessDelegable()) &&
                isTrue(instanceDetails.getIsConsumerDelegable()) == isTrue(updateSeed.getIsConsumerDelegable()) &&
                isTrue(instanceDetails.getIsSignalHubEnabled()) == isTrue(updateSeed.getIsSignalHubEnabled());
    }

    @Given("l'utente effettua l'aggiunta di una versione in stato {eServiceDescriptorState} all'e-service con successo")
    public void createEServiceVersionDraftSuccessfully(EServiceDescriptorState descriptorState) {
        UUID newDescriptor = this.dataPreparationService.createNextDraftDescriptor(
            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate());
        this.dataPreparationService.bringTemplateInstanceDescriptorToGivenState(
            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(),
            newDescriptor,
            descriptorState,
            false
        );
        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorIdCreatedFromTemplate(newDescriptor);
    }

    private void editEServiceInstanceFields(UUID eServiceId, UpdateEServiceTemplateInstanceSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.updateEServiceTemplateInstanceByIdWithHttpInfo(
                eServiceId,
                seed),
            ResponseEntity::getStatusCode);
    }
}
