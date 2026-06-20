package it.pagopa.pn.interop.cucumber.steps.selfcare;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.User;
import it.pagopa.interop.selfcare.service.ISelfcareClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SelfcareSteps {
    private final ISelfcareClient selfcareClient;
    private final IdentityService identityService;
    private final TenantContext tenantContext;
    private HttpStatus httpStatus;

    public SelfcareSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext, TenantContext tenantContext) {
        this.selfcareClient = clientTokenConfigurator.getISelfcareClient();
        this.identityService = sharedStepsContext.getIdentityService();
        this.tenantContext = tenantContext;
    }

    @When("viene invocata l'API di recupero utenze per l'istituzione: {string}")
    public void callInstitutionAPI(String tenantType) {
        callInstitutionAPIWithRoles(tenantType, null);
    }

    @When("viene invocata l'API di recupero utenze per l'istituzione: {string} filtrando per ruolo: {string}")
    public void callInstitutionAPIFilteredByRole(String tenantType, String role) {
        callInstitutionAPIWithRoles(tenantType, List.of(role));
    }

    private void callInstitutionAPIWithRoles(String tenantType, List<String> roles) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        try {
            ResponseEntity<List<User>> institutionsSelfcareResponse = selfcareClient.getInstitutionUsers(tenantId, null, roles, null);
            httpStatus = institutionsSelfcareResponse.getStatusCode();
            tenantContext.setSelfcareUsers(institutionsSelfcareResponse.getBody());
        } catch (HttpStatusCodeException e) {
            httpStatus = e.getStatusCode();
        }
    }

    @Then("si verifica che la chiamata a selfcare abbia ritornato uno status code: {int}")
    public void verifySelfcareResponse(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, httpStatus.value());
    }

    @Then("si verifica che la risposta contenga esattamente {int} utente con ruolo {string} dell'istituzione: {string}")
    public void verifySelfcareResponseContainsExactUsersByRole(int expectedUsers, String role, String tenantType) {
        List<User> users = tenantContext.getSelfcareUsers();
        Assertions.assertNotNull(users);
        Assertions.assertEquals(expectedUsers, users.size());

        List<UUID> expectedUserIds = identityService.getUserIds(tenantType, role);
        Assertions.assertFalse(expectedUserIds.isEmpty());

        assertThat(users)
                .extracting(User::getUserId)
                .isSubsetOf(expectedUserIds.toArray(new UUID[0]));
    }


}
