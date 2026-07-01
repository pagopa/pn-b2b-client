package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.collections4.IterableUtils.isEmpty;

public class EServiceRiskAnalysisAdditionSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;
    private final EServicesCommonContext eServicesCommonContext;

    public EServiceRiskAnalysisAdditionSteps(ClientTokenConfigurator clientTokenConfigurator,
                                        SharedStepsContext sharedStepsContext,
                                        BFFDataPreparationService dataPreparationService) {
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
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
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

    @When("l'utente aggiunge con successo un'analisi del rischio coerente con il tenant kind {string}")
    public void successfullyAddRiskAnalysisByTenantKind(String tenantKind) {
        addRiskAnalysisByTenantKind(tenantKind);
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
            UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
            sharedStepsContext.getPollingService().makePolling(
                    () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                    desc -> !isEmpty(desc.getEservice().getRiskAnalysis()),
                    "Nessuna analisi del rischio rilevata"
            );
        }
    }

    @When("l'utente aggiunge un'analisi del rischio coerente con il tenant kind {string}")
    public void addRiskAnalysisByTenantKind(String tenantKind) {
        List<String> tenantTypes = identityService.getTenantTypesByKind(tenantKind);
        if (isEmpty(tenantTypes)) {
            throw new IllegalArgumentException("Nessun tenant type trovato per il tenant kind: " + tenantKind);
        }

        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantTypes.get(0), null));
        RiskAnalysis eServiceRiskAnalysisSeed = dataPreparationService.getRiskAnalysis(tenantKind, true);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
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
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String tenantType = sharedStepsContext.getTenantType();
        RiskAnalysis eServiceRiskAnalysisSeed = dataPreparationService.getRiskAnalysis(tenantType, false);
        var answers = eServiceRiskAnalysisSeed.getRiskAnalysisForm().getAnswers();
        answers.remove("usesPersonalData");

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
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysis eServiceRiskAnalysisSeed = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
        String outdatedVersion = String.format("%d.1f", (int) Double.parseDouble(eServiceRiskAnalysisSeed.getRiskAnalysisForm().getVersion()) - 1);
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
