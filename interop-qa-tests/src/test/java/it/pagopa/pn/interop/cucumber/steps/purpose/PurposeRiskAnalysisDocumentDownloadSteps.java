package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionDocument;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import it.pagopa.interop.purpose.domain.CreatedEserviceVersion;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PurposeRiskAnalysisDocumentDownloadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final CommonUtils commonUtils;
    private final DataPreparationService dataPreparationService;

    private List<PurposeVersion> purposeVersions;

    public PurposeRiskAnalysisDocumentDownloadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                    SharedStepsContext sharedStepsContext,
                                                    CommonUtils commonUtils,
                                                    DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
        this.commonUtils = commonUtils;
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente scarica il documento di analisi del rischio")
    public void userDownloadRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        Purpose getPurposeResponse = clientTokenConfigurator.getPurposeApiClient().getPurpose(
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())
        );
        commonUtils.assertValidResponse();
        purposeVersions = getPurposeResponse.getVersions();

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getRiskAnalysisDocument(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getVersionId()),
                        Optional.ofNullable(getPurposeResponse.getCurrentVersion())
                                .map(PurposeVersion::getRiskAnalysisDocument)
                                .map(PurposeVersionDocument::getId)
                                .orElse(null)
                )
        );
    }

    @Given("l'utente ha già aggiornato finalità rispettando le stime di carico per quell'e-service")
    public void userUpdatePurposeWithCorrectThreshold() {
        CreatedEserviceVersion createdEserviceVersion = dataPreparationService.createNewPurposeVersion(
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                new PurposeVersionSeed().dailyCalls(49)
        );
        sharedStepsContext.getPurposeCommonContext().setVersionId(createdEserviceVersion.getCurrentVersionId().toString());
    }

    @Then("si ottiene status code {int} e un documento diverso")
    public void verifiyStatusCodeAndDocument(int statusCode) {
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());

        int length = purposeVersions.size();
        Assertions.assertNotEquals(purposeVersions.get(length - 1).getRiskAnalysisDocument().getId(),
                purposeVersions.get(length - 2).getRiskAnalysisDocument().getId());
    }

}
