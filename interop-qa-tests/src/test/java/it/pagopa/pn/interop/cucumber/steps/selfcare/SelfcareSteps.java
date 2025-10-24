package it.pagopa.pn.interop.cucumber.steps.selfcare;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.User;
import it.pagopa.interop.selfcare.service.ISelfcareClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public class SelfcareSteps {
    private final ISelfcareClient selfcareClient;
    private final IdentityService identityService;

    private ResponseEntity<List<User>> institutionsSelfcareResponse;

    public SelfcareSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.selfcareClient = clientTokenConfigurator.getISelfcareClient();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("viene invocata l'API di recupero utenze per l'istituzione: {string}")
    public void callInstitutionAPI(String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        institutionsSelfcareResponse = selfcareClient.getInstitutionUsers(tenantId, null, null, null);
    }

    @Then("si verifica che la chiamata a selfcare abbia ritornato uno status code: {int}")
    public void verifySelfcareResponse(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, institutionsSelfcareResponse.getStatusCode().value());

    }
}
