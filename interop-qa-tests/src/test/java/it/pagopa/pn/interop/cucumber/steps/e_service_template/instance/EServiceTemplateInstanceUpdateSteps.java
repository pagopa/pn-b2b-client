package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceInstanceLabelUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jeasy.random.EasyRandom;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Cucumber steps involving quotas of E-service templates
 */
@Data
public class EServiceTemplateInstanceUpdateSteps {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;
    private final IEServiceClient eServiceClient;
    private final IM2MEserviceClient m2mEServiceClient;
    private final BlobFileCreator blobService;
    private final IdentityService identityService;
    private final EServiceTemplateInstanceUtility eServiceTemplateInstanceUtility;
    private UpdateEServiceTemplateInstanceSeed lastUpdateEServiceTemplateInstanceSeed;
    private String previousInterface;

    public EServiceTemplateInstanceUpdateSteps(BFFDataPreparationService dataPreparationService,
                                               ClientTokenConfigurator clientTokenConfigurator,
                                               SharedStepsContext sharedStepsContext,
                                               BlobFileCreator blobService
    ) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.blobService = blobService;
        this.identityService = sharedStepsContext.getIdentityService();
        this.m2mEServiceClient = clientTokenConfigurator.getM2meServiceClient();
        this.eServiceTemplateInstanceUtility = new EServiceTemplateInstanceUtility(this.sharedStepsContext);
    }

    @When("l'utente tenta la modifica dei campi dell'istanza dell'e-service template")
    public void editEServiceInstanceFields() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
        lastUpdateEServiceTemplateInstanceSeed = new UpdateEServiceTemplateInstanceSeed()
                .isClientAccessDelegable(true)
                .isConsumerDelegable(true)
                .isSignalHubEnabled(true);
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template {string} usando l'endpoint di update per lo stato {eServiceDescriptorState} con {string}")
    @When("l'utente tenta la modifica del campo instanceLabel dell'istanza dell'e-service template {string} in stato {eServiceDescriptorState} con {string}")
    public void editEServiceInstanceInstanceLabelField(String eServiceTemplateInstanceId, EServiceDescriptorState eServiceState, String instanceLabel) {
        String parsedSuffix = eServiceTemplateInstanceUtility.parseSuffix(instanceLabel);
        UUID eServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId(eServiceTemplateInstanceId);
        switch (eServiceState) {
            case DRAFT:
                lastUpdateEServiceTemplateInstanceSeed = new UpdateEServiceTemplateInstanceSeed()
                        .instanceLabel(parsedSuffix);
                editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
                break;
            case PUBLISHED:
                EServiceInstanceLabelUpdateSeed seed = new EServiceInstanceLabelUpdateSeed()
                        .instanceLabel(parsedSuffix);
                editEServiceInstanceInstanceLabel(eServiceId, seed);
                break;
            default:
                throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                        EServiceDescriptorState.class.getSimpleName(),
                        eServiceState)
                );
        }
    }

    @When("l'utente tenta la modifica dei campi di un'istanza inesistente dell'e-service template")
    public void editNotExistentEServiceInstanceFields() {
        UUID eServiceId = UUID.randomUUID();
        lastUpdateEServiceTemplateInstanceSeed = new UpdateEServiceTemplateInstanceSeed()
                .instanceLabel(RandomStringUtils.insecure().nextAlphanumeric(5))
                .isClientAccessDelegable(false)
                .isConsumerDelegable(false)
                .isSignalHubEnabled(false);
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
                                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId()),
                            ResponseEntity::getStatusCode),
                    res ->
                            res.getStatusCode().is2xxSuccessful() &&
                                    nonNull(res.getBody()) &&
                                    res.getBody().getResults().stream().anyMatch(instance -> instance.getId().equals(sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate())),
                    "L'istanza non è presente nell'elenco delle istanze dell'e-service template: non è stato trovato un'istanza con l'id " +
                            sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate()
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
                    "L'istanza non è stata modificata correttamente: uno dei campi dell'istanza non è stato modificato correttamente."
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
        return isTrue(instanceDetails.getIsClientAccessDelegable()) == isTrue(updateSeed.getIsClientAccessDelegable()) &&
                isTrue(instanceDetails.getIsConsumerDelegable()) == isTrue(updateSeed.getIsConsumerDelegable()) &&
                isTrue(instanceDetails.getIsSignalHubEnabled()) == isTrue(updateSeed.getIsSignalHubEnabled());
    }

    @Given("l'utente effettua l'aggiunta di una versione in stato {eServiceDescriptorState} all'e-service con successo")
    public void createEServiceVersionDraftSuccessfully(EServiceDescriptorState descriptorState) {
        UUID oldDescriptor = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastEServiceDescriptorIdCreatedFromTemplate();
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastEServiceIdCreatedFromTemplate();
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceId);
        sharedStepsContext.getEServicesCommonContext().setOldDescriptorId(oldDescriptor);

        UUID newDescriptor = this.dataPreparationService.createNextDraftDescriptor(eServiceId);
        this.dataPreparationService.bringTemplateInstanceDescriptorToGivenState(
                eServiceId,
                newDescriptor,
                descriptorState,
                false
        );
        sharedStepsContext.getEServiceTemplateStepContext().setLastEServiceDescriptorIdCreatedFromTemplate(newDescriptor);
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(newDescriptor);

    }

    @When("l'utente tenta di associare un'interfaccia all'istanza dell'e-service template")
    public void uploadInterfaceToEServiceTemplateInstance() {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastEServiceDescriptorIdCreatedFromTemplate();
        String kind = "INTERFACE";
        String prettyName = "new pretty name " + RandomUtils.insecure().randomInt(0, 100);
        Resource inter = this.blobService.createBlobFile("src/main/resources/interface1.yaml", "new instance interface");

        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument(eServiceId, descriptorId, kind, prettyName, inter));
    }

    // TODO 11/12/2025 generalizzabile anche a e-service non creati a partire da templates
    @And("[si prende nota dell'attuale interfaccia dell'istanza dell'e-service]")
    public void storeEServiceInterface() throws IOException {
        String prevToken = clientTokenConfigurator.getLastToken();
        String newToken = identityService.getToken(sharedStepsContext.getTenantType(), M2MRole.M2M_ADMIN.toString());
        clientTokenConfigurator.setBearerToken(newToken);

        this.previousInterface = getEServiceInstanceInterface();

        clientTokenConfigurator.setBearerToken(prevToken);
    }

    private String getEServiceInstanceInterface() throws IOException {
        UUID eServiceId = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastEServiceIdCreatedFromTemplate();
        UUID descriptorId = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastEServiceDescriptorIdCreatedFromTemplate();

        FileDownloadMultipart descriptorInterface = m2mEServiceClient.getDescriptorInterface(
                eServiceId, descriptorId);

        byte[] actualInterface = Files.readAllBytes(descriptorInterface.getFile().toPath());
        return new String(actualInterface, StandardCharsets.UTF_8);
    }

    /* DEV. NOTE: il confronto puntuale di due file di interfaccia non è la migliore delle opzioni,
    poiché questa subisce delle variazione lato backend INTEROP che ne modificano la forma pur
    mantenendone la coerenza semantica (es. https://pagopaspa.slack.com/archives/C06D24MANNN/p1765448353344599).
    Si consiglia di riformulare lo step usando forme di astrazione superiori, eventualmente
    attraverso librerie esterne. */
    @Then("l'interfaccia dell'istanza dell'e-service template non ha subito mutamenti")
    public void instanceInterfaceUntouched() throws IOException {
        String prevToken = clientTokenConfigurator.getLastToken();
        String newToken = identityService.getToken(sharedStepsContext.getTenantType(), M2MRole.M2M_ADMIN.toString());
        clientTokenConfigurator.setBearerToken(newToken);

        String actualInterfaceStr = getEServiceInstanceInterface();
        String expectedInterfaceStr = this.previousInterface;

        assertThat(actualInterfaceStr)
                .as("Verifica che il doc. di interfaccia reperito sia identico a quello definito in fase di creazione dell'istanza")
                .isEqualTo(expectedInterfaceStr);

        clientTokenConfigurator.setBearerToken(prevToken);
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

    private void editEServiceInstanceInstanceLabel(UUID eServiceId, EServiceInstanceLabelUpdateSeed seed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
                () -> eServiceClient.updateEServiceInstanceLabelAfterPublicationWithHttpInfo(
                        eServiceId,
                        seed
                ),
                ResponseEntity::getStatusCode
        );
    }


}
