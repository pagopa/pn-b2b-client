package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

public class TenantDeclaredAttributesListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public TenantDeclaredAttributesListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                SharedStepsContext sharedStepsContext,
                                                IdentityService identityService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = identityService;
    }

    @When("l'utente richiede una operazione di listing degli attributi dichiarati posseduti da {string}")
    public void userRequiredListingOperationOwnBy(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getDeclaredAttributes(tenantId)
        );
    }

    @Then("si ottiene status code 200 e la lista degli attributi contenente l'attributo dichiarato")
    public void verifyStatusCodeAndAttributeList() {
        DeclaredAttributesResponse declaredAttributesResponse = (DeclaredAttributesResponse) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(200, sharedStepsContext.getHttpCallExecutor().getResponseStatus().value());
        Assertions.assertTrue(declaredAttributesResponse.getAttributes().stream()
                .anyMatch(declaredTenantAttribute -> declaredTenantAttribute.getId().equals(sharedStepsContext.getAttributeCommonContext().getAttributeId())),
                "The desired declared attribute is not present in the declared attribute list!");
    }
}
