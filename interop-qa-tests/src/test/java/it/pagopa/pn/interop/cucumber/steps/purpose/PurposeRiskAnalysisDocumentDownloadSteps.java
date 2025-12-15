package it.pagopa.pn.interop.cucumber.steps.purpose;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionDocument;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import it.pagopa.interop.purpose.domain.CreatedEserviceVersion;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;

public class PurposeRiskAnalysisDocumentDownloadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final CommonUtils commonUtils;
    private final BFFDataPreparationService dataPreparationService;
    private final PollingService pollingService;

    private List<PurposeVersion> purposeVersions;

    public PurposeRiskAnalysisDocumentDownloadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                    SharedStepsContext sharedStepsContext,
                                                    CommonUtils commonUtils,
                                                    BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
        this.commonUtils = commonUtils;
        this.dataPreparationService = dataPreparationService;
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @When("l'utente scarica il documento di analisi del rischio")
    public void userDownloadRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        Purpose getPurposeResponse = clientTokenConfigurator.getPurposeApiClient().getPurpose(
            UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()));
        try {
            getPurposeResponse = pollingService.makePolling(() ->
                clientTokenConfigurator.getPurposeApiClient().getPurpose(
                    UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())),
                a -> nonNull(requireNonNull(a.getCurrentVersion()).getRiskAnalysisDocument()),
                "Non è stata rilevata alcuna risk analysis");
        } catch (PollingPredicateException e) {
            /* 12/12/2025 nel caso specifico si prevede che lo step possa NON riscontrare una risk
                analysis (motivo per cui si sopprime l'eccezione), tuttavia viene eseguito il
                polling per avere ragionevole certezza che la piattaforma abbia avuto il tempo
                - qualora presente - di generarla. */
        }

        commonUtils.assertValidResponse();
        purposeVersions = getPurposeResponse.getVersions();

        Purpose finalGetPurposeResponse = getPurposeResponse;
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getRiskAnalysisDocument(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getVersionId()),
                        Optional.ofNullable(finalGetPurposeResponse.getCurrentVersion())
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
        Assertions.assertEquals(statusCode, httpCallExecutor.getResponseStatus().value());

        int length = purposeVersions.size();
        Assertions.assertNotEquals(purposeVersions.get(length - 1).getRiskAnalysisDocument().getId(),
                purposeVersions.get(length - 2).getRiskAnalysisDocument().getId());
    }

}
