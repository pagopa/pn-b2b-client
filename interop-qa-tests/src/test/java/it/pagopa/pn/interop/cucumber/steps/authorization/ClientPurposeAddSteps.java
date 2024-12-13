package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.pn.interop.cucumber.steps.purpose.domain.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

import java.util.UUID;

public class ClientPurposeAddSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;
    private final PurposeCommonContext purposeCommonContext;
    private final CommonUtils commonUtils;

    public ClientPurposeAddSteps(IAuthorizationClient authorizationClient,
                                 ClientCommonSteps clientCommonSteps,
                                 DataPreparationService dataPreparationService,
                                 HttpCallExecutor httpCallExecutor,
                                 PurposeCommonContext purposeCommonContext,
                                 CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
        this.purposeCommonContext = purposeCommonContext;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede l'associazione della finalità al client")
    public void userRetrievesFinalization() {
        commonUtils.setBearerToken(commonUtils.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.addClientPurpose("", clientCommonSteps.getClients().get(0),
                        new PurposeAdditionDetailsSeed().purposeId(UUID.fromString(purposeCommonContext.getPurposeId())))
        );
    }
}
