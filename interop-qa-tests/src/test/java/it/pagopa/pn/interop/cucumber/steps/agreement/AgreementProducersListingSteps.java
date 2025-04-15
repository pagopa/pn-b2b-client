package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Then;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

public class AgreementProducersListingSteps {
    private final SharedStepsContext sharedStepsContext;

    public AgreementProducersListingSteps(SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
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
