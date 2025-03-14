package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.lang.Math.abs;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionQuotasUpdateSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving quotas of E-service templates */
@Data
public class EServiceTemplateQuotasSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;
    private final EasyRandom easyRandom;

    private EServiceTemplateVersionQuotasUpdateSeed lastTemplateVersionQuotasUpdateSeed;

    public EServiceTemplateQuotasSteps(ClientTokenConfigurator clientTokenConfigurator,
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
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
    }

    @When("l'utente tenta la modifica delle quote della versione dell'e-service template")
    public void editEServiceTemplateVersionQuotas() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        lastTemplateVersionQuotasUpdateSeed = easyRandom.nextObject(
            EServiceTemplateVersionQuotasUpdateSeed.class);
        lastTemplateVersionQuotasUpdateSeed.setVoucherLifespan(abs(lastTemplateVersionQuotasUpdateSeed.getVoucherLifespan()));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsTotal(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer() + 1));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsPerConsumer(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer()));

        editEServiceTemplateVersionQuotas(eServiceTemplateId, eServiceTemplateVersionId, lastTemplateVersionQuotasUpdateSeed);
    }

    @When("l'utente tenta la modifica delle quote della versione dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateVersionQuotasWithEmptySpec() {
        editEServiceTemplateVersionQuotas(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId(),
            new EServiceTemplateVersionQuotasUpdateSeed());
    }

    @When("l'utente tenta la modifica delle quote della versione dell'e-service template specificando un \"dailyCallsTotal\" inferiore a \"dailyCallsPerConsumer\"")
    public void editEServiceTemplateVersionQuotasWithDailyCallsTotalLessThanDailyCallsPerConsumer() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        lastTemplateVersionQuotasUpdateSeed = easyRandom.nextObject(
            EServiceTemplateVersionQuotasUpdateSeed.class);
        lastTemplateVersionQuotasUpdateSeed.setVoucherLifespan(abs(lastTemplateVersionQuotasUpdateSeed.getVoucherLifespan()));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsTotal(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer() - 1));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsPerConsumer(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer()));

        editEServiceTemplateVersionQuotas(eServiceTemplateId, eServiceTemplateVersionId, lastTemplateVersionQuotasUpdateSeed);
    }

    @When("l'utente tenta la modifica delle quote della versione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateVersionQuotas() {
        editEServiceTemplateVersionQuotas(UUID.randomUUID(), UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateVersionQuotasUpdateSeed.class));
    }

    @When("l'utente tenta la modifica delle quote di una versione inesistente dell'e-service template")
    public void editEServiceTemplateNonExistentVersionQuotas() {
        editEServiceTemplateVersionQuotas(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateVersionQuotasUpdateSeed.class));
    }

    @Then("la modifica delle quote della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionQuotasEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall( // TODO usare la versione invece del template
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        templateContext.getLastTemplateManaged().id(),
                        templateContext.getLastTemplateManaged().lastVersionId()),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceTemplateVersionDetails version = res.getBody();
                        return this.areConsistent(version, lastTemplateVersionQuotasUpdateSeed);
                    }
                    return false;
                },
                "Le quote dell'e-service template non sono state modificate correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("Le quote dell'e-service template non sono state modificate correttamente");
        }
    }

    private void editEServiceTemplateVersionQuotas(UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateVersionQuotasUpdateSeed lastTemplateVersionQuotasUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersionQuotasWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                lastTemplateVersionQuotasUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    private boolean areConsistent(EServiceTemplateVersionDetails version, EServiceTemplateVersionQuotasUpdateSeed lastUpdate) {
        return version.getDailyCallsPerConsumer().equals(lastUpdate.getDailyCallsPerConsumer()) &&
            version.getDailyCallsTotal().equals(lastUpdate.getDailyCallsTotal()) &&
            version.getVoucherLifespan().equals(lastUpdate.getVoucherLifespan());
    }
}
