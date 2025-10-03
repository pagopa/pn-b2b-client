package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

public class AgreementProducersListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public AgreementProducersListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                          SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede una operazione di listing degli erogatori degli e-service per cui ha una richiesta di fruizione limitata ai primi {int}")
    public void requireProducerListingOperationWithLimit(int limit) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementProducers(0, limit, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli erogatori degli e-service per cui ha una richiesta di fruizione con offset {int}")
    public void requireProducerListingOperationWithOffset(int offset) {
        sharedStepsContext.getAgreementCommonContext().setResponseOffsetOne(
                clientTokenConfigurator.getAgreementClient().getAgreementProducers(offset, 50, null)
        );

        sharedStepsContext.getAgreementCommonContext().setResponseOffsetTwo(
                clientTokenConfigurator.getAgreementClient().getAgreementProducers(offset - 1, 50, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli erogatori degli e-service per cui ha una richiesta di fruizione filtrando per la keyword {string}")
    public void requireProducerListingOperationWithKeyword(String keyword) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementProducers(0, 50, keyword)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} erogator(i)(e)")
    public void verifyStatusAndProducersList(int statusCode, int producersNumber) {
        Assertions.assertEquals(statusCode, sharedStepsContext.getHttpCallExecutor().getResponseStatus().value());
        Assertions.assertEquals(producersNumber,
                ((ResponseEntity<CompactOrganizations>) sharedStepsContext.getHttpCallExecutor().getResponse()).getBody().getResults().size());
    }

    @Then("si ottiene status code 200 con la corretta verifica dell'offset")
    public void verifyStatusCodeAndOffset() {
        Assertions.assertEquals(200, sharedStepsContext.getAgreementCommonContext().getResponseOffsetOne().getStatusCodeValue());
        Assertions.assertEquals(200, sharedStepsContext.getAgreementCommonContext().getResponseOffsetTwo().getStatusCodeValue());

        // Two responses (listing operations), where: the first has an offset of 0, and the second has an offset of -1
        // The second element of the second list is equal to the first element of the first list.
        Assertions.assertEquals(sharedStepsContext.getAgreementCommonContext().getResponseOffsetOne().getBody().getResults().get(0).getId(),
                sharedStepsContext.getAgreementCommonContext().getResponseOffsetTwo().getBody().getResults().get(1).getId());
    }
}
