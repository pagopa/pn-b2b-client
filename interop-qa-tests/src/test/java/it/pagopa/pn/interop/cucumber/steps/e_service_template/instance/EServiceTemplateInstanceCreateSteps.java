package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.HttpStatus;
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

    private InstanceEServiceSeed lastEServiceCreatedFromTemplateSeed;

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
        UUID lastEServiceIdCreatedFromTemplate = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = getDescriptorId(
            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceNameCreatedFromTemplate(),
            EServiceDescriptorState.DRAFT);
        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorIdCreatedFromTemplate(descriptorId);
        this.dataPreparationService.bringTemplateInstanceDescriptorToGivenState(
            lastEServiceIdCreatedFromTemplate,
            descriptorId,
            expectedState,
            false);
    }

    private UUID getDescriptorId(String eServiceName, EServiceDescriptorState state) {
        ResponseEntity<ProducerEServices> producerEServicesWithHttpInfo = eServiceClient.getProducerEServicesWithHttpInfo(
            sharedStepsContext.getXCorrelationId(),
            eServiceName);
        UUID descriptorId;
        int index = producerEServicesWithHttpInfo.getBody().getResults().size() - 1;
        if (state == EServiceDescriptorState.DRAFT) {
            descriptorId = producerEServicesWithHttpInfo.getBody().getResults().get(index)
                .getDraftDescriptor().getId();
        } else {
            descriptorId = producerEServicesWithHttpInfo.getBody().getResults().get(index)
                .getActiveDescriptor().getId();
        }
        return descriptorId;
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
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && !res.getBody().getResults().isEmpty()) {
                        int index = res.getBody().getResults().size() - 1;
                        String name = res.getBody().getResults().get(index).getName();
                        //String instanceLabel = res.getBody().getResults().get(index).getInstanceLabel();
                        //sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceNameCreatedFromTemplate(getInstanceName(name, instanceLabel));
                        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceNameCreatedFromTemplate(name);
                        return expectedState == EServiceDescriptorState.DRAFT || res.getBody().getResults().stream().anyMatch(
                            instance -> instance.getLatestDescriptor().getState() == expectedState);
                    }
                    return false;
                },
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

                if(eServiceCreatedFromTemplate.get().getDescriptors().size() > 1) {
                    throw new IllegalStateException("L'e-service appena creato ha più di un descriptor: ciò rende incerto quale descriptor considerare per le successive operazioni di test");
                }
                if(eServiceCreatedFromTemplate.get().getDescriptors().size() == 1) {
                    this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorCreatedFromTemplate(eServiceCreatedFromTemplate.get().getDescriptors().get(0));
                }

                if(eServiceCreatedFromTemplate.get().getDescriptors().size() == 1) {
                    softly.assertThat(
                            eServiceCreatedFromTemplate)
                        .get()
                        .as("Check stato dell'istanza creata").extracting(EServiceTemplateInstance::getLatestDescriptor)
                        .extracting(CompactDescriptor::getState)
                        .isEqualTo(expectedState);
                } else {
                    softly.assertThat(expectedState)
                        .withFailMessage("La lista di descriptor associata all'istanza è vuota, "
                            + "il che è previsto solo per template in stato %s, quando in questo caso lo stato atteso è %s",
                            EServiceDescriptorState.DRAFT,
                            expectedState)
                        .isEqualTo(EServiceDescriptorState.DRAFT);

                }

                String instanceDefaultName = eServiceSourceTemplate.getEserviceTemplate().getName();
                String instanceLabel = isNull(lastEServiceCreatedFromTemplateSeed)
                    ? null
                    : lastEServiceCreatedFromTemplateSeed.getInstanceLabel();
                softly.assertThat(eServiceCreatedFromTemplate)
                    .get()
                    .extracting(EServiceTemplateInstance::getName)
                    .as("Check correttezza del nome dell'istanza creata")
                    .isEqualTo(getInstanceName(instanceDefaultName, instanceLabel));

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

    private static String getInstanceName(String instanceDefaultName, String instanceLabel) {
        String suffix = nonNull(instanceLabel) ? " " + instanceLabel : "";
        return instanceDefaultName + suffix;
    }

    @Then("il nuovo e-service è stato creato")
    public void checkEServiceCreated() {
        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceClient.getProducerEServiceDetailsWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    this.sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceCreatedFromTemplate().getId()
                ),
                ResponseEntity::getStatusCode),
            res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()),
            "Il nuovo e-service non è stato creato correttamente"
        );
    }

    private void createEServiceFromTemplate(UUID id, InstanceEServiceSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        createEServiceInstance(id, seed);

        /* 409 solitamente indica la presenza di un altro e-service con nome uguale. Qualora ci
        * fosse bisogno di definire una logica più precisa oltre al check del ResponseStatus
        * segue un esempio di body restituito:
        * {"type":"about:blank","title":"Duplicated service name","status":409,"detail":"An EService with name eservice-template-2121364233-883207603 already exists","correlationId":"b58e2950-263d-489d-893c-da92cf01c6fa","errors":[{"code":"007","detail":"An EService with name eservice-template-2121364233-883207603 already exists"}]} */
        if(httpCallExecutor.getResponseStatus().equals(HttpStatus.CONFLICT)) {
            InstanceEServiceSeed newSeed = isNull(seed) ? new InstanceEServiceSeed() : seed;
            newSeed.setInstanceLabel(easyRandom.nextObject(String.class));
            createEServiceInstance(id, newSeed);
            this.lastEServiceCreatedFromTemplateSeed = newSeed;
        } else {
            this.lastEServiceCreatedFromTemplateSeed = seed;
        }


        if(!httpCallExecutor.getResponseStatus().isError()) {
            this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceIdCreatedFromTemplate(((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody().getId());

            // TODO ridondante con il parametro di sopra nel context, sceglierne uno
            this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceCreatedFromTemplate((CreatedResource) ((ResponseEntity<?>) httpCallExecutor.getResponse()).getBody());
        }
    }

    private void createEServiceInstance(UUID id, InstanceEServiceSeed seed) {
        httpCallExecutor.performCall(
            () -> eServiceClient.createEServiceInstanceFromTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                id,
                seed),
            ResponseEntity::getStatusCode);
    }
}
