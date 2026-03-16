package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

public class TenantCertifiedAttributesListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public TenantCertifiedAttributesListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = this.sharedStepsContext.getIdentityService();
    }

    @When("l'utente richiede una operazione di listing degli attributi certificati posseduti da {string}")
    public void userRequiredListingOperationOwnBy(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getCertifiedAttributes(tenantId)
        );
    }

    @Then("si ottiene status code 200 e la lista degli attributi certificati contenente l'attributo assegnato e l'attributo IPA \"Comune\"")
    public void verifyStatusCodeeAndAttributeList() {
        CertifiedAttributesResponse certifiedAttributesResponse = (CertifiedAttributesResponse) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(200, sharedStepsContext.getHttpCallExecutor().getResponseStatus().value());
        Assertions.assertTrue(certifiedAttributesResponse.getAttributes().stream()
                .anyMatch(certifiedTenantAttribute -> certifiedTenantAttribute.getId().equals(sharedStepsContext.getAttributeCommonContext().getAttributeId())),
                "The desired attribute is not present in the certified attribute list!");
        Assertions.assertTrue(certifiedAttributesResponse.getAttributes().stream()
                .anyMatch(certifiedTenantAttribute -> certifiedTenantAttribute.getName().equals("Comuni e loro Consorzi e Associazioni")),
                "The attribute IPA \"Comune\" is not present in the certified attribute list!");
    }
}
