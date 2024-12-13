package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.pn.interop.cucumber.steps.purpose.domain.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.EServicesCommonDomain;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PurposeCommonStep {
    private final CommonUtils commonUtils;
    private final DataPreparationService dataPreparationService;
    private final PurposeCommonContext purposeCommonContext;
    private final EServicesCommonDomain eServicesCommonDomain;

    public PurposeCommonStep(CommonUtils commonUtils,
                             DataPreparationService dataPreparationService,
                             PurposeCommonContext purposeCommonContext,
                             EServicesCommonDomain eServicesCommonDomain) {
        this.commonUtils = commonUtils;
        this.dataPreparationService = dataPreparationService;
        this.purposeCommonContext = purposeCommonContext;
        this.eServicesCommonDomain = eServicesCommonDomain;
    }

    @Given("{string} ha già creato {int} finalità in stato {string} per quell'eservice")
    public void tenantHasAlreadyCreateFinalizationWithStatus(String tenantType, int n, String purposeVersionState) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        UUID consumerId = commonUtils.getOrganizationId(tenantType);
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        for (int index = 0; index < n; index++) {
            dataPreparationService.createPurposeWithGivenState(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                    EServiceMode.DELIVER, PurposeVersionState.fromValue(purposeVersionState),
                    TEServiceMode.builder()
                            .eserviceId(eServicesCommonDomain.getEserviceId())
                            .consumerId(consumerId)
                            .riskAnalysisFormSeed(riskAnalysis.getRiskAnalysisForm())
                            .build());

            purposeCommonContext.getPurposesIds().add(purposeCommonContext.getPurposeId());
            purposeCommonContext.getCurrentVersionIds().add(purposeCommonContext.getVersionId());
            purposeCommonContext.getWaitingForApprovalVersionIds().add(purposeCommonContext.getWaitingForApprovalVersionId());
        }

        // Ottieni l'ultimo elemento dagli elenchi
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
        dataPreparationService.rejectPurposeVersion(UUID.fromString(purposeCommonContext.getPurposeId()), UUID.fromString(purposeCommonContext.getWaitingForApprovalVersionId()));
    }

}
