package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenants;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

public class TenantListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    public TenantListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede una operazione di listing degli aderenti limitata a {int}")
    public void requireOperationListWithLimit(int count) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getTenants(count, null, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli aderenti filtrando per la keyword {string}")
    public void requireConsumerOperationListWithKeyword(String keyword) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getTenants(50, keyword, null)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} tenant")
    public void verifyStatusCodeAndConsumerListSize(int statusCode, int tenantNum) {
        Tenants compactOrganizations = (Tenants) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(statusCode, sharedStepsContext.getHttpCallExecutor().getResponseStatus().value());
        Assertions.assertEquals(tenantNum, compactOrganizations.getResults().size());
    }
}
