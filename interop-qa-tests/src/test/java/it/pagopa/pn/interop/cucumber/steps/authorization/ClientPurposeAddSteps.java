package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.service.utils.CommonUtils;

public class ClientPurposeAddSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;

    public ClientPurposeAddSteps(IAuthorizationClient authorizationClient, ClientCommonSteps clientCommonSteps) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
    }

    @When("l'utente richiede l'associazione della finalità al client")
    public void userRetrievesFinalization() {
        clientCommonSteps.performCall(() ->
                authorizationClient.addClientPurpose("", clientCommonSteps.getClients().get(0), clientCommonSteps.getPurposeId())
        );
    }
}
