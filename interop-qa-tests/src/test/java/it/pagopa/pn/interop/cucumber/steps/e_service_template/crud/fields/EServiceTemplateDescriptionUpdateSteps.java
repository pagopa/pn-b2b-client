package it.pagopa.pn.interop.cucumber.steps.e_service_template.crud.fields;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

@Data
public class EServiceTemplateDescriptionUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateStepContext templateContext;
    private final EasyRandom easyRandom;

    private EServiceTemplateDescriptionUpdateSeed lastTemplateDescriptionUpdateSeed;

    public EServiceTemplateDescriptionUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext,
                                EServiceTemplateStepContext templateContext
        ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.templateContext = templateContext;
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template")
    public void editEServiceTemplateDescription() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateDescriptionUpdateSeed = easyRandom.nextObject(
            EServiceTemplateDescriptionUpdateSeed.class);
        editEServiceTemplateDescription(eServiceTemplateId, lastTemplateDescriptionUpdateSeed);
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando la stessa descrizione")
    public void editEServiceTemplateDescriptionWithSameDescription() {
        editEServiceTemplateDescriptionWith(templateContext.getLastTemplateManaged().eServiceDescription());
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateDescriptionWith() {
        editEServiceTemplateDescriptionWith("");
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando NULL")
    public void editEServiceTemplateDescriptionWithNullDescription() {
        editEServiceTemplateDescriptionWith(null);
    }

    @When("l'utente tenta la modifica della descrizione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateDescription() {
        editEServiceTemplateDescription(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class));
    }

    @Then("la modifica della descrizione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateDescriptionEdited() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceTemplateDetails template = res.getBody();
                        return template.getDescription().equals(
                            lastTemplateDescriptionUpdateSeed.getDescription());
                    }
                    return false;
                },
                "La descrizione dell'e-service template non è stata modificata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La descrizione dell'e-service template non è stata modificata correttamente");
        }
    }

    private void editEServiceTemplateDescriptionWith(String description) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class)
            .description(description);
        editEServiceTemplateDescription(eServiceTemplateId, lastTemplateDescriptionUpdateSeed);
    }

    private void editEServiceTemplateDescription(UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed lastTemplateDescriptionUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateDescriptionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateDescriptionUpdateSeed),
            ResponseEntity::getStatusCode);
    }
}