package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.lifecycle;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving publishing, suspension and reactivation of E-service template versions */
@Data
public class EServiceTemplateVersionPublishSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;

    public EServiceTemplateVersionPublishSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
    }

    @When("l'utente tenta la pubblicazione della versione dell'e-service template")
    public void publishEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        publishEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la pubblicazione di una versione di un e-service template inesistente")
    public void publishNonExistentEServiceTemplate() {
        publishEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la pubblicazione di una versione di un e-service template indicando un identificativo vuoto")
    public void publishUnspecifiedEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        publishEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), null);
    }

    @When("l'utente tenta la pubblicazione di una versione inesistente di un e-service template")
    public void publishNonExistentEServiceTemplateVersion() {
        publishEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID());
    }

    @Then("la pubblicazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionPublished() {
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
                res -> nonNull(res.getBody()) && res.getBody().getState() == EServiceTemplateVersionState.PUBLISHED,
                "La versione dell'e-service template non è stata pubblicata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata pubblicata correttamente");
        }
    }

    /* TODO 17/03/2025: questo e tutti gli altri metodi che conducono al dataPreparationService
    *   per effettuare op. inerenti e-service templates sono datati, risalenti a prima che si
    *   affermasse la strategia dei 3 step di tipo:
    *       - l'utente tenta la [lettura, scrittura o altra operzione inerente la risorsa...]
    *       - la [lettura, scrittura o altra operzione inerente la risorsa...] è stata effettuata correttamente
    *       - l'utente effettua la [lettura, scrittura o altra operzione inerente la risorsa...] con successo
    *   Tutti gli step che fanno capo a quei metodi del dataPreparationService andrebbero riformulati
    *   per seguire la strategia di sopra, e quindi quei metodi andrebbero alla fine rimossi. */
    @When("l'utente effettua la pubblicazione dell'e-service template")
    public void publishEServiceTemplate() {
        testAssistant.publishEServiceTemplate();
    }

    private void publishEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.publishEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId));
    }
}
