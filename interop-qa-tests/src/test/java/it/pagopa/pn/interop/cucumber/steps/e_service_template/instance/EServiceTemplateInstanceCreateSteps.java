package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.java.en.And;
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
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

import java.util.List;
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

    @Given("l'utente crea una nuova versione dell'istanza del template con successo")
    public void createNewTemplateInstanceVersionSuccessfully() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID newDescriptorId = this.dataPreparationService.createNextDraftDescriptor(eServiceId);
        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorIdCreatedFromTemplate(newDescriptorId);

        checkEServiceAndMutateState(EServiceDescriptorState.DRAFT);
    }

    @Given("l'utente specifica i metadati mancanti all'istanza del template {isAsynchronous} con successo")
    public void putInterfaceMetadataSuccessfully(boolean isAsync) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();

        dataPreparationService.interpolateInterfaceToDescriptor(eServiceId, descriptorId);
        dataPreparationService.updateTemplateInstanceDraftDescriptor(eServiceId, descriptorId, isAsync);
    }

    @And("l'utente tenta la pubblicazione di una nuova versione dell'istanza del template")
    public void attemptToPublishNewTemplateInstanceVersion() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();

        sharedStepsContext.getDelayService().delayForSeconds(5);
        httpCallExecutor.performCall(() -> eServiceClient.publishDescriptor(eServiceId, descriptorId));
    }

    private void checkEServiceAndMutateState(EServiceDescriptorState expectedState) {
        checkEServiceCreated(EServiceDescriptorState.DRAFT);
        UUID lastEServiceIdCreatedFromTemplate = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();
        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorIdCreatedFromTemplate(descriptorId);
        this.dataPreparationService.bringTemplateInstanceDescriptorToGivenState(
                lastEServiceIdCreatedFromTemplate,
                descriptorId,
                expectedState,
                false);
    }

    @Then("il nuovo e-service è stato creato correttamente in stato {eServiceDescriptorState}")
    public void checkEServiceCreated(EServiceDescriptorState expectedState) {
        try {
            UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
            UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceDescriptorIdCreatedFromTemplate();

            pollingService.makePolling(
                    () -> httpCallExecutor.performCall(
                            () -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(eServiceId, descriptorId),
                            ResponseEntity::getStatusCode),
                    res -> res.getStatusCode().is2xxSuccessful(),
                    "Il nuovo e-service non è stato creato correttamente"
            );

            ResponseEntity<ProducerEServiceDescriptor> descriptorResponse =
                    (ResponseEntity<ProducerEServiceDescriptor>) httpCallExecutor.getResponse();
            ProducerEServiceDescriptor eServiceCreatedDescriptor = descriptorResponse.getBody();

            EServiceTemplateVersionDetails eServiceSourceTemplate = this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId()).getBody();

            List<CompactDescriptor> compactDescriptors =
                    nonNull(eServiceCreatedDescriptor) && nonNull(eServiceCreatedDescriptor.getEservice())
                            ? eServiceCreatedDescriptor.getEservice().getDescriptors()
                            : null;
            String eServiceName =
                    nonNull(eServiceCreatedDescriptor) && nonNull(eServiceCreatedDescriptor.getEservice())
                            ? eServiceCreatedDescriptor.getEservice().getName()
                            : null;

            assertSoftly(softly -> {
                softly.assertThat(eServiceCreatedDescriptor)
                        .as("Check reperimento descriptor dell'istanza creata")
                        .isNotNull();

                softly.assertThat(eServiceCreatedDescriptor)
                        .extracting(ProducerEServiceDescriptor::getId)
                        .as("Check id descriptor dell'istanza creata")
                        .isEqualTo(descriptorId);

                softly.assertThat(eServiceCreatedDescriptor)
                        .as("Check stato dell'istanza creata")
                        .extracting(ProducerEServiceDescriptor::getState)
                        .isEqualTo(expectedState);

                String templateName = eServiceSourceTemplate.getEserviceTemplate().getName();
                String instanceDefaultName = expectedEServiceInstanceName(templateName, instanceLabel);
                softly.assertThat(eServiceName)
                        .as("Check correttezza del nome dell'istanza creata")
                        .isEqualTo(instanceDefaultName);

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

            if (expectedState != EServiceDescriptorState.DRAFT) {
                CompactDescriptor compactDescriptor = java.util.Objects
                        .requireNonNull(compactDescriptors, "Lista descriptor dell'e-service non valorizzata")
                        .stream()
                        .filter(descriptor -> descriptorId.equals(descriptor.getId()))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException(
                                "Il descriptor atteso con id %s non e presente tra i descriptor dell'e-service %s"
                                        .formatted(descriptorId, eServiceId)));
                sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorCreatedFromTemplate(compactDescriptor);
            }
            sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceNameCreatedFromTemplate(eServiceName);
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
            ResponseEntity<CreatedEServiceDescriptor> response =
                    (ResponseEntity<CreatedEServiceDescriptor>) httpCallExecutor.getResponse();
            CreatedEServiceDescriptor createdEServiceDescriptor = response.getBody();

            // TODO ridondanti con il parametro di sotto nel context, li si mantiene momentaneamente per mantenere retrocompatibilità
            this.sharedStepsContext.getEServiceTemplateStepContext()
                    .setLastEServiceIdCreatedFromTemplate(createdEServiceDescriptor.getId());
            this.sharedStepsContext.getEServiceTemplateStepContext()
                    .setLastEServiceDescriptorIdCreatedFromTemplate(createdEServiceDescriptor.getDescriptorId());

            this.sharedStepsContext.getEServiceTemplateStepContext()
                    .setLastEServiceCreatedFromTemplate(createdEServiceDescriptor);
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
