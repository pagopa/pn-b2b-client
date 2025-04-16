package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Then;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CatalogCommonSteps {
    private final SharedStepsContext sharedStepsContext;

    public CatalogCommonSteps(SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
    }

    @Then("si ottiene status code {int} e la lista di {int} e-service(s)")
    public void verifyReceivedResponse(int statusCode, int eServiceNumber) {
        HttpCallExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(HttpStatus.valueOf(statusCode), httpCallExecutor.getClientResponse());
        Assertions.assertEquals(eServiceNumber,
                ((ResponseEntity<CompactEServicesLight>) httpCallExecutor.getResponse()).getBody().getResults().size());

    }
}
