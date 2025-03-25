package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
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
        UUID eServiceId = UUID.randomUUID();
        lastUpdateEServiceTemplateInstanceSeed = new UpdateEServiceTemplateInstanceSeed();
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @Then("i campi dell'istanza dell'e-service template sono stati modificati correttamente")
    public void checkEServiceInstanceFieldsEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id()),
                    ResponseEntity::getStatusCode),
                res ->
                    res.getStatusCode().is2xxSuccessful() &&
                        nonNull(res.getBody()) &&
                        res.getBody().getResults().stream().anyMatch(instance -> instance.getId().equals(sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate())) &&
                        res.getBody().getResults().stream().anyMatch(instance -> instance.getInstanceLabel().equals(lastUpdateEServiceTemplateInstanceSeed.getInstanceLabel())),
                "L'istanza non è presente nell'elenco delle istanze dell'e-service template oppure non è stata modificata correttamente. Visionare i log delle call HTTP per maggiori dettagli."
            );
            /* TODO 12/03/2025 andrebbe effettuato un secondo polling per verificare la coerenza
             *   con i restanti campi di lastUpdateEServiceTemplateInstanceSeed. Rimandato causa
             *   incertezza sulla API da utilizzare. */
        } catch (PollingPredicateException e) {
            /* TODO questo tipo di gestione potrebbe essere di fatto inutile, lasciare che l'eccezione si
             *  propaghi potrebbe portare sostanzialmente allo stesso risultato. Indagare. */
            fail(e.getMessage());
        }
    }

    @Given("l'utente effettua l'aggiunta di una versione in stato {eServiceDescriptorState} all'e-service con successo")
    public void createEServiceVersionDraftSuccessfully(EServiceDescriptorState descriptorState) {
        UUID newDescriptor = this.dataPreparationService.createNextDraftDescriptor(
            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate());
        this.dataPreparationService.bringDescriptorToGivenState(
            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(),
            newDescriptor,
            descriptorState,
            false
        );
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
