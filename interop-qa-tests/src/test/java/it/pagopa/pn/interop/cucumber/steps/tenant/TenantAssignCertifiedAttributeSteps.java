package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class TenantAssignCertifiedAttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public TenantAssignCertifiedAttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                               SharedStepsContext sharedStepsContext,
                                               IdentityService identityService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = identityService;
    }

    @When("l'utente assegna a {string} l'attributo certificato precedentemente creato")
    public void assignCertifiedAttribute(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().addCertifiedAttribute(
                        tenantId,
                        new CertifiedTenantAttributeSeed().id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                )
        );
    }
}
