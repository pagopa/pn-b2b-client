package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class PurposeReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

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

    @When("l'utente richiede la lettura della finalità numero {collectionIndex}")
    public void userReadPurpose(int purposeIndex) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        List<UUID> purposesIds = sharedStepsContext.getPurposeCommonContext()
            .getPurposesIdsAsUUID();
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getPurposeApiClient().getPurpose(purposesIds.get(purposeIndex))
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
