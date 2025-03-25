package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstance;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateInstanceCreateSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;
    private final IEServiceClient eServiceClient;

    private InstanceEServiceSeed lastEServiceCreatedFromTemplate;

    public EServiceTemplateInstanceCreateSteps(DataPreparationService dataPreparationService,
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

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpec() {
        createEServiceFromTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id(), null);
    }

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando tutte le specifiche")
    public void createEServiceFromTemplateFullSpec() {
        InstanceEServiceSeed seed = easyRandom.nextObject(InstanceEServiceSeed.class);
        createEServiceFromTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id(), seed);
    }

    @When("l'utente tenta la creazione di un nuovo e-service indicando un template inesistente")
    public void createEServiceFromNonExistentTemplate() {
        createEServiceFromTemplate(UUID.randomUUID(), null);
    }

    @Given("l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpecSuccessfully() {
        createEServiceFromTemplateMinimalSpec();
        checkEServiceCreated(EServiceDescriptorState.DRAFT);
    }

    // TODO il passo precedente è un sottoinsieme di questo, accorpare per ridurre ambiguità
    // TODO aggiungere una virgola: "[...] a partire dal template con successo, indicando [...]"
    @Given("l'utente effettua la creazione di un nuovo e-service in stato {eServiceDescriptorState} a partire dal template con successo indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpecSuccessfully(EServiceDescriptorState expectedState) {
        createEServiceFromTemplateMinimalSpec();
        checkEServiceCreated(EServiceDescriptorState.DRAFT);

        if(anyNull(sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(), sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorCreatedFromTemplate())) {
            throw new IllegalStateException(("Una o più precondizioni necessarie al mutamento di "
                + "stato dell'e-service non sono rispettate: eServiceId = %s, eServiceDescriptor = %s")
                .formatted(sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(), sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorCreatedFromTemplate()));
        }
        this.dataPreparationService.bringDescriptorToGivenState(
            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate(),
            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorCreatedFromTemplate().getId(),
            expectedState,
            false);
    }

    @Then("il nuovo e-service è stato creato correttamente in stato {eServiceDescriptorState}")
    public void checkEServiceCreated(EServiceDescriptorState expectedState) {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id()
                    ),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && !res.getBody().getResults().isEmpty(),
                "Il nuovo e-service non è stato creato correttamente"
            );

            EServiceTemplateVersionDetails eServiceSourceTemplate = this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id(),
                sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId()).getBody();
            Optional<EServiceTemplateInstance> eServiceCreatedFromTemplate = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody()
                .getResults()
                .stream()
                .filter(instance -> instance.getId().equals(sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate()))
                .findAny();

            assertSoftly(softly -> {
                softly.assertThat(eServiceCreatedFromTemplate)
                    .as("Check esistenza istanza del template")
                    .withFailMessage("Fra le istanze del template non è presente quella appena creata. E' possibile sia avvenuto un errore a monte in fase di creazione dell'istanza, oppure a valle in fase di reperimento delle stesse.")
                    .isPresent();

                if(eServiceCreatedFromTemplate.get().getDescriptors().size() != 1) {
                    throw new IllegalStateException("L'e-service appena creato ha più di un descriptor: ciò rende incerto quale descriptor considerare per le successive operazioni di test");
                }
                this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorCreatedFromTemplate(eServiceCreatedFromTemplate.get().getDescriptors().get(0));

                softly.assertThat(eServiceCreatedFromTemplate)
                    .get()
                    .as("Check stato dell'istanza creata")
                    .extracting(EServiceTemplateInstance::getLatestDescriptor)
                    .extracting(CompactDescriptor::getState)
                    .isEqualTo(expectedState);

                String instanceDefaultName = eServiceSourceTemplate.getEserviceTemplate().getName();
                softly.assertThat(eServiceCreatedFromTemplate)
                    .get()
                    .as("Check correttezza del nome dell'istanza creata")
                    .isEqualTo(isNull(lastEServiceCreatedFromTemplate) || isNull(lastEServiceCreatedFromTemplate.getInstanceLabel())
                        ? instanceDefaultName
                        : "%s %s".formatted(instanceDefaultName, lastEServiceCreatedFromTemplate.getInstanceLabel()));

                /* TODO 10/03/2025: in checkEServiceCreatedFromLatestTemplateVersion (parte del test
                 *   dell'API di upgrade del servizio) è stata usata l'API
                 *   getProducerEServiceDescriptor; verificare se possa essere sufficiente per essere usata
                 *   anche qui, e in tal caso usarla al posto di getEServiceTemplateInstances */

                /* TODO 10/03/2025 sebbene i controlli soprastanti bastino a implementare lo
                    scenario indicato in SRS, sarebbe il caso di verificare che il risultato sia
                    coerente con tutti gli altri parametri del template, nonché con i parametri
                    inseriti nella creazione dell'e-service a partire dal template.
                    Un modo elastico per implementarli potrebbe essere mappare con Mapstruct
                    EServiceTemplateVersionDetails in EServiceTemplateInstance e procedere con
                    un isEqualTo(...), e quindi fare lo stesso mappando EServiceTemplateInstance
                    in InstanceEServiceSeed.
                 */
            });
        } catch (PollingPredicateException e) {
            fail("Il nuovo e-service non è stato creato correttamente");
        }
    }

    private void createEServiceFromTemplate(UUID id, InstanceEServiceSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.createEServiceInstanceFromTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                id,
                seed),
            ResponseEntity::getStatusCode);

        this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceIdCreatedFromTemplate(((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody().getId());
        this.lastEServiceCreatedFromTemplate = seed;
    }
}
