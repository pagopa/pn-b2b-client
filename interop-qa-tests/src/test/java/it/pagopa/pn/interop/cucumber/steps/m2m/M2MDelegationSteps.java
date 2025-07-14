package it.pagopa.pn.interop.cucumber.steps.m2m;

import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATE;
import static it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole.DELEGATING;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.delegate.service.IM2MDelegationClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegation;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationProxy;
import java.util.List;
import java.util.Objects;

public class M2MDelegationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final IM2MDelegationClient delegationClient;
    private final PollingService pollingService;

    private DelegationProxy delegationProxy;

    public M2MDelegationSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.delegationClient = clientTokenConfigurator.getM2mDelegationClient();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @When("l'ente delegante tenta di inoltrare una richiesta m2m di delega in fruizione all'ente delegato")
    public void consumerDelegate() {
        String delegatorTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATING);
        String delegateTenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(DELEGATE);

        String delegatingTenantToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(delegatingTenantToken);
        this.delegationProxy = DelegationProxy.ofMainDelegation(sharedStepsContext.getDelegationCommonContext());

        this.httpCallExecutor.performCall(() -> this.delegationClient.createConsumerDelegation(
            new DelegationSeed()
                .delegateId(identityService.getOrganizationId(delegateTenant))
                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
        ));

        if (httpCallExecutor.getClientResponse().is2xxSuccessful()) {
            ConsumerDelegation createdDelegation = (ConsumerDelegation) httpCallExecutor.getResponse();
            delegationProxy.setDelegationId(createdDelegation.getId());
            delegationProxy.setDelegatorId(identityService.getOrganizationId(delegatorTenant));
            delegationProxy.setDelegateId(identityService.getOrganizationId(delegateTenant));
        }
    }

    @Then("la delega è stata inoltrata correttamente")
    public void checkDelegation() {
        pollingService.makePolling(() -> this.delegationClient.getConsumerDelegations(
            List.of(this.delegationProxy.getDelegatorId()),
            List.of(this.delegationProxy.getDelegateId()),
            List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId())
        ),
            delegations -> Objects.nonNull(delegations) && delegations.getResults().stream().anyMatch(consumerDelegation -> consumerDelegation.getId().equals(delegationProxy.getDelegationId()))
        ,"La delega non è stata creata correttamente. Consultare i log per maggiori dettagli.");
    }

}