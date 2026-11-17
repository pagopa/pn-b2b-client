package it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template;

import io.cucumber.java.en.And;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.RiskAnalysisFormTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.RiskAnalysisTemplateAnswerSeed;
import it.pagopa.interop.purpose.service.IM2MPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.DataPreparationServiceTemplate.RiskAnalysisExample.PERSONAL_DATA;

public class PurposeTemplateUpdateSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IM2MPurposeTemplateClient m2mPurposeTemplateClient;
    private final M2MDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;

    public PurposeTemplateUpdateSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.m2mPurposeTemplateClient = clientTokenConfigurator.getM2mPurposeTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @And("viene modificato il purpose template destinato a enti {string} indicando un URL casuale come indirizzo dell'informativa sul trattamento dei dati personali")
    public void updatePurposeTemplateWithUrl(String tenantType) {
        String targetTenantKind = sharedStepsContext.getIdentityService().getKind(tenantType);
        RiskAnalysisFormTemplateSeed templateSeed = dataPreparationService.getRiskAnalysisTemplateByExample(targetTenantKind, PERSONAL_DATA);
        List<String> suggestedValues = List.of("https://example-%s.com/privacy-policy".formatted(RandomStringUtils.insecure().nextAlphanumeric(3)));
        RiskAnalysisTemplateAnswerSeed answerSeed = new RiskAnalysisTemplateAnswerSeed()
                .editable(false)
                .suggestedValues(suggestedValues);
        templateSeed.getAnswers().put("policyProvidedOnlineLink", answerSeed);
        httpCallExecutor.performCall(() -> m2mPurposeTemplateClient.replacePurposeTemplateRiskAnalysis(
                sharedStepsContext.getPurposeTemplateContext().getPurposeTemplateId(),
                templateSeed
        ));
    }
}

