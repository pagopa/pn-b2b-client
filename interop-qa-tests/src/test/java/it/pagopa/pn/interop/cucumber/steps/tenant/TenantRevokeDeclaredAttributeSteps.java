package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class TenantRevokeDeclaredAttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public TenantRevokeDeclaredAttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente revoca l'attributo precedentemente dichiarato")
    public void revokeAttributePreviouslyCreated() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeDeclaredAttribute(
                        sharedStepsContext.getAttributeCommonContext().getAttributeId())
        );
    }
}
