package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
public class DelegationCommonStep {
    private final SharedStepsContext sharedStepsContext;
    private final IDelegationApiClient delegationApiClient;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;
    private final ITenantsApi tenantsApi;

    public DelegationCommonStep(SharedStepsContext sharedStepsContext,
                                IDelegationApiClient delegationApiClient,
                                ITenantsApi tenantsApi) {
        this.sharedStepsContext = sharedStepsContext;
        this.delegationApiClient = delegationApiClient;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.tenantsApi = tenantsApi;
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

    @Then("si ottiene lo status code {int}")
    public void thenStatusCodeIs(int statusCode) {
        int actualStatusCode = httpCallExecutor.getClientResponse().value();
        if (isSuccessful(statusCode)) Assertions.assertEquals(200, actualStatusCode);
        else Assertions.assertEquals(statusCode, actualStatusCode);
    }

    boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

}
