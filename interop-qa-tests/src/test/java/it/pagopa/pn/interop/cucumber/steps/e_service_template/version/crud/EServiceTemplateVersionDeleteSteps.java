package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateInfoMapper;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
@Data
public class EServiceTemplateVersionDeleteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateInfoMapper templateInfoMapper;
    private final DescriptorAttributesMapper descriptorAttributesMapper;

    private UUID lastDeletedVersion;

    public EServiceTemplateVersionDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateInfoMapper templateInfoMapper,
        DescriptorAttributesMapper descriptorAttributesMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateInfoMapper = templateInfoMapper;
        this.descriptorAttributesMapper = descriptorAttributesMapper;
    }

    @Given("l'utente effettua la cancellazione della versione dell'e-service template con successo")
    public void deleteEServiceTemplateVersionSuccessfully() {
        deleteEServiceTemplateVersion();
        checkEServiceTemplateVersionDeleted();
        this.lastDeletedVersion = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId();
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template")
    public void deleteEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().lastVersionId();
        deleteEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template indicando un identificativo vuoto")
    public void deleteUnspecifiedEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        deleteEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id(), null);
    }

    @Then("la cancellazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionDeleted() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
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
        deleteEServiceTemplateVersion(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id(), UUID.randomUUID());
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template già cancellata")
    public void deleteAlreadyDeletedEServiceTemplateVersion() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = this.lastDeletedVersion;
        deleteEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
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
