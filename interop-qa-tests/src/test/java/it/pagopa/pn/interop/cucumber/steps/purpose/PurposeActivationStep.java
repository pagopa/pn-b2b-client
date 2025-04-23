package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class PurposeActivationStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IPurposeApiClient purposeApiClient;
    private final SharedStepsContext sharedStepsContext;
    private final PollingService pollingService;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final EServicesCommonContext eServicesCommonContext;

    public PurposeActivationStep(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        this.sharedStepsContext = sharedStepsContext;
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
    }

    @Given("{string} ha già creato e pubblicato un e-service con una soglia di carico tale da gestire una sola chiamata")
    public void tenantHasAlreadyCreatedAndPublishedEService(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        String eserviceName = String.format("e-service-%d", sharedStepsContext.getTestSeed());

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().name(eserviceName),
                new UpdateEServiceDescriptorSeed().dailyCallsPerConsumer(1).dailyCallsTotal(1)
        );
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());

        dataPreparationService.addInterfaceToDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId());
        dataPreparationService.publishDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId());
    }

    @When("l'utente (ri)attiva la finalità in stato {string} per quell'e-service")
    public void userActivatesPurposeInStateForThatEService(String state) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String versionId = "WAITING_FOR_APPROVAL".equals(state) || "REJECTED".equals(state)
                        ? sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId()
                        : sharedStepsContext.getPurposeCommonContext().getVersionId();
        if (versionId == null) throw new IllegalArgumentException("No versionId found!");
        httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()), UUID.fromString(versionId)));
        pollingService.makePolling(() -> purposeApiClient.getPurpose(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())),
                res -> Optional.ofNullable(res.getCurrentVersion()).map(PurposeVersion::getState).filter(status -> status == PurposeVersionState.ACTIVE).isPresent(),
                "There was an error while activating the purpose!");
    }

    @When("l'utente (ri)attiva la prima finalità in stato {string} per quell'e-service")
    public void userActivatesFirstPurposeInStateForThatEService(String state) {
        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposesIds().get(0);
        String waitingForApprovalVersionId = sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionIds().get(0);
        String currentVersionId = sharedStepsContext.getPurposeCommonContext().getCurrentVersionIds().get(0);
        String versionId = List.of("WAITING_FOR_APPROVAL", "REJECTED").contains(state) ? waitingForApprovalVersionId : currentVersionId;

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().activatePurposeVersion(UUID.fromString(purposeId), UUID.fromString(versionId))
        );
    }

    @Then("si ottiene status code {int} e la finalità in stato {string}")
    public void verifyStatusCodeAndPurposeState(int statusCode, String desiredState) {
        PurposeVersionState purposeVersionState = PurposeVersionState.fromValue(desiredState);
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getPurposeApiClient().getPurpose(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()))
                ),
                res -> {
                    Purpose purpose = (Purpose) httpCallExecutor.getResponse();
                    return "WAITING_FOR_APPROVAL".equals(desiredState)
                            ? purpose.getWaitingForApprovalVersion().getState() == purposeVersionState
                            : purposeVersionState == PurposeVersionState.REJECTED
                                ? purpose.getRejectedVersion().getState() == purposeVersionState
                                : purpose.getCurrentVersion().getState() == purposeVersionState;
                },
                "Purpose with desired state not found!"
        );
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
    }
}
