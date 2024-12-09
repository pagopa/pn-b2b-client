package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientPurposeAddSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;

    public ClientPurposeAddSteps(IAuthorizationClient authorizationClient,
                                 ClientCommonSteps clientCommonSteps,
                                 DataPreparationService dataPreparationService,
                                 HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede l'associazione della finalità al client")
    public void userRetrievesFinalization() {
        httpCallExecutor.performCall(() ->
                authorizationClient.addClientPurpose("", clientCommonSteps.getClients().get(0), clientCommonSteps.getPurposeId())
        );
    }
}
