package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.DelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

public class DelegateCommonStep {
    private final CommonUtils commonUtils;
    private final IDelegationApiClient delegationApiClient;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public DelegateCommonStep(CommonUtils commonUtils,
                              IDelegationApiClient delegationApiClient,
                              SharedStepsContext sharedStepsContext,
                              HttpCallExecutor httpCallExecutor) {
        this.commonUtils = commonUtils;
        this.delegationApiClient = delegationApiClient;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = httpCallExecutor;
    }

    @And("la delega è stata creata correttamente")
    public void delegationIsPresent() {
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId()))),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while creating the delegation!"
        );
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
