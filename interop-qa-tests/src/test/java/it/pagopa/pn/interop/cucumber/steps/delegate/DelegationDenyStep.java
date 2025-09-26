package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class DelegationDenyStep {

    public static final String REJECTION_REASON = "Missing all required data!";
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IConsumerDelegationsApiClient consumerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;

    public DelegationDenyStep(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.producerDelegationsApiClient = clientTokenConfigurator.getProducerDelegationsApiClient();
        this.consumerDelegationsApiClient = clientTokenConfigurator.getConsumerDelegationsApiClient();
        this.delegationApiClient = clientTokenConfigurator.getDelegationApiClient();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @When("l'utente rifiuta la delega")
    public void whenUserRejectsDelegation() {
        String authToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(authToken);
        rejectProducerDelegation();
    }

    @And("l'ente {string} rifiuta la delega")
    public void delegationIsRejectedByTenant(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        rejectProducerDelegation();
    }

    @And("l'ente {string} rifiuta la delega con successo")
    public void delegationIsRejectedByTenantSuccessfully(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        rejectProducerDelegation();

        UUID delegationId = sharedStepsContext.getDelegationCommonContext().getDelegationId();
        delegationApiClient.waitForState(delegationId, DelegationState.REJECTED);
    }

    @And("l'ente {delegationRole} rifiuta la delega in fruizione")
    @And("l'ente {delegationRole} rifiuta la delega in fruizione con successo")
    public void delegationIsRejectedByTenant(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        rejectConsumerDelegation(tenantType);
    }

    private void rejectProducerDelegation() {
        OffsetDateTime now = OffsetDateTime.now();
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.rejectProducerDelegation(
                        sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                        new RejectDelegationPayload().rejectionReason(REJECTION_REASON)));
        sharedStepsContext.getDelegationCommonContext().setRejectedAt(now);
        sharedStepsContext.getDelegationCommonContext().setRejectionReason(REJECTION_REASON);
    }

    @And("l'ente fruitore {string} rifiuta la delega")
    public void rejectConsumerDelegation(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> consumerDelegationsApiClient.rejectConsumerDelegation(
                        sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                        new RejectDelegationPayload().rejectionReason(REJECTION_REASON)));
        if (httpCallExecutor.getResponseStatus() == HttpStatus.OK) waitForDelegationState(DelegationState.REJECTED);
    }

    @And("l'ente {string} con ruolo {string} revoca la delega")
    public void delegationIsRevokedByTenantWithRole(String tenantType, String role) {
        revokeDelegation(tenantType, role);
    }

    @And("l'ente {string} con ruolo {string} revoca la delega con successo")
    public void delegationIsRevokedSuccessfullyByTenantWithRole(String tenantType, String role) {
        revokeDelegation(tenantType, role);
        UUID delegationId = sharedStepsContext.getDelegationCommonContext().getDelegationId();
        delegationApiClient.waitForState(delegationId, DelegationState.REVOKED);
    }

    private void revokeDelegation(String tenantType, String role) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        OffsetDateTime now = OffsetDateTime.now();
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.revokeProducerDelegation(
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()));
        sharedStepsContext.getDelegationCommonContext().setRevokedAt(now);
    }

    @And("l'ente {string} con ruolo {string} revoca la delega in fruizione")
    public void consumerDelegationIsRevokedByTenantWithRole(String tenantType, String role) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        httpCallExecutor.performCall(
                () -> consumerDelegationsApiClient.revokeConsumerDelegation(
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()));
        if (httpCallExecutor.getResponseStatus() == HttpStatus.OK) waitForDelegationState(DelegationState.REVOKED);
    }

    @And("l'ente {delegationRole} con ruolo {string} revoca la delega in fruizione")
    public void consumerDelegationIsRevokedByTenantWithRole(DelegationRole delegationRole, String role) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        consumerDelegationIsRevokedByTenantWithRole(tenantType, role);
    }

    private void waitForDelegationState(DelegationState delegationState) {
        // wait until delegation is correctly rejected
        pollingService.makePolling(
                () -> delegationApiClient.getDelegation(
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()),
                res ->  res.getState().equals(delegationState),
                "There was an error while mutating delegation state to '%s'".formatted(delegationState)
        );
    }

}
