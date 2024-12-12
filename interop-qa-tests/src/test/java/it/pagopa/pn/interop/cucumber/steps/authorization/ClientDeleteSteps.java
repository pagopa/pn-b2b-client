package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientDeleteSteps {
    private final IAuthorizationClient authorizationClientCreate;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientDeleteSteps(IAuthorizationClient authorizationClientCreate,
                             ClientCommonSteps clientCommonSteps,
                             HttpCallExecutor httpCallExecutor,
                             CommonUtils commonUtils) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede una operazione di cancellazione di quel client")
    public void deleteClient() {
        commonUtils.setBearerToken(commonUtils.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClientCreate.deleteClient("", clientCommonSteps.getClients().get(0)));
    }
}
