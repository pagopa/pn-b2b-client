package it.pagopa.pn.interop.cucumber.steps.e_service_template.crud;

import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template */
/* DEV. NOTE 14/03/2025: non ci sono step che effettuano la cancellazione poiché la cancellazione
* di un template avviene quando vengono cancellate tutte le VERSIONI di un template. Non ci
* sopo APIs che effettuano la cancellazione di un template, ma solo delle sue versioni. */
@Data
public class EServiceTemplateDeleteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateStepContext templateContext;

    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;

    /* TODO 13/03/2025: molte di queste assegnazioni sono condivise da tutte la classi di step.
    *   Provare a racchiudere il codice comune in un costruttore in una classe astratta da far
    *   ereditare a questa e a tutte le altre. */
    public EServiceTemplateDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateStepContext templateContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.templateContext = templateContext;
    }

    // TODO gli step sono pieni di pattern ricorrenti, questo step ne è un'esempio. Potrebbero essere astratti e portati in classi di utility esterne.
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
}
