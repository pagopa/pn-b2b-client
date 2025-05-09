package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AgreementConsumersListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementConsumersListingSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede una operazione di listing dei fruitori dei propri e-service limitata ai primi {int}")
    public void listFirstConsumersOfOwnEServices(int limit) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .getAgreementConsumers(0, limit, null)
        );
    }

    @When("l'utente richiede una operazione di listing dei fruitori dei propri e-service con offset {int}")
    public void listConsumersOfOwnedEServicesWithOffset(int offset) {
        sharedStepsContext.getAgreementCommonContext().setResponseOffsetOne(
                clientTokenConfigurator.getAgreementClient()
                        .getAgreementConsumers(offset, 50, null)
        );
        sharedStepsContext.getAgreementCommonContext().setResponseOffsetTwo(
                clientTokenConfigurator.getAgreementClient()
                        .getAgreementConsumers(offset - 1, 50, null)
        );
    }

    @When("l'utente richiede una operazione di listing dei fruitori dei propri e-service filtrando per la keyword {string}")
    public void searchConsumersOfOwnedEServices(String keyword) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .getAgreementConsumers(0, 50, keyword)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} fruitor(i)(e)")
    public void verifyStatusCodeAndConsumerList(int statusCode, int consumerNumber) {
        ResponseEntity<CompactOrganizations> compactOrganizationsResponseEntity = (ResponseEntity<CompactOrganizations>) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(HttpStatus.valueOf(statusCode), compactOrganizationsResponseEntity.getStatusCode());
        Assertions.assertEquals(consumerNumber, compactOrganizationsResponseEntity.getBody().getResults().size());

    }
}
