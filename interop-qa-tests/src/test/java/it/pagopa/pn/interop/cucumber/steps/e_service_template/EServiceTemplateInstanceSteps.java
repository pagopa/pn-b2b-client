package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstance;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import org.assertj.core.api.Condition;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateInstanceSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateStepContext templateContext;
    private final EasyRandom easyRandom;
    private final IEServiceClient eServiceClient;

    private InstanceEServiceSeed lastEServiceCreatedFromTemplate;
    private UUID lastEServiceIdCreatedFromTemplate;
    private UpdateEServiceTemplateInstanceSeed lastUpdateEServiceTemplateInstanceSeed;
    private UpdateEServiceDescriptorTemplateInstanceSeed lastUpdateEServiceDescriptorTemplateInstanceSeed;
    private CompactDescriptor lastEServiceDescriptorCreatedFromTemplate;
    private UUID lastEServiceIdUpdatedFromTemplate;
    private UUID lastEServiceDescriptorIdUpdatedFromTemplate;

    public EServiceTemplateInstanceSteps(DataPreparationService dataPreparationService,
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateStepContext templateContext
    ) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.templateContext = templateContext;
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
    }

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpec() {
        createEServiceFromTemplate(templateContext.getLastTemplateManaged().id(), null);
    }


    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando tutte le specifiche")
    public void createEServiceFromTemplateFullSpec() {
        InstanceEServiceSeed seed = easyRandom.nextObject(InstanceEServiceSeed.class);
        createEServiceFromTemplate(templateContext.getLastTemplateManaged().id(), seed);
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

        if(anyNull(lastEServiceIdCreatedFromTemplate, lastEServiceDescriptorCreatedFromTemplate)) {
            throw new IllegalStateException(("Una o più precondizioni necessarie al mutamento di "
                + "stato dell'e-service non sono rispettate: eServiceId = %s, eServiceDescriptor = %s")
                .formatted(lastEServiceIdCreatedFromTemplate, lastEServiceDescriptorCreatedFromTemplate));
        }
        this.dataPreparationService.bringDescriptorToGivenState(
            lastEServiceIdCreatedFromTemplate,
            lastEServiceDescriptorCreatedFromTemplate.getId(),
            expectedState,
            false);
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze dell'e-service template")
    public void getEServiceTemplateInstances() {
        getEserviceTemplateInstances(templateContext.getLastTemplateManaged().id());
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze di un e-service template inesistente")
    public void getNotExistentEServiceTemplateInstances() {
        getEserviceTemplateInstances(UUID.randomUUID());
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template")
    public void editEServiceTemplateInstanceDescriptor() {
        UUID eServiceTemplateInstanceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceTemplateInstanceDescriptorId = lastEServiceDescriptorCreatedFromTemplate.getId();

        lastUpdateEServiceDescriptorTemplateInstanceSeed = easyRandom.nextObject(
            UpdateEServiceDescriptorTemplateInstanceSeed.class);
        editEServiceTemplateInstanceDescriptor(eServiceTemplateInstanceId, eServiceTemplateInstanceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica di un descriptor inesistente dell'istanza dell'e-service template")
    public void editNonExistentEServiceTemplateInstanceDescriptor() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceDescriptorId = UUID.randomUUID();
        lastUpdateEServiceDescriptorTemplateInstanceSeed = easyRandom.nextObject(UpdateEServiceDescriptorTemplateInstanceSeed.class);
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateInstanceDescriptorWithEmptySpec() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceDescriptorId = lastEServiceDescriptorCreatedFromTemplate.getId();
        UpdateEServiceDescriptorTemplateInstanceSeed emptySeed = new UpdateEServiceDescriptorTemplateInstanceSeed();
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, emptySeed);
    }

    @Then("il descriptor dell'istanza dell'e-service template è stato modificato correttamente")
    public void checkEServiceTemplateInstanceDescriptorEdited() {
        UUID eServiceTemplateInstanceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceTemplateInstanceDescriptorId = lastEServiceDescriptorCreatedFromTemplate.getId();
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

    @Then("il nuovo e-service è stato creato correttamente in stato {eServiceDescriptorState}")
    public void checkEServiceCreated(EServiceDescriptorState expectedState) {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        templateContext.getLastTemplateManaged().id()
                    ),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && !res.getBody().getResults().isEmpty(),
                "Il nuovo e-service non è stato creato correttamente"
            );

            EServiceTemplateVersionDetails eServiceSourceTemplate = this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                templateContext.getLastTemplateManaged().id(),
                templateContext.getLastTemplateManaged().lastVersionId()).getBody();
            Optional<EServiceTemplateInstance> eServiceCreatedFromTemplate = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody()
                .getResults()
                .stream()
                .filter(instance -> instance.getId().equals(lastEServiceIdCreatedFromTemplate))
                .findAny();

            assertSoftly(softly -> {
                softly.assertThat(eServiceCreatedFromTemplate)
                    .as("Check esistenza istanza del template")
                    .withFailMessage("Fra le istanze del template non è presente quella appena creata. E' possibile sia avvenuto un errore a monte in fase di creazione dell'istanza, oppure a valle in fase di reperimento delle stesse.")
                    .isPresent();

                if(eServiceCreatedFromTemplate.get().getDescriptors().size() != 1) {
                    throw new IllegalStateException("L'e-service appena creato ha più di un descriptor: ciò rende incerto quale descriptor considerare per le successive operazioni di test");
                }
                this.lastEServiceDescriptorCreatedFromTemplate = eServiceCreatedFromTemplate.get().getDescriptors().get(0);

                softly.assertThat(eServiceCreatedFromTemplate)
                    .get()
                    .as("Check stato dell'istanza creata")
                    .extracting(EServiceTemplateInstance::getActiveDescriptor)
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

    @When("l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione")
    public void updateEServiceInstanceToLatestVersion() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
        updateEServiceInstance(eServiceId);
    }

    @When("l'utente tenta l'aggiornamento di un'istanza inesistente dell'e-service template")
    public void updateNonExistentEServiceInstance() {
        updateEServiceInstance(UUID.randomUUID());
    }

    @When("l'utente tenta l'aggiornamento di un'istanza dell'e-service template specificando un identificativo vuoto")
    public void updateEmptyEServiceInstance() {
        updateEServiceInstance(null);
    }

    @Then("il nuovo e-service riferito all'ultima versione dell'e-service template è stato creato correttamente")
    public void checkEServiceCreatedFromLatestTemplateVersion() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        lastEServiceIdUpdatedFromTemplate,
                        lastEServiceDescriptorIdUpdatedFromTemplate
                    ),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()),
                "Il nuovo e-service non è stato aggiornato correttamente"
            );

            @SuppressWarnings("all")
            ProducerEServiceDescriptor eServiceUpdatedDescriptor = ((ResponseEntity<ProducerEServiceDescriptor>) httpCallExecutor.getResponse()).getBody();

            assertSoftly(softly -> {
                softly.assertThat(lastEServiceDescriptorIdUpdatedFromTemplate)
                    .as("Check presenza descriptor associato all'istanza aggiornata")
                    .isEqualTo(eServiceUpdatedDescriptor.getId()); // NPE impossibile, in quanto da condizione di polling il body non può essere null
                softly.assertThat(eServiceUpdatedDescriptor)
                    .as("Check corretto stato dell'istanza aggiornata")
                    .extracting(ProducerEServiceDescriptor::getState)
                    .isEqualTo(EServiceDescriptorState.DRAFT);
            });
        } catch (PollingPredicateException e) {
            fail("Il nuovo e-service non è stato aggiornato correttamente");
        }
    }

    @When("l'utente tenta la modifica dei campi dell'istanza dell'e-service template")
    public void editEServiceInstanceFields() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
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
                        templateContext.getLastTemplateManaged().id()),
                    ResponseEntity::getStatusCode),
                res ->
                    res.getStatusCode().is2xxSuccessful() &&
                        nonNull(res.getBody()) &&
                        res.getBody().getResults().stream().anyMatch(instance -> instance.getId().equals(lastEServiceIdCreatedFromTemplate)) &&
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
            lastEServiceIdCreatedFromTemplate);
        this.dataPreparationService.bringDescriptorToGivenState(
            lastEServiceIdCreatedFromTemplate,
            newDescriptor,
            descriptorState,
            false
        );
    }

    @Then("sono state visualizzate {int} istanza in stato DRAFT, {int} in stato PUBLISHED e {int} in stato SUSPENDED")
    public void checkEServiceTemplateInstancesCount(int draftCount, int publishedCount, int suspendedCount) {
        List<EServiceTemplateInstance> response = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody().getResults();
        assertSoftly(softly -> {
            softly.assertThat(response)
                .areExactly(
                    draftCount,
                    instanceInState(EServiceDescriptorState.DRAFT));
            softly.assertThat(response)
                .areExactly(
                    publishedCount,
                    instanceInState(EServiceDescriptorState.PUBLISHED));
            softly.assertThat(response)
                .areExactly(
                    suspendedCount,
                    instanceInState(EServiceDescriptorState.SUSPENDED));
        });
    }

    private Condition<EServiceTemplateInstance> instanceInState(EServiceDescriptorState state) {
        return new Condition<>(
            instance -> instance.getActiveDescriptor().getState().equals(state),
            "instances in state %s", state);
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

        this.lastEServiceIdCreatedFromTemplate = ((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody().getId();
        this.lastEServiceCreatedFromTemplate = seed;
    }

    private void updateEServiceInstance(UUID uuid) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.upgradeEServiceInstanceWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                uuid),
            ResponseEntity::getStatusCode);

        ResponseEntity<CreatedEServiceDescriptor> response = (ResponseEntity<CreatedEServiceDescriptor>) httpCallExecutor.getResponse();
        this.lastEServiceIdUpdatedFromTemplate = response.getBody().getId();
        this.lastEServiceDescriptorIdUpdatedFromTemplate = response.getBody().getId();
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

    private void getEserviceTemplateInstances(UUID templateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                templateId
            ),
            ResponseEntity::getStatusCode);
    }

    private boolean areConsistent(ProducerEServiceDescriptor descriptor, UpdateEServiceDescriptorTemplateInstanceSeed seed) {
        return seed.getAudience().equals(descriptor.getAudience()) &&
            seed.getAgreementApprovalPolicy().equals(descriptor.getAgreementApprovalPolicy()) &&
            seed.getDailyCallsPerConsumer().equals(descriptor.getDailyCallsPerConsumer()) &&
            seed.getDailyCallsTotal().equals(descriptor.getDailyCallsTotal());
    }
}
