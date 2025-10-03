package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ReversePurposeUpdateContent;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class PurposeUpdateDraftModeReceiveSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public PurposeUpdateDraftModeReceiveSteps(ClientTokenConfigurator clientTokenConfigurator,
                                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente aggiorna quella finalità per quell'e-service in erogazione inversa")
    public void userUpdateReversePurpose() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().updateReversePurpose(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        new ReversePurposeUpdateContent()
                                .title("some new title")
                                .description("some new description")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("some new free of charge reason")
                                .dailyCalls(49)
                )
        );
    }
}
