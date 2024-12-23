package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DelegationDenyStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IProducerDelegationsApiClient producerDelegationsApiClient;
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public DelegationDenyStep(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.producerDelegationsApiClient = clientTokenConfigurator.getProducerDelegationsApiClient();
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente rifiuta la delega")
    public void whenUserRejectsDelegation() {
        String authToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(authToken);
        rejectDelegation();
    }

    @And("l'ente {string} rifiuta la delega")
    public void delegationIsRejectedByTenant(String tenantType) {
        clientTokenConfigurator.setBearerToken(commonUtils.getToken(tenantType, null));
        rejectDelegation();
    }

    private void rejectDelegation() {
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.rejectDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                        new RejectDelegationPayload().rejectionReason("Missing all required data!")));
    }

    @And("l'ente {string} con ruolo {string} revoca la delega")
    public void delegationIsRevokedByTenantWithRole(String tenantType, String role) {
        clientTokenConfigurator.setBearerToken(commonUtils.getToken(tenantType, role));
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.revokeProducerDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())));
    }

}
