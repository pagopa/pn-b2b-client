package it.pagopa.pn.interop.cucumber.steps.e_service_template.crud;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template */
@Data
public class EServiceTemplateUpdateSteps {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;

    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
        BFFDataPreparationService dataPreparationService,
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

    @When("l'utente tenta delle modifiche all'e-service template")
    public void updateEServiceTemplate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        lastTemplateUpdateSeed = new UpdateEServiceTemplateSeed()
            .name(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getName() + " - modificato")
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
            .name(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getName())
            .intendedTarget("Nuova intended target")
            .description("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE);
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        updateEServiceTemplate(eServiceTemplateId, sameNameUpdateSeed);
    }

    @When("l'utente tenta di modificare l'e-service template specificando un nome vuoto")
    public void updateEServiceTemplateWithEmptyName() {
        UpdateEServiceTemplateSeed emptyNameUpdateSeed = new UpdateEServiceTemplateSeed()
            .name("");
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
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
        updateEServiceTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), new UpdateEServiceTemplateSeed());
    }

    @When("l'utente aggiorna la descrizione dell'e-service template in stato {eServiceTemplateVersionState} con una descrizione di {int} caratteri")
    public void updateEServiceTemplateDescription(EServiceTemplateVersionState eServiceTemplateVersionState, Integer descriptionLength) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        String description = (new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue();

        if (eServiceTemplateVersionState.equals(EServiceTemplateVersionState.PUBLISHED)) {
            EServiceTemplateDescriptionUpdateSeed seed = new EServiceTemplateDescriptionUpdateSeed()
                    .description(description);
            getHttpCallExecutor().performCall(
                    () -> eServiceTemplateClient.updateEServiceTemplateDescription(eServiceTemplateId, seed)
            );
        } else if(eServiceTemplateVersionState.equals(EServiceTemplateVersionState.DRAFT)) {
            EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
            UpdateEServiceTemplateSeed seed = new UpdateEServiceTemplateSeed()
                    .description(description)
                    .name(lastTemplateManaged.getName())
                    .technology(lastTemplateManaged.getTechnology())
                    .mode(lastTemplateManaged.getMode())
                    .intendedTarget(lastTemplateManaged.getIntendedTarget())
                    .personalData(lastTemplateManaged.getPersonalData());
            updateEServiceTemplate(eServiceTemplateId, seed);
        } else {
            fail("unhandled case: %s".formatted(eServiceTemplateVersionState));
        }

        if (getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().setEServiceDescription(description);
        }
    }

    @Then("le modifiche al template sono state applicate correttamente")
    public void checkEServiceTemplateUpdate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall( // TODO è stata introdotta la API specifica per i template, refattorizzare usando quella (non solo qui) per i check che riguardano solo i template
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
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

    private void updateEServiceTemplate(UUID eServiceTemplateId, UpdateEServiceTemplateSeed sameNameUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateWithHttpInfo(
                eServiceTemplateId,
                sameNameUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    private boolean areConsistent(UpdateEServiceTemplateSeed lastUpdate, EServiceTemplateDetails retrievedTemplate) {
        return lastUpdate.getName().equals(retrievedTemplate.getName()) &&
            lastUpdate.getIntendedTarget().equals(retrievedTemplate.getIntendedTarget()) &&
            lastUpdate.getDescription().equals(retrievedTemplate.getDescription()) &&
            lastUpdate.getTechnology().equals(retrievedTemplate.getTechnology()) &&
            lastUpdate.getMode().equals(retrievedTemplate.getMode());
    }
}
