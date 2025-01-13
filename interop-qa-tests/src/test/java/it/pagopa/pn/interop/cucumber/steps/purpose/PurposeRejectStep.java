package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class PurposeRejectStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IPurposeApiClient purposeApiClient;

    public PurposeRejectStep(ClientTokenConfigurator clientTokenConfigurator,
                             SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
    }

    @When("l'utente rifiuta la finalità aggiungendo una motivazione")
    public void userRejectsPurposeWithReason() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String versionId = sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId() != null
                ? sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId()
                : sharedStepsContext.getPurposeCommonContext().getVersionId();

        httpCallExecutor.performCall(() -> purposeApiClient.rejectPurposeVersion(sharedStepsContext.getXCorrelationId(),
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                UUID.fromString(versionId),
                new RejectPurposeVersionPayload().rejectionReason("Motivazione di rifiuto")));
    }
}
