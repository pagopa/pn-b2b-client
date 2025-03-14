package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.ResponseEntity;


/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi
@Data
public class EServiceTemplateVersionCRUDSteps {
    @Mapper(componentModel = "spring")
    public interface EServiceTemplateInfoMapper {
        /* TODO 07/03/2025 overhead, se questo mapper continua a servire solo a questo bisognerebbe
         * semplicemente mutare EServiceTemplateInfo in un pojo e ricorrere ai metodi set per modificarlo   */
        @Mapping(source = "newVersionId", target = "lastVersionId")
        EServiceTemplateInfo withVersionId(EServiceTemplateInfo templateInfo, UUID newVersionId);
    }

    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IEServiceClient eServiceClient;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;
    private final EServiceTemplateInfoMapper templateInfoMapper;
    private final DescriptorAttributesMapper descriptorAttributesMapper;

    private UUID lastDeletedVersion;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateVersionCRUDSteps(ClientTokenConfigurator clientTokenConfigurator,
        DataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext,
        EServiceTemplateInfoMapper templateInfoMapper,
        DescriptorAttributesMapper descriptorAttributesMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
        this.templateInfoMapper = templateInfoMapper;
        this.descriptorAttributesMapper = descriptorAttributesMapper;
    }

    @Given("l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo")
    public void createAnotherEServiceTemplateVersionSuccessfully() {
        createAnotherEServiceTemplateVersion();
        checkEServiceTemplateVersionCreated();
    }

    @When("l'utente tenta la creazione di una ulteriore versione in un e-service template inesistente")
    public void createAnotherEServiceTemplateVersionInNonExistentEServiceTemplate() {
        createAnotherEServiceTemplateVersion(UUID.randomUUID());
    }

    @When("l'utente tenta la creazione di una ulteriore versione nell'e-service template")
    public void createAnotherEServiceTemplateVersion() {
        createAnotherEServiceTemplateVersion(templateContext.getLastTemplateManaged().id());
    }

    @When("l'utente aggiunge all'e-service template una versione in stato {eServiceTemplateVersionState}")
    public void addEServiceTemplateVersion(EServiceTemplateVersionState state) {
        createAnotherEServiceTemplateVersion(templateContext.getLastTemplateManaged().id());
        testAssistant.mutateLastVersionState(state);
        checkEServiceTemplateVersionCreated();
    }

    @Then("la creazione di una ulteriore versione nell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionCreated() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getVersions().stream().anyMatch(v -> v.getId().equals(templateContext.getLastTemplateManaged().lastVersionId())),
                "La versione dell'e-service template non è stata creata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata creata correttamente");
        }
    }

    @Given("l'utente effettua delle modifiche alla versione dell'e-service template con successo")
    public void updateEServiceTemplateVersionSuccessfully() {
        updateEServiceTemplateVersion();
        checkEServiceTemplateVersionUpdate();
    }

    @When("l'utente tenta delle modifiche alla versione di un e-service template inesistente")
    public void updateNonExistentEServiceTemplateVersion() {
        UpdateEServiceTemplateVersionSeed updateSeed = new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            .attributes(testAssistant.nextAttributesSeed())
            .dailyCallsPerConsumer(500)
            .dailyCallsTotal(5000)
            .voucherLifespan(586400)
            .description("Nuova descrizione della versione");
        updateEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), updateSeed);
    }

    @When("l'utente tenta delle modifiche alla versione dell'e-service template")
    public void updateEServiceTemplateVersion() {
        templateContext.setLastTemplateVersionUpdateSeed(new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            .attributes(testAssistant.nextAttributesSeed())
            .dailyCallsPerConsumer(100)
            .dailyCallsTotal(1000)
            .voucherLifespan(86400)
            .description("Nuova descrizione della versione"));
        updateEServiceTemplateVersion(
            this.templateContext.getLastTemplateManaged().id(),
            this.templateContext.getLastTemplateManaged().lastVersionId(),
            this.templateContext.getLastTemplateVersionUpdateSeed());
    }

    @When("l'utente tenta di modificare la versione dell'e-service template indicando una specifica vuota")
    public void updateEServiceTemplateVersionWithEmptySpec() {
        updateEServiceTemplateVersion(
            this.templateContext.getLastTemplateManaged().id(),
            this.templateContext.getLastTemplateManaged().lastVersionId(),
            new UpdateEServiceTemplateVersionSeed());
    }

    @Then("le modifiche alla versione sono state applicate correttamente")
    public void checkEServiceTemplateVersionUpdate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && this.areConsistent(this.templateContext.getLastTemplateVersionUpdateSeed(), res.getBody()),
                "La versione dell'e-service template non corrisponde alle modifiche apportate"
            );
        } catch (PollingPredicateException e) {
            fail("Le modifiche alla versione dell'e-service template non sono state "
                    + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                this.templateContext.getLastTemplateVersionUpdateSeed(), httpCallExecutor.getResponse());
        }
    }

    @Then("i dettagli della versione dell'e-service template sono coerenti con quelli inseriti")
    public void checkEServiceTemplateVersionDetailsConsistent() {
        EServiceTemplateVersionDetails version = ((ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse()).getBody();
        assertThat(areConsistent(this.templateContext.getLastTemplateVersionUpdateSeed(), version))
            .withFailMessage("I dettagli della versione dell'e-service template ottenuti '%s' non sono coerenti con quelli inseriti '%s'", version, this.templateContext.getLastTemplateVersionUpdateSeed())
            .isTrue();
    }

    @Given("l'utente effettua la cancellazione della versione dell'e-service template con successo")
    public void deleteEServiceTemplateVersionSuccessfully() {
        deleteEServiceTemplateVersion();
        checkEServiceTemplateVersionDeleted();
        this.lastDeletedVersion = templateContext.getLastTemplateManaged().lastVersionId();
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template")
    public void deleteEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        deleteEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template indicando un identificativo vuoto")
    public void deleteUnspecifiedEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        deleteEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), null);
    }

    @Then("la cancellazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionDeleted() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getVersions().size() == 1,
                "La versione dell'e-service template non è stata cancellata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata cancellata correttamente: il numero di versioni presenti è diverso da 1");
        }
    }

    @When("l'utente tenta la cancellazione di una versione di un e-service template inesistente")
    public void deleteNonExistentEServiceTemplate() {
        deleteEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la cancellazione di una versione inesistente dell'e-service template")
    public void deleteNonExistentEServiceTemplateVersion() {
        deleteEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID());
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template già cancellata")
    public void deleteAlreadyDeletedEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = this.lastDeletedVersion;
        deleteEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    private void createAnotherEServiceTemplateVersion(UUID eServiceTemplateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.createEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId),
            ResponseEntity::getStatusCode);

        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            UUID idOfNewVersion = ((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody()
                .getId();
            templateContext.setLastTemplateManaged(this.templateInfoMapper.withVersionId(templateContext.getLastTemplateManaged(), idOfNewVersion));
        }
    }

    // TODO diverse NPE possibili, agire di conseguenza
    private boolean areConsistent(UpdateEServiceTemplateVersionSeed lastUpdate, EServiceTemplateVersionDetails retrievedTemplate) {
        DescriptorAttributes descriptorAttributes = retrievedTemplate.getAttributes();
        EServiceTemplateAttributesSeed mappedAttributes = this.descriptorAttributesMapper.mapAttributesToSeeds(
            descriptorAttributes);
        return lastUpdate.getAttributes().equals(mappedAttributes) &&
            lastUpdate.getDescription().equals(retrievedTemplate.getDescription()) &&
                lastUpdate.getAgreementApprovalPolicy().equals(retrievedTemplate.getAgreementApprovalPolicy()) &&
                lastUpdate.getVoucherLifespan().equals(retrievedTemplate.getVoucherLifespan()) &&
                lastUpdate.getDailyCallsTotal().equals(retrievedTemplate.getDailyCallsTotal()) &&
                lastUpdate.getDailyCallsPerConsumer().equals(retrievedTemplate.getDailyCallsPerConsumer());
    }

    private void updateEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UpdateEServiceTemplateVersionSeed sameNameUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersion(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                sameNameUpdateSeed));
    }

    private void deleteEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.deleteEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }
}
