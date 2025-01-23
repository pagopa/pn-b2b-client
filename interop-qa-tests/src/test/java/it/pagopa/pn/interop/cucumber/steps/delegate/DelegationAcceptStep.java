package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.springframework.http.HttpStatus;

public class DelegationAcceptStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IConsumerDelegationsApiClient consumerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final IdentityService identityService;
    private final PollingService pollingService;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public DelegationAcceptStep(ClientTokenConfigurator clientTokenConfigurator,
                                IDelegationApiClient delegationApiClient,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.producerDelegationsApiClient = clientTokenConfigurator.getProducerDelegationsApiClient();
        this.consumerDelegationsApiClient = clientTokenConfigurator.getConsumerDelegationsApiClient();
        this.delegationApiClient = clientTokenConfigurator.getDelegationApiClient();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @And("l'utente accetta la delega")
    public void userAcceptTheDelegation() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        approveProducerDelegation();
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) waitUntilDelegationIsApprove();
    }

    @And("l'ente {string} accetta la delega")
    public void producerDelegationIsAcceptedByTenant(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        approveProducerDelegation();
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) waitUntilDelegationIsApprove();
    }

    @And("l'ente {delegationRole} accetta la delega in fruizione")
    public void consumerDelegationIsAcceptedByTenant(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        approveConsumerDelegation();
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) waitUntilDelegationIsApprove();
    }

    private void approveProducerDelegation() {
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.approveProducerDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    private void approveConsumerDelegation() {
        httpCallExecutor.performCall(
                () -> consumerDelegationsApiClient.approveConsumerDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    public void waitUntilDelegationIsApprove() {
        // wait until delegation is correctly approved
        pollingService.makePolling(
                () -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())),
                res ->  res.getState().equals(DelegationState.ACTIVE),
                "There was an error while accepting the delegation!"
        );
    }
}
