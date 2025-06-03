package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;

import java.util.UUID;

public class DescriptorPublicationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;

    public DescriptorPublicationSteps(ClientTokenConfigurator clientTokenConfigurator,
                                      SharedStepsContext sharedStepsContext,
                                      DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @Given("{string} ha già caricato un'interfaccia per quel descrittore")
    public void loadDescriptorInterface(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        dataPreparationService.addInterfaceToDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
    }

    @Given("{string} ha già creato un e-service in modalità {string} con un descrittore in stato {string}")
    public void createEServiceWithModeAndState(String tenantType, String mode, String eServiceDescriptorState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().mode(EServiceMode.fromValue(mode)),
                new UpdateEServiceDescriptorSeed()
        );
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());

        // If descriptorState is not DRAFT we have to add a completed risk analysis in order to correctly publish the descriptor
        if ("RECEIVE".equalsIgnoreCase(mode) && !"DRAFT".equalsIgnoreCase(eServiceDescriptorState)) {
            RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
            UUID riskAnalysisId = dataPreparationService.addRiskAnalysisToEService(
                    sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                    new EServiceRiskAnalysisSeed()
                            .name(riskAnalysis.getName())
                            .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm())
            );
            sharedStepsContext.getRiskAnalysisCommonContext().setRiskAnalysisId(riskAnalysisId);
        }

        dataPreparationService.bringDescriptorToGivenState(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                EServiceDescriptorState.valueOf(eServiceDescriptorState),
                false
        );
    }

    @When("l'utente pubblica quel descrittore")
    public void userPublishDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().publishDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                )
        );
    }

    @Given("l'utente ha compilato parzialmente l'analisi del rischio")
    public void userPartiallyCompileRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), false);
        dataPreparationService.addRiskAnalysisToEService(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                new EServiceRiskAnalysisSeed().name(riskAnalysis.getName()).riskAnalysisForm(riskAnalysis.getRiskAnalysisForm())
        );
    }
}
