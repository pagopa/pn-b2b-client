package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.springframework.http.HttpStatus;

public class DelegationAcceptStep {
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public DelegationAcceptStep(IProducerDelegationsApiClient producerDelegationsApiClient,
                                IDelegationApiClient delegationApiClient,
                                SharedStepsContext sharedStepsContext) {
        this.producerDelegationsApiClient = producerDelegationsApiClient;
        this.delegationApiClient = delegationApiClient;
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @And("l'utente accetta la delega")
    public void userAcceptTheDelegation() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        approveDelegation();
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) waitUntilDelegationIsApprove();
    }

    @And("l'ente {string} accetta la delega")
    public void delegationIsAcceptedByTenant(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        approveDelegation();
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) waitUntilDelegationIsApprove();
    }

    private void approveDelegation() {
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.approveDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    public void waitUntilDelegationIsApprove() {
        // wait until delegation is correctly approved
        commonUtils.makePolling(
                () -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())),
                res ->  res.getState().equals(DelegationState.ACTIVE),
                "There was an error while accepting the delegation!"
        );
    }
}
