package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.Optional;
import java.util.UUID;

public class PurposeActivationStep {
    private final IPurposeApiClient purposeApiClient;
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public PurposeActivationStep(IPurposeApiClient purposeApiClient,
                                 CommonUtils commonUtils,
                                 SharedStepsContext sharedStepsContext,
                                 HttpCallExecutor httpCallExecutor) {
        this.purposeApiClient = purposeApiClient;
        this.commonUtils = commonUtils;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente (ri)attiva la finalità in stato {string} per quell'e-service")
    public void userActivatesPurposeInStateForThatEService(String state) throws InterruptedException {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        String versionId = "WAITING_FOR_APPROVAL".equals(state) || "REJECTED".equals(state)
                        ? sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId()
                        : sharedStepsContext.getPurposeCommonContext().getVersionId();
        if (versionId == null) throw new IllegalArgumentException("No versionId found!");
        httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(sharedStepsContext.getXCorrelationId(),
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()), UUID.fromString(versionId)));
        commonUtils.makePolling(() -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())),
                res -> Optional.ofNullable(res.getCurrentVersion()).map(PurposeVersion::getState).filter(status -> status == PurposeVersionState.ACTIVE).isPresent(),
                "There was an error while activating the purpose!");
    }
}