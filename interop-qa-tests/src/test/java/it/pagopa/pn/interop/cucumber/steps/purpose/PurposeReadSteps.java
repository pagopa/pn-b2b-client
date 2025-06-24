package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

public class PurposeReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public PurposeReadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede la lettura della finalità")
    public void userReadPurpose() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getPurpose(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())
                )
        );
    }

    @Then("si ottiene status code 200 ma l'analisi del rischio solo per admin")
    public void verifyStatusCodeAndRiskAnalysis() {
        Assertions.assertEquals(200, httpCallExecutor.getResponseStatus().value());
        if (sharedStepsContext.getRole() != Role.ADMIN) {
            Assertions.assertNull(((Purpose)httpCallExecutor.getResponse()).getRiskAnalysisForm());
        }
    }

}
