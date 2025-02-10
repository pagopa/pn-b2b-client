package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PurposeCommonStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final SharedStepsContext sharedStepsContext;
    private final IPurposeApiClient purposeApiClient;


    public PurposeCommonStep(ClientTokenConfigurator clientTokenConfigurator,
                             IdentityService identityService,
                             DataPreparationService dataPreparationService,
                             SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.identityService = identityService;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
    }

    @Given("il {delegationRole} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(DelegationRole delegationRole, int n, String purposeVersionState) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        tenantHasAlreadyCreateFinalizationWithStatus(tenantType, n, purposeVersionState);
    }

    @Given("{string} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(String tenantType, int n, String purposeVersionState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID consumerId = identityService.getOrganizationId(tenantType);
        createFinalizationWithGivenStatus(consumerId, tenantType, n, purposeVersionState);
    }

    @Given("per conto del {delegationRole}, il {delegationRole} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(DelegationRole delegationRole1, DelegationRole delegationRole2, int n, String purposeVersionState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole1);
        UUID consumerId = identityService.getOrganizationId(tenantType);
        createFinalizationWithGivenStatus(consumerId, tenantType, n, purposeVersionState);
    }

    public void createFinalizationWithGivenStatus(UUID consumerId, String tenantType, int n, String purposeVersionState) {
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        PurposeCommonContext purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
        for (int index = 0; index < n; index++) {
            dataPreparationService.createPurposeWithGivenState(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                    EServiceMode.DELIVER, PurposeVersionState.fromValue(purposeVersionState),
                    TEServiceMode.builder()
                            .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                            .consumerId(consumerId)
                            .riskAnalysisFormSeed(riskAnalysis.getRiskAnalysisForm())
                            .build());

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

    @And("il {delegationRole} controlla che la finalità sia stata archiviata")
    public void purposeIsArchived(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        Purpose purpose = purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(),
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()));
        Optional.ofNullable(purpose)
                .map(Purpose::getCurrentVersion)
                .map(PurposeVersion::getState)
                .filter(state -> state.equals(PurposeVersionState.ARCHIVED))
                .orElseThrow(() -> new IllegalStateException("The purpose was not archived"));



    }

}
