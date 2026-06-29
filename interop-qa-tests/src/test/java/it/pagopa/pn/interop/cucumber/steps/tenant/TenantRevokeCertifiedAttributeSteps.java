package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class TenantRevokeCertifiedAttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;

    public TenantRevokeCertifiedAttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente revoca l'attributo precedentemente creato e assegnato")
    public void revokeAttributePreviouslyCreated() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeCertifiedAttribute(
                        identityService.getOrganizationId(sharedStepsContext.getTenantType()),
                        sharedStepsContext.getAttributeCommonContext().getAttributeId())
        );
    }

    @When("l'utente revoca l'attributo certificato {int}-esimo nel gruppo {int}-esimo precedentemente creato e assegnato a {string}")
    public void revokeCertifiedAttribute(int attributeIndex, int groupIndex, String consumerTenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeCertifiedAttribute(
                        identityService.getOrganizationId(consumerTenant),
                        sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(groupIndex).get(attributeIndex))
        );
    }
}
