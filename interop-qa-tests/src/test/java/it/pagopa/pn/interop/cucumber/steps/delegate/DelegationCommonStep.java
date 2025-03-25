package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantFeature;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
public class DelegationCommonStep {
    private final PollingService pollingService;
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
    private final ITenantsApi tenantsApi;

    public DelegationCommonStep(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext,
                                PollingService pollingService) {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.tenantsApi = clientTokenConfigurator.getTenantsApi();
        this.pollingService = pollingService;
    }

    @Given("l'ente {string} rimuove la disponibilità a ricevere deleghe")
    public void tenantRemoveDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        try {
            tenantsApi.updateTenantDelegatedFeatures(false, false);
        } catch (HttpClientErrorException.Conflict e) {
            log.info("No delegation availability defined for the given tenant!");
        } catch (Exception e) {
            log.error("Error while removing delegation availability", e);
        }
    }

    @Given("l'ente {string} rimuove la disponibilità a ricevere deleghe in fruizione")
    public void tenantRemoveConsumerDelegationAvailability(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        String correlationId = sharedStepsContext.getXCorrelationId();
        UUID tenantId = this.identityService.getOrganizationId(tenantType);
        try {
            tenantsApi.updateTenantDelegatedFeatures(false, false);
            pollingService.makePolling(
                () -> tenantsApi.getTenant(correlationId, tenantId),
        result -> result.getFeatures().stream()
                    .map(TenantFeature::getDelegatedConsumer)
                    .allMatch(Objects::isNull),
                "An error occured when trying to remove consumer delegation for tenant %s".formatted(tenantType)
            );
        } catch (HttpClientErrorException.Conflict e) {
            log.info("No delegation availability defined for the given tenant!");
        } catch (Exception e) {
            e.printStackTrace();
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
