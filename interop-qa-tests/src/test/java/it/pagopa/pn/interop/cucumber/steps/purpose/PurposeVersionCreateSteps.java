package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PurposeVersionCreateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;

    private int newDailyCalls;

    public PurposeVersionCreateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                     SharedStepsContext sharedStepsContext,
                                     BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente aggiorna la stima di carico per quella finalità restando entro la soglia")
    public void userUpdateCallsEstimateBelowThreshold() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        newDailyCalls = 50;
        sharedStepsContext.getRiskAnalysisCommonContext().setDailyCalls(newDailyCalls);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurposeVersion(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        new PurposeVersionSeed().dailyCalls(newDailyCalls)
                )
        );
    }

    @When("l'utente aggiorna la stima di carico per quella finalità a {int}")
    public void userUpdateCallsEstimateBelowThreshold(int newDailyCalls) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        this.newDailyCalls = newDailyCalls;
        sharedStepsContext.getRiskAnalysisCommonContext().setDailyCalls(newDailyCalls);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurposeVersion(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        new PurposeVersionSeed().dailyCalls(newDailyCalls)
                )
        );
    }

    @When("l'utente aggiorna la stima di carico per quella finalità superando la soglia")
    public void userUpdateCallsEstimateAboveThreshold() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        newDailyCalls = 51;
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().createPurposeVersion(
                        UUID.fromString(sharedStepsContext.getPurposeId()),
                        new PurposeVersionSeed().dailyCalls(newDailyCalls)
                )
        );
    }

    @Given("l'utente ha già creato una versione nuova della finalità in stato WAITING_FOR_APPROVAL")
    public void userCreatePurposeWithState() {
        dataPreparationService.createNewPurposeVersion(
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                new PurposeVersionSeed().dailyCalls(51)
        );
    }

    @Then("si ottiene status code 200 e la nuova versione della finalità è stata creata in stato {string} con la nuova stima di carico")
    public void verifyStatusCodeAndNewPurposeVersion(String desiredState) {
        PurposeVersionResource purposeVersionResource = (PurposeVersionResource) httpCallExecutor.getResponse();
        AtomicReference<PurposeVersion> version = new AtomicReference<>();
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getPurposeApiClient().getPurpose(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())
                ),
                res -> {
                    PurposeVersion purposeVersion = res.getVersions().stream()
                            .filter(v -> v.getId().equals(purposeVersionResource.getVersionId())).findAny().orElse(null);
                    version.set(purposeVersion);
                    return purposeVersion != null;
                },
                "The desired purpose version was not found!"
        );
        Assertions.assertEquals(200, httpCallExecutor.getResponseStatus().value());
        Assertions.assertEquals(newDailyCalls, version.get().getDailyCalls());
        Assertions.assertEquals(desiredState, version.get().getState().getValue());
    }
}
