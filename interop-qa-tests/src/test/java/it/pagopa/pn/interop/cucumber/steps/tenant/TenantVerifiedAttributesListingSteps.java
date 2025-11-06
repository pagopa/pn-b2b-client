package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedAttributesResponse;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

public class TenantVerifiedAttributesListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;

    public TenantVerifiedAttributesListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente richiede una operazione di listing degli attributi verificati posseduti da {string}")
    public void listVerifiedAttributeOwnBy(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getVerifiedAttributes(tenantId)
        );
    }

    @Then("si ottiene status code 200 e la lista degli attributi contenente l'attributo verificato da {string}")
    public void verifyStatusCodeAndAttributeList(String tenantType) {
        Assertions.assertEquals(200, httpCallExecutor.getResponseStatus().value());

        UUID verifierId = identityService.getOrganizationId(tenantType);
        Assertions.assertTrue(
                ((VerifiedAttributesResponse) httpCallExecutor.getResponse()).getAttributes()
                        .stream()
                        .anyMatch(attr -> attr.getId().equals(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                && attr.getVerifiedBy().stream().anyMatch(tenantVerifier -> tenantVerifier.getId().equals(verifierId))),
                "The verified attribute searched is not present in the verified attribute list!"
        );
    }
}
