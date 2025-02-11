package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

@Slf4j
public class DelegationDenyStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final IConsumerDelegationsApiClient consumerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
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

    @And("l'ente {delegationRole} rifiuta la delega in fruizione")
    public void delegationIsRejectedByTenant(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        rejectConsumerDelegation(tenantType);
    }

    private void rejectProducerDelegation() {
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.rejectProducerDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                        new RejectDelegationPayload().rejectionReason("Missing all required data!")));
    }

    @And("l'ente fruitore {string} rifiuta la delega")
    public void rejectConsumerDelegation(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> consumerDelegationsApiClient.rejectConsumerDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                        new RejectDelegationPayload().rejectionReason("Missing all required data!")));
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) waitForDelegationState(DelegationState.REJECTED);
    }

    @And("l'ente {string} con ruolo {string} revoca la delega")
    public void delegationIsRevokedByTenantWithRole(String tenantType, String role) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.revokeProducerDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())));
    }

    @And("l'ente {string} con ruolo {string} revoca la delega in fruizione")
    public void consumerDelegationIsRevokedByTenantWithRole(String tenantType, String role) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        httpCallExecutor.performCall(
                () -> consumerDelegationsApiClient.revokeConsumerDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())));
        if (httpCallExecutor.getClientResponse() == HttpStatus.OK) waitForDelegationState(DelegationState.REVOKED);
    }

    @And("l'ente {delegationRole} con ruolo {string} revoca la delega in fruizione")
    public void consumerDelegationIsRevokedByTenantWithRole(DelegationRole delegationRole, String role) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        consumerDelegationIsRevokedByTenantWithRole(tenantType, role);
    }

    // FIXME 21/01/2025 revisionare i seguenti metodi ed eventualmente rimuovere

    @Given("l'ente qualificato come PA su Interoperabilità di tipo amministratore")
    public void tenantIsQualifiedAsPA() {
        // Logic to qualify the tenant as PA with administrator privileges
        clientTokenConfigurator.setBearerToken(identityService.getToken("PA_ADMIN", null));
    }
    
    @When("Richiamare l’API di disponibilità in fruizione di un e-service")
    public void callAvailabilityAPI() {
        // Logic to call the API for availability
       // httpCallExecutor.performCall(() -> producerDelegationsApiClient.checkAvailability());
    }
    
    @Then("La disponibilità viene correttamente data e in risposta si ottiene codice 200")
    public void verifyAvailabilityResponse() {
        // Verify that the response status code is 200
        Assertions.assertEquals(200, httpCallExecutor.getClientResponse().value());
    }

    private void waitForDelegationState(DelegationState delegationState) {
        // wait until delegation is correctly rejected
        pollingService.makePolling(
                () -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())),
                res ->  res.getState().equals(delegationState),
                "There was an error while revoking the delegation!"
        );

    }

}
