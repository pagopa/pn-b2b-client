package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;

import java.util.Optional;
import java.util.UUID;

public class PurposeRejectStep {
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IPurposeApiClient purposeApiClient;

    public PurposeRejectStep(SharedStepsContext sharedStepsContext,
                             IPurposeApiClient purposeApiClient) {
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.purposeApiClient = purposeApiClient;
    }

    @When("l'utente rifiuta la finalità aggiungendo una motivazione")
    public void userRejectsPurposeWithReason() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        String versionId = sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId() != null
                ? sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId()
                : sharedStepsContext.getPurposeCommonContext().getVersionId();

        httpCallExecutor.performCall(() -> purposeApiClient.rejectPurposeVersion(sharedStepsContext.getXCorrelationId(),
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                UUID.fromString(versionId),
                new RejectPurposeVersionPayload().rejectionReason("Motivazione di rifiuto")));
    }
}
