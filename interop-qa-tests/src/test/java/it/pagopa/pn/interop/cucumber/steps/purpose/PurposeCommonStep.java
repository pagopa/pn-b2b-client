package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PurposeCommonStep {
    private final CommonUtils commonUtils;
    private final DataPreparationService dataPreparationService;
    private final SharedStepsContext sharedStepsContext;

    public PurposeCommonStep(CommonUtils commonUtils,
                             DataPreparationService dataPreparationService,
                             SharedStepsContext sharedStepsContext) {
        this.commonUtils = commonUtils;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("{string} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(String tenantType, int n, String purposeVersionState) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        UUID consumerId = commonUtils.getOrganizationId(tenantType);
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
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        PurposeCommonContext purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
        dataPreparationService.rejectPurposeVersion(UUID.fromString(purposeCommonContext.getPurposeId()), UUID.fromString(purposeCommonContext.getWaitingForApprovalVersionId()));
    }

}
