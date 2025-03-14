package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi
/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template */
@Data
public class EServiceTemplateCRUDSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;

    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateCRUDSteps(ClientTokenConfigurator clientTokenConfigurator,
        DataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode}")
    public void createEServiceTemplate(EServiceMode eServiceMode) {
        EServiceTemplateSeed templateSeed = getEServiceTemplateSeed(eServiceMode);
        createEServiceTemplate(templateSeed);
    }

    @When("l'utente tenta la creazione di un e-service template indicando una specifica vuota")
    public void createUnspecifiedEServiceTemplate() {
        createEServiceTemplate(new EServiceTemplateSeed());
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState}")
    public void createEServiceTemplate(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState) {
        createEServiceTemplate(eServiceMode);
        if (eServiceMode == EServiceMode.RECEIVE) {
            testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully(); // perché ogni template in RECEIVE deve avere una risk analysis
        }
        testAssistant.mutateLastVersionState(desiredState);
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} usando lo stesso nome")
    public void createEServiceTemplateWithSameName(EServiceMode eServiceMode) {
        String lastTemplateNameUsed = templateContext.getLastTemplateManaged().name();
        EServiceTemplateSeed sameNameTemplateSeed = this.getEServiceTemplateSeed(eServiceMode)
            .name(lastTemplateNameUsed);
        createEServiceTemplate(sameNameTemplateSeed);
    }

    @When("l'utente tenta delle modifiche all'e-service template")
    public void updateEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateUpdateSeed = new UpdateEServiceTemplateSeed()
            .name(templateContext.getLastTemplateManaged().name() + " - modificato")
            .intendedTarget("Nuovo intended target")
            .description("Nuova descrizione")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE)
            .isSignalHubEnabled(false);
        updateEServiceTemplate(eServiceTemplateId, lastTemplateUpdateSeed);
    }

    @When("l'utente tenta di modificare l'e-service template specificando lo stesso nome")
    public void updateEServiceTemplateWithSameName() {
        UpdateEServiceTemplateSeed sameNameUpdateSeed = new UpdateEServiceTemplateSeed()
            .name(templateContext.getLastTemplateManaged().name())
            .intendedTarget("Nuova intended target")
            .description("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE);
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        updateEServiceTemplate(eServiceTemplateId, sameNameUpdateSeed);
    }

    @When("l'utente tenta di modificare l'e-service template specificando un nome vuoto")
    public void updateEServiceTemplateWithEmptyName() {
        UpdateEServiceTemplateSeed emptyNameUpdateSeed = new UpdateEServiceTemplateSeed()
            .name("");
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        updateEServiceTemplate(eServiceTemplateId, emptyNameUpdateSeed);
    }

    @When("l'utente tenta delle modifiche a un e-service template inesistente")
    public void updateNonExistentEServiceTemplate() {
        UUID eServiceTemplateId = UUID.randomUUID();
        UpdateEServiceTemplateSeed updateSeed = new UpdateEServiceTemplateSeed()
            .name("Nuovo nome")
            .intendedTarget("Nuova intended target")
            .description("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE);
        updateEServiceTemplate(eServiceTemplateId, updateSeed);
    }

    @When("l'utente tenta di modificare l'e-service template indicando una specifica vuota")
    public void updateEServiceTemplateWithEmptySpec() {
        updateEServiceTemplate(templateContext.getLastTemplateManaged().id(), new UpdateEServiceTemplateSeed());
    }

    @Then("le modifiche al template sono state applicate correttamente")
    public void checkEServiceTemplateUpdate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall( // TODO è stata introdotta la API specifica per i template, refattorizzare usando quella (non solo qui) per i check che riguardano solo i template
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && this.areConsistent(lastTemplateUpdateSeed, res.getBody().getEserviceTemplate()),
                "L'e-service template non corrisponde alle modifiche apportate"
            );
        } catch (PollingPredicateException e) {
            fail("Le modifiche all'e-service template non sono state "
                    + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                lastTemplateUpdateSeed, httpCallExecutor.getResponse());
        }
    }

    // TODO gli step sono pieni di pattern ricorrenti, questo step ne è un'esempio. Andrebbero astratti e portati in classi di utility esterne.
    @Then("la cancellazione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateDeleted() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().equals(HttpStatus.NOT_FOUND),
                "L'e-service template non è stato cancellato correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("L'e-service template non è stato cancellato correttamente");
        }
    }

    @When("l'utente tenta la visualizzazione dei dettagli dell'e-service template")
    public void getEServiceTemplateDetails() {
        getEServiceTemplateDetails(templateContext.getLastTemplateManaged().id());
    }

    @When("l'utente tenta la visualizzazione dei dettagli di un e-service template inesistente")
    public void getNonExistentEServiceTemplateDetails() {
        getEServiceTemplateDetails(UUID.randomUUID());
    }

    @When("l'utente tenta la visualizzazione dei dettagli dell'e-service template indicando un identificativo vuoto")
    public void getUnspecifiedEServiceTemplateDetails() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        getEServiceTemplateDetails(null);
    }

    @Then("i dettagli dell'e-service template contengono esattamente {int} versioni")
    public void checkEServiceTemplateDetailsContainVersions(int expectedVersionCount) {
        EServiceTemplateDetails template = ((ResponseEntity<EServiceTemplateDetails>) httpCallExecutor.getResponse()).getBody();
        assertThat(template.getVersions()).hasSize(expectedVersionCount);
    }

    private void updateEServiceTemplate(UUID eServiceTemplateId, UpdateEServiceTemplateSeed sameNameUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                sameNameUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    /** Return a new {@link EServiceTemplateSeed} with only the mandatory fields set
     * @param eServiceMode the risk analysis mode of the e-service
     * @return a new {@link EServiceTemplateSeed} instance
     */
    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode) {
        String templateName = String.format("eservice-template-%s", testAssistant.nextTestResourceNameSuffix());
        VersionSeedForEServiceTemplateCreation version = new VersionSeedForEServiceTemplateCreation()
            .voucherLifespan(86400);
        return new EServiceTemplateSeed()
            .intendedTarget("Audience description per il template " + templateName)
            .name(templateName)
            .description("Descrizione del servizio associato al template " + templateName)
            .mode(eServiceMode)
            .version(version)
            .technology(EServiceTechnology.REST);
    }

    private void createEServiceTemplate(EServiceTemplateSeed templateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        CreatedEServiceTemplateVersion creationResponse = this.dataPreparationService.createEServiceTemplate(
            templateSeed);
        templateContext.setLastTemplateManaged(new EServiceTemplateInfo(
            templateSeed.getName(),
            templateSeed.getIntendedTarget(),
            templateSeed.getDescription(),
            creationResponse.getId(),
            creationResponse.getVersionId()));
    }

    private boolean areConsistent(UpdateEServiceTemplateSeed lastUpdate, EServiceTemplateDetails retrievedTemplate) {
        return lastUpdate.getName().equals(retrievedTemplate.getName()) &&
            lastUpdate.getIntendedTarget().equals(retrievedTemplate.getIntendedTarget()) &&
            lastUpdate.getDescription().equals(retrievedTemplate.getDescription()) &&
            lastUpdate.getTechnology().equals(retrievedTemplate.getTechnology()) &&
            lastUpdate.getMode().equals(retrievedTemplate.getMode());
    }

    private void getEServiceTemplateDetails(UUID eServiceTemplateId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId),
            ResponseEntity::getStatusCode);
    }
}
