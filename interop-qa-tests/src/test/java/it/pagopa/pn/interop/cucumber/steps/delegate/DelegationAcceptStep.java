package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.DelegationCommonContext;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;

public class DelegationAcceptStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IConsumerDelegationsApiClient consumerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final IdentityService identityService;
    private final PollingService pollingService;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

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
        approveProducerDelegation(
            httpCallExecutor,
            producerDelegationsApiClient,
            delegationApiClient,
            sharedStepsContext.getDelegationCommonContext(),
            pollingService
        );
    }

    @And("l'ente {string} accetta la delega")
    @And("l'ente {string} accetta la delega con successo")
    public void producerDelegationIsAcceptedByTenant(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        approveProducerDelegation(
            httpCallExecutor,
            producerDelegationsApiClient,
            delegationApiClient,
            sharedStepsContext.getDelegationCommonContext(),
            pollingService
        );
    }

    public static void approveProducerDelegation(
        IHttpExecutor httpExecutor,
        IProducerDelegationsApiClient producerClient,
        IDelegationApiClient delegationClient,
        DelegationCommonContext context,
        PollingService pollingService
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        httpExecutor.performCall(
            () -> producerClient.approveProducerDelegation(
                context.getDelegationId()));
        if (httpExecutor.getResponseStatus() == HttpStatus.OK) {
            context.setActivatedAt(now);
            waitUntilDelegationIsApproved(
                delegationClient,
                pollingService,
                context
            );
        }
    }

    @And("l'ente {delegationRole} accetta la delega in fruizione")
    public void consumerDelegationIsAcceptedByTenant(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        approveConsumerDelegation();
        if (httpCallExecutor.getResponseStatus() == HttpStatus.OK) {
            waitUntilDelegationIsApproved(
                delegationApiClient,
                pollingService,
                sharedStepsContext.getDelegationCommonContext()
            );
        }
    }

    private void approveConsumerDelegation() {
        httpCallExecutor.performCall(
                () -> consumerDelegationsApiClient.approveConsumerDelegation(
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    public static void waitUntilDelegationIsApproved(
        IDelegationApiClient delegationClient,
        PollingService pollingService,
        DelegationCommonContext context
    ) {
        // wait until delegation is correctly approved
        pollingService.makePolling(
                () -> delegationClient.getDelegation(
                    context.getDelegationId()),
                res ->  res.getState().equals(DelegationState.ACTIVE),
                "There was an error while accepting the delegation!"
        );
    }
}
