package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class AgreementCreationStep {
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final DataPreparationService dataPreparationService;

    public AgreementCreationStep(SharedStepsContext sharedStepsContext,
                                 DataPreparationService dataPreparationService) {
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha già rifiutato quella richiesta di fruizione")
    public void tenantHasDeclinedThatRequest(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        dataPreparationService.rejectAgreement(sharedStepsContext.getAgreementId());
    }

    @Given("{string} ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione")
    public void requestForServiceAlreadySubmittedAndPendingApproval(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        UUID agreementId = dataPreparationService.createAgreement(sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId());
        sharedStepsContext.setAgreementId(agreementId);

        dataPreparationService.submitAgreement(agreementId, AgreementState.PENDING);

    }
}
