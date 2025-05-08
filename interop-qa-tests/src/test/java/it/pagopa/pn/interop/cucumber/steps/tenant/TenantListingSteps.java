package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class TenantListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final HttpCallExecutor httpCallExecutor;

    public TenantListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede una operazione di listing degli aderenti limitata a {int}")
    public void requireOperationListWithLimit(int count) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getTenants(count, null, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli aderenti filtrando per la keyword {string}")
    public void requireConsumerOperationListWithKeyword(String keyword) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getTenants(50, keyword, null)
        );
    }
}
