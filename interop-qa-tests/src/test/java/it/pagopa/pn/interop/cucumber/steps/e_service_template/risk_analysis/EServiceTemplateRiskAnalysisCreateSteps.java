package it.pagopa.pn.interop.cucumber.steps.e_service_template.risk_analysis;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;

/** Cucumber steps involving risk analyses of E-service templates */
@Data
public class EServiceTemplateRiskAnalysisCreateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EasyRandom easyRandom;

    public EServiceTemplateRiskAnalysisCreateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
    }

    @Given("l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo")
    public void addRiskAnalysisToEServiceTemplateSuccessfully() {
        testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully();
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template")
    public void addRiskAnalysisToEServiceTemplate() {
        testAssistant.addRiskAnalysisToEServiceTemplate();
    }

    @When("l'utente tenta la creazione di una risk analysis indicando una specifica vuota")
    public void addRiskAnalysisWithEmptySpecToEServiceTemplate() {
        testAssistant.addRiskAnalysisToEServiceTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), new EServiceTemplateRiskAnalysisSeed());
    }

    @When("l'utente tenta l'aggiunta di una risk analysis a un e-service template inesistente")
    public void addRiskAnalysisToNonExistentEServiceTemplate() {
        EServiceTemplateRiskAnalysisSeed riskAnalysisSeed = easyRandom.nextObject(EServiceTemplateRiskAnalysisSeed.class);
        String tenantType = sharedStepsContext.getTenantType();
        String kind = sharedStepsContext.getIdentityService().getKind(tenantType);
        riskAnalysisSeed.setTenantKind(TenantKind.fromValue(kind));

        testAssistant.addRiskAnalysisToEServiceTemplate(UUID.randomUUID(), riskAnalysisSeed);
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template specificando lo stesso nome")
    public void addRiskAnalysisToEServiceTemplateWithSameName() {
        String tenantType = sharedStepsContext.getTenantType();
        String kind = sharedStepsContext.getIdentityService().getKind(tenantType);
        EServiceTemplateRiskAnalysisSeed sameNameRiskAnalysisSeed = easyRandom
            .nextObject(EServiceTemplateRiskAnalysisSeed.class)
            .name(sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysis().getName())
            .tenantKind(TenantKind.fromValue(kind));
        testAssistant.addRiskAnalysisToEServiceTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), sameNameRiskAnalysisSeed);
    }

    @Then("l'aggiunta della risk analysis all'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisAddedToEServiceTemplate() {
        testAssistant.checkRiskAnalysisAddedToEServiceTemplate();
    }
}
