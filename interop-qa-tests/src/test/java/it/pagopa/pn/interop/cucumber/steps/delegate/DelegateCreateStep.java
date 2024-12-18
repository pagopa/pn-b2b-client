package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.delegate.service.IDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

@Slf4j
public class DelegateCreateStep {
    private final IDelegationsApiClient producerDelegationsApiClient;
    private final IDelegationApiClient delegationApiClient;
    private final ITenantsApi tenantsApi;
    private final CommonUtils commonUtils;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public DelegateCreateStep(IDelegationsApiClient producerDelegationsApiClient,
                              IDelegationApiClient delegationApiClient,
                              ITenantsApi tenantsApi,
                              CommonUtils commonUtils,
                              SharedStepsContext sharedStepsContext,
                              HttpCallExecutor httpCallExecutor) {
        this.producerDelegationsApiClient = producerDelegationsApiClient;
        this.delegationApiClient = delegationApiClient;
        this.tenantsApi = tenantsApi;
        this.commonUtils = commonUtils;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("l'ente {string} rimuove la disponibilità a ricevere deleghe")
    public void tenantRemoveDelegationAvailability(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        try {
            tenantsApi.deleteTenantDelegatedProducerFeature();
        } catch (HttpClientErrorException.Conflict e) {
            log.info("No delegation availability defined for the given tenant!");
        }
    }

    @And("l'ente {string} concede la disponibilità a ricevere deleghe")
    public void tenantGrantsDelegationAvailability(String tenantType) throws InterruptedException {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor.performCall(() -> tenantsApi.assignTenantDelegatedProducerFeature());
        Thread.sleep(4000);
    }

    @And("l'ente {string} richiede la creazione di una delega per l'ente {string}")
    public void createDelegate(String delegatorTenantType, String tenantType) throws InterruptedException {
        commonUtils.setBearerToken(commonUtils.getToken(delegatorTenantType, null));
        createDelegate(tenantType);
    }

    @And("l'utente richiede la creazione di una delega per l'ente {string}")
    public void userRequestDelegationCreation(String tenantType) {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        createDelegate(tenantType);
    }

    @And("l'ente {string} accetta la delega")
    public void delegationIsAcceptedByTenant(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.approveDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    @And("l'ente {string} rifiuta la delega")
    public void delegationIsRejectedByTenant(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.rejectDelegation(sharedStepsContext.getXCorrelationId(),
                        sharedStepsContext.getDelegationCommonContext().getDelegationId(),
                        new RejectDelegationPayload().rejectionReason("Missing all required data!")));
    }

    @And("l'ente {string} revoca la delega")
    public void delegationIsRevokedByTenant(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        httpCallExecutor.performCall(
                () -> producerDelegationsApiClient.revokeProducerDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId())));
    }

    private void createDelegate(String tenantType) {
        UUID organizationId = commonUtils.getOrganizationId(tenantType);
        httpCallExecutor.performCall(() -> producerDelegationsApiClient.createProducerDelegation(sharedStepsContext.getXCorrelationId(),
                new DelegationSeed().eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId()).delegateId(organizationId)));
        sharedStepsContext.getDelegationCommonContext().setDelegationId(((CreatedResource) httpCallExecutor.getResponse()).getId());

        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(),
                        String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId()))),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while creating the delegation!"
        );

    }


}
