package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;

import java.util.List;
import java.util.UUID;

public class PurposeArchiveSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final PurposeCommonContext purposeCommonContext;

    public PurposeArchiveSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.purposeCommonContext = sharedStepsContext.getPurposeCommonContext();
    }

    @When("l'utente archivia quella finalità in stato {string}")
    public void userArchivePurpose(String state) {
        String versionId = List.of("WAITING_FOR_APPROVAL", "REJECTED").contains(state)
                ? purposeCommonContext.getWaitingForApprovalVersionId()
                : purposeCommonContext.getVersionId();

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().archivePurposeVersion(
                        UUID.fromString(purposeCommonContext.getPurposeId()),
                        UUID.fromString(versionId)
                )
        );
    }
}
