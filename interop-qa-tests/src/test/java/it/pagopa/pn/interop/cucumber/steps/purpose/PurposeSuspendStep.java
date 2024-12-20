package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class PurposeSuspendStep {
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final IPurposeApiClient purposeApiClient;
    private final HttpCallExecutor httpCallExecutor;

    public PurposeSuspendStep(SharedStepsContext sharedStepsContext,
                              IPurposeApiClient purposeApiClient) {
        this.sharedStepsContext = sharedStepsContext;
        this.purposeApiClient = purposeApiClient;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente sospende quella finalità in stato {string}")
    public void userSuspendsPurposeInState(String state) {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        String versionId =
                "WAITING_FOR_APPROVAL".equals(state) || "REJECTED".equals(state)
                ? sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId()
                : sharedStepsContext.getPurposeCommonContext().getVersionId();
        if (versionId == null) throw new IllegalArgumentException("No versionId found!");

        httpCallExecutor.performCall(() -> purposeApiClient.suspendPurposeVersion(sharedStepsContext.getXCorrelationId(),
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()), UUID.fromString(versionId)));
    }
}
