package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;

import java.util.List;

public class EServiceRiskAnalysisAdditionSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
    private final DataPreparationService dataPreparationService;
    private final EServicesCommonContext eServicesCommonContext;

    public EServiceRiskAnalysisAdditionSteps(ClientTokenConfigurator clientTokenConfigurator,
                                        SharedStepsContext sharedStepsContext,
                                        DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
    }

    @Given("{string} ha già creato un e-service in modalità {string} con un descrittore in DRAFT")
    public void tenantHasAlreadyCreatedEServiceWithModeAndDraftDescriptor(String tenantType, String mode) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().mode(EServiceMode.fromValue(mode)),
                new UpdateEServiceDescriptorSeed()
        );
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());

    }

    @When("l'utente aggiunge un'analisi del rischio")
    public void addRiskAnalysis() {
        RiskAnalysis eServiceRiskAnalysisSeed = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().addRiskAnalysisToEService(
                        eServicesCommonContext.getEserviceId(),
                        new EServiceRiskAnalysisSeed()
                                .name(eServiceRiskAnalysisSeed.getName())
                                .riskAnalysisForm(eServiceRiskAnalysisSeed.getRiskAnalysisForm())
                )
        );
    }

    @When("l'utente aggiunge un'analisi del rischio non corretta per la tipologia di ente")
    public void addWrongRiskAnalysis() {
        // We want to get the wrong risk analysis template, so we need to invert the tenantType
        String tenantType = (List.of("GSP", "Privato").contains(sharedStepsContext.getTenantType())) ? "PA1" : "Privato";
        RiskAnalysis eServiceRiskAnalysisSeed = dataPreparationService.getRiskAnalysis(tenantType, true);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().addRiskAnalysisToEService(eServicesCommonContext.getEserviceId(),
                        new EServiceRiskAnalysisSeed()
                                .name(eServiceRiskAnalysisSeed.getName())
                                .riskAnalysisForm(eServiceRiskAnalysisSeed.getRiskAnalysisForm())
                )
        );
    }

    @When("l'utente aggiunge un'analisi del rischio con versione template non aggiornata")
    public void addRiskAnalysisWithWrongVersion() {
        RiskAnalysis eServiceRiskAnalysisSeed = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
        String outdatedVersion = String.format("%.1f", Integer.parseInt(eServiceRiskAnalysisSeed.getRiskAnalysisForm().getVersion()) - 1);
        eServiceRiskAnalysisSeed.getRiskAnalysisForm().setVersion(outdatedVersion);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().addRiskAnalysisToEService(
                        eServicesCommonContext.getEserviceId(),
                        new EServiceRiskAnalysisSeed()
                                .name(eServiceRiskAnalysisSeed.getName())
                                .riskAnalysisForm(eServiceRiskAnalysisSeed.getRiskAnalysisForm())
                )
        );
    }


}
