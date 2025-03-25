package it.pagopa.pn.interop.cucumber.steps.e_service_template.crud;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import lombok.Data;
import org.springframework.http.HttpStatus;

// TODO perché @Data? Considerarne rimozione da questa e dalle altre classi
/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template */
@Data
public class EServiceTemplateCreateSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;

    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateCreateSteps(ClientTokenConfigurator clientTokenConfigurator,
        DataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
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
        String lastTemplateNameUsed = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().name();
        EServiceTemplateSeed sameNameTemplateSeed = this.getEServiceTemplateSeed(eServiceMode)
            .name(lastTemplateNameUsed);
        createEServiceTemplate(sameNameTemplateSeed);
    }

    private void createEServiceTemplate(EServiceTemplateSeed templateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        httpCallExecutor.performCall(() -> eServiceTemplateClient.createEServiceTemplate(
            sharedStepsContext.getXCorrelationId(), templateSeed));
        if (httpCallExecutor.getResponseStatus().isError()) {
            return; // a questo punto si prevede che i passi successivi riconoscano l'errore
        }

        CreatedEServiceTemplateVersion creationResponse = (CreatedEServiceTemplateVersion) httpCallExecutor.getResponse();
        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceTemplateClient.getEServiceTemplateVersion(
                    sharedStepsContext.getXCorrelationId(),
                    creationResponse.getId(),
                    creationResponse.getVersionId())),
            res -> res != HttpStatus.NOT_FOUND,
            "There was an error while retrieving the e-service template"
        );

        sharedStepsContext.getEServiceTemplateStepContext().setLastTemplateManaged(new EServiceTemplateInfo(
            templateSeed.getName(),
            templateSeed.getIntendedTarget(),
            templateSeed.getDescription(),
            creationResponse.getId(),
            creationResponse.getVersionId()));
    }

    /** Return a new {@link EServiceTemplateSeed} with only the mandatory fields set
     * @param eServiceMode the risk analysis mode of the e-service
     * @return a new {@link EServiceTemplateSeed} instance
     */
    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode) {
        String templateName = testAssistant.nextEServiceTemplateName();
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
}
