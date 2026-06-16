package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DescriptorPublicationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;
    private final IdentityService identityService;

    public DescriptorPublicationSteps(ClientTokenConfigurator clientTokenConfigurator,
                                      SharedStepsContext sharedStepsContext,
                                      BFFDataPreparationService dataPreparationService) {
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

    @Given("{string} ha già caricato un'interfaccia di callback per quel descrittore")
    public void loadCallbackDescriptorInterface(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID callbackInterfaceId = dataPreparationService.addCallbackInterfaceToDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
        sharedStepsContext.getEServicesCommonContext().setCallbackInterfaceId(callbackInterfaceId);
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

    // TODO: bisogna rifattorizzare il codice per riutlizzare in maniera corretta
    @Given("{string} ha già creato un e-service in modalità {string} con un descrittore in stato {string} e flag dati personali a {string}")
    public void createEServiceWithModeAndStateAndPersonaDataFlag(String tenantType, String mode, String eServiceDescriptorState, String personalDataFlag) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptorWithCustomPersonalData(
                new EServiceSeed().mode(EServiceMode.fromValue(mode)),
                new UpdateEServiceDescriptorSeed(),
                personalDataFlag.equals("undefined") ? null : personalDataFlag.equalsIgnoreCase("true")
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
    @When("l'utente pubblica l'e-service")
    public void userPublishDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        publishDescriptor(
                sharedStepsContext.getHttpCallExecutor(),
                clientTokenConfigurator.getEServiceClient(),
                sharedStepsContext.getEServicesCommonContext());
    }

    public static void publishDescriptor(IHttpExecutor httpExecutor, IEServiceClient client, EServicesCommonContext context) {
        httpExecutor.performCall(
                () -> client.publishDescriptor(
                        context.getEserviceId(),
                        context.getDescriptorId()
                )
        );
    }

    @Given("l'utente ha compilato parzialmente l'analisi del rischio")
    public void userPartiallyCompileRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), false);
        dataPreparationService.addRiskAnalysisToEService(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                new EServiceRiskAnalysisSeed().name(riskAnalysis.getName()).riskAnalysisForm(riskAnalysis.getRiskAnalysisForm()));
    }
}
