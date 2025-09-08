package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;

import java.util.UUID;

public class PurposeSuspendSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public PurposeSuspendSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente sospende quella finalità in stato {string}")
    public void userSuspendPurposeWithState(String state) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        suspendPurposeWithState(state);
    }

    @When("l'ente {delegationRole} sospende quella finalità in stato {string}")
    public void userSuspendPurposeWithState(DelegationRole delegationRole, String state) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, null));
        suspendPurposeWithState(state);
    }

    private void suspendPurposeWithState(String state) {
        String versionId = ("WAITING_FOR_APPROVAL".equals(state) || "REJECTED".equals(state))
                ? sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId()
                : sharedStepsContext.getPurposeCommonContext().getVersionId();

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().suspendPurposeVersion(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        UUID.fromString(versionId),
                        new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId())
                )
        );
    }
}
