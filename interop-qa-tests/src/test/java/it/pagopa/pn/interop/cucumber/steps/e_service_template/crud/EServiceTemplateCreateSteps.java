package it.pagopa.pn.interop.cucumber.steps.e_service_template.crud;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.Document;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.Data;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi

/**
 * Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template
 */
@Data
public class EServiceTemplateCreateSteps {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final DelayService delayService;
    private final IdentityService identityService;

    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
     *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
     *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateCreateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                       BFFDataPreparationService dataPreparationService,
                                       SharedStepsContext sharedStepsContext,
                                       EServiceTemplateTestAssistant testAssistant,
                                       DelayService delayService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.delayService = delayService;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode}")
    public void createEServiceTemplate(EServiceMode eServiceMode) {
        EServiceTemplateSeed templateSeed = getEServiceTemplateSeed(eServiceMode);
        createEServiceTemplate(templateSeed);
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} con flagPersonalDate impostato a {string}")
    public void createEServiceTemplate(EServiceMode eServiceMode, String flagPersonalDate) {
        EServiceTemplateSeed templateSeed = getEServiceTemplateSeed(eServiceMode, flagPersonalDate.equals("undefined") ? null : flagPersonalDate.equalsIgnoreCase("true"));
        createEServiceTemplate(templateSeed);
    }

    @When("l'utente tenta la creazione di un e-service template indicando una specifica vuota")
    public void createUnspecifiedEServiceTemplate() {
        createEServiceTemplate(new EServiceTemplateSeed());
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState}")
    public void createEServiceTemplate(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState) {
        createEServiceTemplate(eServiceMode);

        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastTemplateManaged();
        if (eServiceMode == EServiceMode.RECEIVE && nonNull(lastTemplateManaged)) {
            testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully(); // perché ogni template in RECEIVE deve avere una risk analysis
        }
        testAssistant.mutateLastVersionState(desiredState);
    }

    @When("l'utente effettua la creazione di un e-service template {isAsynchronous} in modalità {eServiceMode} con tecnologia {string} in stato di {eServiceTemplateVersionState}")
    public void createEServiceTemplate(Boolean isAsync, EServiceMode eServiceMode, String technology, EServiceTemplateVersionState desiredState) {
        EServiceTechnology technology1 = EServiceTechnology.fromValue(technology);
        sharedStepsContext.getEServiceTemplateStepContext().setTechnology(technology1);
        EServiceTemplateSeed templateSeed = this.getEServiceTemplateSeed(eServiceMode);
        templateSeed.asyncExchange(isAsync).technology(technology1);
        this.createEServiceTemplate(templateSeed);
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged();
        if (eServiceMode == EServiceMode.RECEIVE && nonNull(lastTemplateManaged)) {
            testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully(); // perché ogni template in RECEIVE deve avere una risk analysis
        }
        testAssistant.mutateLastVersionState(desiredState);
    }

    @Given("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState} con nome {string}")
    public void createEServiceTemplateWithName(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState, String name) {
        createEServiceTemplateWithName(eServiceMode, desiredState, name, null);
    }

    @Given("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState} con nome {string} e descrizione di {int} caratteri")
    public void createEServiceTemplateWithName(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState, String name, Integer descriptionLength) {

        EServiceTemplateSeed templateSeed;
        if (descriptionLength != null) {
            String description = (new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue();
            templateSeed = getEServiceTemplateSeed(eServiceMode, true, description);
        } else {
            templateSeed = getEServiceTemplateSeed(eServiceMode, true);
        }

        EServiceTemplateStepContext ctx = sharedStepsContext.getEServiceTemplateStepContext();
        String seed = ctx.getLastUsedEServiceTemplateNameSeed();
        if (seed == null) {
            seed = templateSeed.getName();
            ctx.setLastUsedEServiceTemplateNameSeed(seed);
        }
        templateSeed.name(seed + name);

        createEServiceTemplate(templateSeed);
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastTemplateManaged();
        if (eServiceMode == EServiceMode.RECEIVE && nonNull(lastTemplateManaged)) {
            testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully(); // perché ogni template in RECEIVE deve avere una risk analysis
        }
        if (nonNull(lastTemplateManaged)) {
            testAssistant.mutateLastVersionState(desiredState);
        }
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState} con flagPersonalData impostato a {string}")
    public void createEServiceTemplate(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState, String flagPersonalData) {
        createEServiceTemplate(eServiceMode, flagPersonalData);
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastTemplateManaged();
        if (eServiceMode == EServiceMode.RECEIVE && nonNull(lastTemplateManaged)) {
            testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully(); // perché ogni template in RECEIVE deve avere una risk analysis
        }
        testAssistant.mutateLastVersionState(desiredState);
    }

    @When("l'e-service template creato ha una descrizione di {int} caratteri")
    public void checkLengthDescriptionOfEServiceTemplateCreated(Integer descriptionLength) {
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastTemplateManaged();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> eServiceTemplateClient.getEServiceTemplate(lastTemplateManaged.getId())
                ),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service template"
        );

        String description = ((EServiceTemplateDetails) httpCallExecutor.getResponse()).getDescription();

        Assertions.assertNotNull(description);
        Assertions.assertEquals(descriptionLength, description.length());
    }

    @When("l'e-service template creato è configurato come {isAsynchronous}")
    public void checkEServiceTemplateIsConfiguredAsAsynchronous(Boolean isAsync) {
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastTemplateManaged();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> eServiceTemplateClient.getEServiceTemplate(lastTemplateManaged.getId())
                ),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service template"
        );

        Assertions.assertEquals(isAsync, ((EServiceTemplateDetails) httpCallExecutor.getResponse()).getAsyncExchange());
    }

    @When("{string} porta la versione dell'e-service template in stato {eServiceTemplateVersionState}")
    public void mutateEServiceTemplateState(String tenantType, EServiceTemplateVersionState desiredState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        testAssistant.mutateLastVersionState(desiredState);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} usando lo stesso nome")
    public void createEServiceTemplateWithSameName(EServiceMode eServiceMode) {
        String lastTemplateNameUsed = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getName();
        EServiceTemplateSeed sameNameTemplateSeed = this.getEServiceTemplateSeed(eServiceMode)
                .name(lastTemplateNameUsed);
        createEServiceTemplate(sameNameTemplateSeed);
    }

    @Given("l'utente ha già creato un e-service template in modalità {eServiceMode}, stato {eServiceTemplateVersionState} e {int} DOCUMENTI già caricati")
    public void createEServiceTemplate(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState, int documents) {
        // creo il template
        createEServiceTemplate(eServiceMode);
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastTemplateManaged();

        // genero E carico i documenti
        List<Document> documentList = dataPreparationService.addDocumentsToResource(
                UUID.randomUUID(),
                documents,
                "E-Service template document",
                "EST doc",
                (prettyName, resource) -> testAssistant.addDocumentToEserviceTemplateVersion(
                        lastTemplateManaged.getId(),
                        lastTemplateManaged.getLastVersionId(),
                        EServiceTemplateDocumentKind.DOCUMENT,
                        prettyName,
                        sharedStepsContext.getUserToken(),
                        resource
                ));

        // NOTE 24/09/2025: si riutilizza l'attributo di EServicesCommonContext, essendo il tipo di
        // dato trattato identico, ed essendo che i successivi step di verifica vi fanno riferimento.
        // Sarebbe eventualmente da generalizzare spostandolo al liv. superiore "SharedStepContext".
        sharedStepsContext.getEServicesCommonContext().setDocumentsMetadata(documentList.stream().map(Document::getMetadata).toList());

        // muto lo stato in quello atteso
        if (!desiredState.equals(EServiceTemplateVersionState.DRAFT)) {
            delayService.delayForSeconds(1); // <-- per concedere il tempo di caricare il doc. di tipo INTERFACE evitando errori di eventual consistency
        }
        testAssistant.mutateLastVersionState(desiredState);
    }

    private void createEServiceTemplate(EServiceTemplateSeed templateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        httpCallExecutor.performCall(() -> eServiceTemplateClient.createEServiceTemplate(templateSeed));
        if (httpCallExecutor.getResponseStatus().isError()) {
            return; // a questo punto si prevede che i passi successivi riconoscano l'errore, dunque non si lanciano errori
        }

        CreatedEServiceTemplateVersion creationResponse = (CreatedEServiceTemplateVersion) httpCallExecutor.getResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> eServiceTemplateClient.getEServiceTemplateVersion(
                                creationResponse.getId(),
                                creationResponse.getVersionId())),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service template"
        );

        sharedStepsContext.getEServiceTemplateStepContext().addTemplateManaged(new EServiceTemplateInfo(
                templateSeed.getName(),
                templateSeed.getIntendedTarget(),
                templateSeed.getDescription(),
                templateSeed.getTechnology(),
                templateSeed.getMode(),
                creationResponse.getId(),
                creationResponse.getVersionId(),
                templateSeed.getPersonalData(),
                templateSeed.getAsyncExchange()
                ));
    }

    /**
     * Return a new {@link EServiceTemplateSeed} with only the mandatory fields set
     *
     * @param eServiceMode the risk analysis mode of the e-service
     * @return a new {@link EServiceTemplateSeed} instance
     */
    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode) {
        return getEServiceTemplateSeed(eServiceMode, false);
    }

    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode, Boolean flagPersonalData) {
        String templateName = testAssistant.buildEServiceTemplateName();
        VersionSeedForEServiceTemplateCreation version = new VersionSeedForEServiceTemplateCreation()
                .voucherLifespan(86400);
        return new EServiceTemplateSeed()
                .intendedTarget("Audience description per il template " + templateName)
                .name(templateName)
                .description("Descrizione del servizio associato al template " + templateName)
                .mode(eServiceMode)
                .version(version)
                .personalData(flagPersonalData)
                .technology(EServiceTechnology.REST);
    }

    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode, Boolean flagPersonalData, String description) {
        String templateName = testAssistant.buildEServiceTemplateName();
        VersionSeedForEServiceTemplateCreation version = new VersionSeedForEServiceTemplateCreation()
                .voucherLifespan(86400);
        return new EServiceTemplateSeed()
                .intendedTarget("Audience description per il template " + templateName)
                .name(templateName)
                .description(description)
                .mode(eServiceMode)
                .version(version)
                .personalData(flagPersonalData)
                .technology(EServiceTechnology.REST);
    }
}
