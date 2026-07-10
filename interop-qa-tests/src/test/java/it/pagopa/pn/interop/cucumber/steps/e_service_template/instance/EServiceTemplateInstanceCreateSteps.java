package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.nullableBoolean;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Cucumber steps involving quotas of E-service templates
 */
@Data
@Slf4j(topic = "EServiceTemplateInstanceCreateSteps")
public class EServiceTemplateInstanceCreateSteps {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;
    private final IEServiceClient eServiceClient;
    private final EServiceTemplateInstanceUtility eServiceTemplateInstanceUtility;
    private InstanceEServiceSeed lastEServiceCreatedFromTemplateSeed;
    private String instanceLabel;

    public EServiceTemplateInstanceCreateSteps(BFFDataPreparationService dataPreparationService,
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
        this.eServiceTemplateInstanceUtility = new EServiceTemplateInstanceUtility(this.sharedStepsContext);
    }

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpec() {
        createEServiceFromTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), null);
    }

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando tutte le specifiche")
    public void createEServiceFromTemplateFullSpec() {
        instanceLabel = RandomStringUtils.insecure().nextAlphanumeric(12);
        InstanceEServiceSeed seed = new InstanceEServiceSeed()
                .isClientAccessDelegable(true)
                .isConsumerDelegable(true)
                .isSignalHubEnabled(false)
                .instanceLabel(instanceLabel);
        createEServiceFromTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), seed);
    }

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando specifiche non permesse")
    public void createEServiceFromTemplateWrongSpec() {
        instanceLabel = RandomStringUtils.insecure().nextAlphanumeric(12);
        InstanceEServiceSeed seed = new InstanceEServiceSeed()
                .isClientAccessDelegable(true)
                .isConsumerDelegable(false)
                .isSignalHubEnabled(false)
                .instanceLabel(instanceLabel);
        createEServiceFromTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), seed);
    }

    @When("l'utente tenta la creazione di un nuovo e-service con suffisso {string} a partire dal template indicando tutte le specifiche")
    public void createEServiceFromTemplateFullSpecWithSuffix(String suffix) {
        instanceLabel = eServiceTemplateInstanceUtility.parseSuffix(suffix);
        InstanceEServiceSeed seed = new InstanceEServiceSeed()
                .isClientAccessDelegable(true)
                .isConsumerDelegable(true)
                .isSignalHubEnabled(false)
                .instanceLabel(instanceLabel);
        createEServiceFromTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), seed);
    }

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando tutte le specifiche e impostando delega amministrativa a {string} e delega tecnica a {string}")
    public void createEServiceFromTemplateFullSpecSpecifyingConsumerDelegationFlags(String isConsumerDelegable, String isClientAccessDelegable) {
        instanceLabel = RandomStringUtils.insecure().nextAlphanumeric(12);

        InstanceEServiceSeed seed = new InstanceEServiceSeed()
                .isClientAccessDelegable(nullableBoolean(isClientAccessDelegable))
                .isConsumerDelegable(nullableBoolean(isConsumerDelegable))
                .isSignalHubEnabled(false)
                .instanceLabel(instanceLabel);
        createEServiceFromTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), seed);
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

    @Given("l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie e impostando l'e-service come asincrono e con:")
    public void createEServiceFromTemplateMinimalSpecSuccessfullyAndNonAsynchronous(AsyncExchangePropertiesInstanceSeed asyncExchangePropertiesInstanceSeed) {
//        createEServiceFromTemplate(
//                sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
//                new InstanceEServiceSeed().asyncExchangeProperties(asyncExchangePropertiesInstanceSeed)
//        );
    }

    @Given("l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando tutte le specifiche")
    public void createEServiceFromTemplateFullSpecSuccessfully() {
        createEServiceFromTemplateFullSpec();
        checkEServiceCreated(EServiceDescriptorState.DRAFT);
    }

    // TODO il passo precedente è un sottoinsieme di questo, accorpare per ridurre ambiguità
    // TODO aggiungere una virgola: "[...] a partire dal template con successo, indicando [...]"
    @Given("l'utente effettua la creazione di un nuovo e-service in stato {eServiceDescriptorState} a partire dal template con successo indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpecSuccessfully(EServiceDescriptorState expectedState) {
        createEServiceFromTemplateMinimalSpec();
        checkEServiceAndMutateState(expectedState);
    }

    @Given("l'utente effettua la creazione di un nuovo e-service in stato {eServiceDescriptorState} con suffisso {string} a partire dal template con successo indicando tutte le specifiche")
    public void createEServiceFromTemplateWithSuffixSuccessfully(EServiceDescriptorState expectedState, String suffix) {
        createEServiceFromTemplateFullSpecWithSuffix(suffix);
        checkEServiceAndMutateState(expectedState);
    }

    @Given("l'utente effettua la creazione di un nuovo e-service in stato {eServiceDescriptorState} partire dal template e impostando delega amministrativa a {string} e delega tecnica a {string}")
    public void createEServiceFromTemplateFullSpecSuccessfullySpecifyingConsumerDelegationFlags(EServiceDescriptorState expectedState, String isConsumerDelegable, String isClientAccessDelegable) {
        createEServiceFromTemplateFullSpecSpecifyingConsumerDelegationFlags(isConsumerDelegable, isClientAccessDelegable);
        checkEServiceAndMutateState(expectedState);
    }

    private void checkEServiceAndMutateState(EServiceDescriptorState expectedState) {
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
                                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId()
                            ),
                            ResponseEntity::getStatusCode),
                    res -> {
                        if (res.getStatusCode().is2xxSuccessful() && !res.getBody().getResults().isEmpty()) {
                            int index = res.getBody().getResults().size() - 1;
                            String name = res.getBody().getResults().get(index).getName();
                            sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceNameCreatedFromTemplate(name);
                            return expectedState == EServiceDescriptorState.DRAFT || res.getBody().getResults().stream().anyMatch(
                                    instance -> instance.getLatestDescriptor().getState() == expectedState);
                        }
                        return false;
                    },
                    "Il nuovo e-service non è stato creato correttamente"
            );

            EServiceTemplateVersionDetails eServiceSourceTemplate = this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId()).getBody();
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

                if (eServiceCreatedFromTemplate.get().getDescriptors().size() > 1) {
                    throw new IllegalStateException("L'e-service appena creato ha più di un descriptor: ciò rende incerto quale descriptor considerare per le successive operazioni di test");
                }
                if (eServiceCreatedFromTemplate.get().getDescriptors().size() == 1) {
                    this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorCreatedFromTemplate(eServiceCreatedFromTemplate.get().getDescriptors().get(0));
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

                String templateName = eServiceSourceTemplate.getEserviceTemplate().getName();
                String instanceDefaultName = expectedEServiceInstanceName(templateName, instanceLabel);
                softly.assertThat(eServiceCreatedFromTemplate)
                        .get()
                        .extracting(EServiceTemplateInstance::getName)
                        .as("Check correttezza del nome dell'istanza creata")
                        .isEqualTo(instanceDefaultName);

                sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceCreatedFromTemplate.get().getId());
                sharedStepsContext.getEServicesCommonContext().setDescriptorId(
                        this.getDescriptorId(eServiceCreatedFromTemplate.get().getName(), expectedState)
                );

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
        } catch (IllegalArgumentException e) {
            fail("Il nuovo e-service non è stato creato correttamente");
        }
    }

    @Then("il suffisso {string} è utilizzato correttamente nell'e-service")
    public void checkEServiceName(String suffix) {
        String templateEServiceName = this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
                sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId()
        ).getBody().getEserviceTemplate().getName();

        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();

        String expectedEServiceInstanceName = this.expectedEServiceInstanceName(templateEServiceName, suffix);

        pollingService.makePolling(
                () -> eServiceClient.getProducerEServiceDetailsWithHttpInfo(eServiceId),
                res -> nonNull(res.getBody()) && res.getBody().getName().equals(expectedEServiceInstanceName),
                res -> "Il suffisso dell'istanza non è stato aggiornato correttamente: atteso suffisso '%s', ma il nome completo ottenuto è '%s'".formatted(suffix, res.getBody().getName())
        ).getBody().getName();
    }

    @Then("il nome del {string} e-service creato è stato aggiornato correttamente con il nome dell'e-service template e con il suffisso {string}")
    public void checkEServiceNameUpdated(String position, String suffix) {

        String templateEServiceName = this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
                sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId()
        ).getBody().getEserviceTemplate().getName();

        int index = switch (position.toLowerCase()) {
            case "ultimo" -> 0;
            case "penultimo" -> 1;
            default -> throw new IllegalArgumentException("Invalid position: " + position);
        };

        String expectedEServiceInstanceName = this.expectedEServiceInstanceName(templateEServiceName, suffix);
        pollingService.makePolling(
                () -> this.eServiceClient.getProducerEServiceDetailsWithHttpInfo(
                        sharedStepsContext.getEServiceTemplateStepContext().getEServiceCreatedFromTemplateWithIndex(index).getId()
                ),
                res -> nonNull(res.getBody()) && res.getBody().getName().equals(expectedEServiceInstanceName),
                res -> "Il suffisso dell'istanza non è stato aggiornato correttamente: atteso suffisso '%s', ma il nome completo ottenuto è '%s'".formatted(suffix, res.getBody().getName())
        ).getBody().getName();
    }

    /* DEV. NOTE: step usato temporaneamente in sostituzione di
     * "il nuovo e-service è stato creato correttamente in stato DRAFT" a causa di un bug che
     * ne impediva l'utilizzo (ticket https://pagopa.atlassian.net/browse/PIN-6500), ora risolto. */
    @Deprecated
    @Then("il nuovo e-service è stato creato")
    public void checkEServiceCreated() {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> eServiceClient.getProducerEServiceDetailsWithHttpInfo(
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
        this.lastEServiceCreatedFromTemplateSeed = seed;

        if (!httpCallExecutor.getResponseStatus().isError()) {
            this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceIdCreatedFromTemplate(((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody().getId());

            // TODO ridondante con il parametro di sopra nel context, sceglierne uno
            this.sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceCreatedFromTemplate((CreatedResource) ((ResponseEntity<?>) httpCallExecutor.getResponse()).getBody());
        }
    }

    private void createEServiceInstance(UUID id, InstanceEServiceSeed seed) {
        httpCallExecutor.performCall(
                () -> eServiceClient.createEServiceInstanceFromTemplateWithHttpInfo(
                        id,
                        seed),
                ResponseEntity::getStatusCode);
    }

    private String expectedEServiceInstanceName(String templateEServiceName, String suffix) {
        String parsedSuffix = eServiceTemplateInstanceUtility.parseSuffix(suffix);
        return templateEServiceName + (
                parsedSuffix == null || parsedSuffix.trim().isEmpty() ? "" : " - " + parsedSuffix.trim()
        );
    }
}
