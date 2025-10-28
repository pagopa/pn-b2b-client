package it.pagopa.pn.interop.cucumber.steps.llgg;

import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import org.assertj.core.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AdeguamentoLineeGuidaSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public AdeguamentoLineeGuidaSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("verifica che la versione 3.1 dell'analisi del rischio includa domande inerenti all'uso di dati personali")
    public void checkRiskAnalysis3_1() {
        this.userRequireTemplateVersionAndCheck("3.1", (risk) -> {
            List<String> questionIds = risk.getQuestions()
                    .stream()
                    .map(q -> q.getId())
                    .collect(Collectors.toList());

            Assertions.assertThat(questionIds)
                    .as("Verifica che le domande includano l'uso di dati personali e di dati personali di terze parti")
                    .contains("usesPersonalData", "usesThirdPartyPersonalData", "usesThirdPartyConfidentialData");
        });
    }

    @When("l'utente aggiunge un'analisi del rischio con un flag relativo ai dati personali impostato a {string}")
    public void addCustomRiskAnalysis(String flagCondition) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysis customRiskAnalysis;

        if (flagCondition.equals("true")) {
            customRiskAnalysis = FileUtils.readJsonAs("classpath:it/pagopa/pn/cucumber/llgg/personal-data-true-risk-analysis.json", RiskAnalysis.class);
        } else if (flagCondition.equals("false")) {
            customRiskAnalysis = FileUtils.readJsonAs("classpath:it/pagopa/pn/cucumber/llgg/personal-data-false-risk-analysis.json", RiskAnalysis.class);
        } else {
            throw new RuntimeException(String.format("Flag condition non riconosciuto: %s", flagCondition));
        }

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().addRiskAnalysisToEService(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        new it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed()
                                .name(customRiskAnalysis.getName())
                                .riskAnalysisForm(customRiskAnalysis.getRiskAnalysisForm())
                )
        );
    }

    @When("viene settato il personalDataFlag a {string} passando un {string} inesistente")
    public void updatePersonalDataFlagAfterPublication(String flagPersonalData, String target) {

        UUID id = UUID.randomUUID();
        boolean isEservice = target.equalsIgnoreCase("eServiceId");

        EServicePersonalDataFlagUpdateSeed eserviceSeed = new EServicePersonalDataFlagUpdateSeed();
        eserviceSeed.setPersonalData(flagPersonalData.equalsIgnoreCase("true"));

        EServiceTemplatePersonalDataFlagUpdateSeed eServiceTemplateSeed = new EServiceTemplatePersonalDataFlagUpdateSeed();
        eServiceTemplateSeed.setPersonalData(flagPersonalData.equalsIgnoreCase("true"));

        httpCallExecutor.performCall(
                () -> {
                    if(isEservice) clientTokenConfigurator.getEServiceClient().updateEServicePersonalDataFlagAfterPublication(id,eserviceSeed);
                    else clientTokenConfigurator.getEServiceTemplateClient().updateEServiceTemplatePersonalDataFlagAfterPublication(id,eServiceTemplateSeed);
                }
        );
    }

    @When("viene settato il personalDataFlag a {string} nell'eservice appena creato")
    public void updatePersonalDataFlag(String flagPersonalData) {

        UUID eserviceId = this.sharedStepsContext.getEServicesCommonContext().getEserviceId();

        EServicePersonalDataFlagUpdateSeed seed = new EServicePersonalDataFlagUpdateSeed();
        seed.setPersonalData(flagPersonalData.equalsIgnoreCase("true"));

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServicePersonalDataFlagAfterPublication(
                        eserviceId,seed
                )
        );
    }

    @When("viene settato il personalDataFlag a {string} passando un {string} inesistente e un token invalido")
    public void updatePersonalDataFlagAfterPublicationWithInvalidToken(String flagPersonalData, String target) {

        UUID id = UUID.randomUUID();
        boolean isEservice = target.equalsIgnoreCase("eServiceId");

        EServicePersonalDataFlagUpdateSeed eserviceSeed = new EServicePersonalDataFlagUpdateSeed();
        eserviceSeed.setPersonalData(flagPersonalData.equalsIgnoreCase("true"));

        EServiceTemplatePersonalDataFlagUpdateSeed eServiceTemplateSeed = new EServiceTemplatePersonalDataFlagUpdateSeed();
        eServiceTemplateSeed.setPersonalData(flagPersonalData.equalsIgnoreCase("true"));

        httpCallExecutor.performCall(
                () -> {
                    if(isEservice) clientTokenConfigurator.getEServiceClient().updateEServicePersonalDataFlagAfterPublicationWithInvalidToken(id,eserviceSeed);
                    else clientTokenConfigurator.getEServiceTemplateClient().updateEServiceTemplatePersonalDataFlagAfterPublicationWithInvalidToken(id,eServiceTemplateSeed);
                }
        );
    }

    public void userRequireTemplateVersionAndCheck(String version, Consumer<RiskAnalysisFormConfig> action) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysisFormConfig riskAnalysis;

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().retrieveRiskAnalysisConfigurationByVersion(
                        version, sharedStepsContext.getEServicesCommonContext().getEserviceId()
                )
        );

        riskAnalysis = (RiskAnalysisFormConfig) httpCallExecutor.getResponse();
        action.accept(riskAnalysis);
    }
}
