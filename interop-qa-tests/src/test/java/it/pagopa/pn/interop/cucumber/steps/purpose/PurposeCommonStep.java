package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.domain.CreatedEserviceVersion;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Qualifier;

import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PurposeCommonStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;
    private final SharedStepsContext sharedStepsContext;
    private final IPurposeApiClient purposeApiClient;
    private final PollingService pollingService;


    public PurposeCommonStep(ClientTokenConfigurator clientTokenConfigurator,
                             @Qualifier("interopIdentityService") IdentityService identityService,
                             BFFDataPreparationService dataPreparationService,
                             SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.identityService = identityService;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @Given("il {delegationRole} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(DelegationRole delegationRole, int n, String purposeVersionState) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        tenantHasAlreadyCreateFinalizationWithStatus(tenantType, n, purposeVersionState);
    }

    @Given("il {delegationRole} visualizza la finalità creata")
    public void tenantViewCreatedPurpose(DelegationRole delegationRole) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        purposeApiClient.getPurpose(
            
            UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()));
    }

    @Given("{string} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(String tenantType, int n, String purposeVersionState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID consumerId = identityService.getOrganizationId(tenantType);
        createFinalizationWithGivenStatus(consumerId, tenantType, n, purposeVersionState, null);
    }

    @Given("{string} ha già creato {int} finalità in stato {string} per quell'eservice con flagPersonalData impostato a {string}")
    public void tenantHasAlreadyCreateFinalizationWithStatus(String tenantType, int n, String purposeVersionState, String flagPersonalData) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID consumerId = identityService.getOrganizationId(tenantType);
        createFinalizationWithGivenStatus(consumerId, tenantType, n, purposeVersionState, null);
    }

    @Given("{string} ha già pubblicato quella versione di e-service")
    public void publishDescriptor(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.publishDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        );
    }

    @Given("{string} ha già creato una finalità in stato {string} per quell'eservice associando quell'analisi del rischio creata dall'erogatore")
    public void createPurposeAndAddRiskAnalysis(String tenantType, String purposeState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID consumerId = identityService.getOrganizationId(tenantType);

        dataPreparationService.createPurposeWithGivenState(
                sharedStepsContext.getTestSeed(),
                EServiceMode.RECEIVE,
                PurposeVersionState.valueOf(purposeState),
                TEServiceMode.builder()
                        .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                        .consumerId(consumerId)
                        .riskAnalysisId(sharedStepsContext.getRiskAnalysisCommonContext().getRiskAnalysisId())
                        .build()
        );
    }

    @Given("{string} ha già creato un'analisi del rischio per quell'e-service")
    public void createRiskAnalysis(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);

        UUID riskAnalysisId = dataPreparationService.addRiskAnalysisToEService(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                new EServiceRiskAnalysisSeed()
                        .name(riskAnalysis.getName())
                        .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm())
        );
        sharedStepsContext.getRiskAnalysisCommonContext().setRiskAnalysisId(riskAnalysisId);
    }

    @Then("si ottiene status code {int} e la lista di {int} finalità")
    public void verifyStatusAndPurposeList(int statusCode, int count) {
        Purposes purposes = (Purposes) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(statusCode, sharedStepsContext.getHttpCallExecutor().getResponseStatus().value());
        Assertions.assertEquals(count, purposes.getResults().size());
    }

    @Given("{string} ha già creato una finalità in stato {string} per quell'e-service contenente la keyword {string}")
    public void createPurposeWithStateWithKeyword(String tenantType, String purposeState, String keyword) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID consumerId = identityService.getOrganizationId(tenantType);

        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        String title = String.format("purpose %d - %d - %s", sharedStepsContext.getTestSeed(), new Random().nextInt(), keyword);

        dataPreparationService.createPurposeWithGivenState(
                sharedStepsContext.getTestSeed(),
                EServiceMode.DELIVER,
                PurposeVersionState.fromValue(purposeState),
                TEServiceMode.builder()
                        .title(title)
                        .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                        .consumerId(consumerId)
                        .riskAnalysisFormSeed(
                                new RiskAnalysisFormSeed()
                                        .version(riskAnalysis.getRiskAnalysisForm().getVersion())
                                        .answers(riskAnalysis.getRiskAnalysisForm().getAnswers())
                        )
                        .build()
        );
    }

    @Given("per conto del {delegationRole}, il {delegationRole} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(DelegationRole delegationRole1, DelegationRole delegationRole2, int n, String purposeVersionState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole1);
        UUID consumerId = identityService.getOrganizationId(tenantType);
        createFinalizationWithGivenStatus(consumerId, tenantType, n, purposeVersionState,
                new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    public void createFinalizationWithGivenStatus(UUID consumerId, String tenantType, int n, String purposeVersionState, DelegationRef delegationRef) {
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        PurposeCommonContext purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
        for (int index = 0; index < n; index++) {
            dataPreparationService.createPurposeWithGivenState(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                    EServiceMode.DELIVER, PurposeVersionState.fromValue(purposeVersionState),
                    TEServiceMode.builder()
                            .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                            .consumerId(consumerId)
                            .riskAnalysisFormSeed(riskAnalysis.getRiskAnalysisForm())
                            .build(),
                    delegationRef);

            purposeCommonContext.getPurposesIds().add(purposeCommonContext.getPurposeId());
            purposeCommonContext.getCurrentVersionIds().add(purposeCommonContext.getVersionId());
            purposeCommonContext.getWaitingForApprovalVersionIds().add(purposeCommonContext.getWaitingForApprovalVersionId());
        }

        // Get the last element from the lists
        List<String> purposesIds = purposeCommonContext.getPurposesIds();
        List<String> currentVersionIds = purposeCommonContext.getCurrentVersionIds();
        List<String> waitingForApprovalVersionIds = purposeCommonContext.getWaitingForApprovalVersionIds();
        purposeCommonContext.setPurposeId((purposesIds.isEmpty()) ? null : purposesIds.get(purposesIds.size() - 1));
        purposeCommonContext.setVersionId((currentVersionIds.isEmpty()) ? null : currentVersionIds.get(currentVersionIds.size() - 1));
        purposeCommonContext.setWaitingForApprovalVersionId((waitingForApprovalVersionIds.isEmpty()) ? null : waitingForApprovalVersionIds.get(waitingForApprovalVersionIds.size() - 1));

    }

    @Given("{string} ha già rifiutato l'aggiornamento della stima di carico per quella finalità")
    public void tenantHasAlreadyRejectedLoadEstimateUpdateForPurpose(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        PurposeCommonContext purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
        dataPreparationService.rejectPurposeVersion(UUID.fromString(purposeCommonContext.getPurposeId()), UUID.fromString(purposeCommonContext.getWaitingForApprovalVersionId()));
    }

    @Given("{string} ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service")
    public void requireUpdateEffortEstimation(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        CreatedEserviceVersion createdEserviceVersion = dataPreparationService.createNewPurposeVersion(
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                new PurposeVersionSeed().dailyCalls(51)
        );
        sharedStepsContext.getPurposeCommonContext().setWaitingForApprovalVersionId(createdEserviceVersion.getWaitingForApprovalVersionId().toString());
    }

    @Given("{string} ha già portato la finalità in stato {string}")
    public void moveDescriptorToState(String tenantTpye, String desiredPurposeState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantTpye, null));
        switch (desiredPurposeState) {
            case "ARCHIVED" -> dataPreparationService.archivePurpose(
                    UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                    UUID.fromString(sharedStepsContext.getPurposeCommonContext().getVersionId())
            );
            case "SUSPENDED" -> dataPreparationService.suspendPurpose(
                    UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                    UUID.fromString(sharedStepsContext.getPurposeCommonContext().getVersionId()),
                    null
            );
            default -> throw new IllegalArgumentException("Passed wrong desired purpose state!");
        }
    }

    @And("il {delegationRole} controlla che la finalità sia stata archiviata")
    public void purposeIsArchived(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())),
                res -> Optional.ofNullable(res).map(Purpose::getCurrentVersion).map(PurposeVersion::getState).filter(state -> state.equals(PurposeVersionState.ARCHIVED)).isPresent(),
                "The purpose was not archived"
        );
    }

    @Then("si ottiene status code {int} e il template in versione {string}")
    public void verifyStatusCodeAndTemplateVersion(int statusCode, String expectedVersion) {
        RiskAnalysisFormConfig riskAnalysisFormConfig = (RiskAnalysisFormConfig) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(statusCode, sharedStepsContext.getHttpCallExecutor().getResponseStatus().value());
        Assertions.assertEquals(expectedVersion, riskAnalysisFormConfig.getVersion());
    }

}
