package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import java.util.List;
import java.util.UUID;

import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

@Slf4j
public class PurposeActivationStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IPurposeApiClient purposeApiClient;
    private final SharedStepsContext sharedStepsContext;
    private final PollingService pollingService;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;
    private final EServicesCommonContext eServicesCommonContext;

    public PurposeActivationStep(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
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

    @When("l'utente tenta di attivare la finalità")
    public void userTriesToActivatePurpose() {
        DelegationRef delegationRef = new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId());
        httpCallExecutor.performCall(
                () -> purposeApiClient.activatePurposeVersion(
                    UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                    UUID.fromString(sharedStepsContext.getPurposeCommonContext().getVersionId()),
                    delegationRef)
        );
    }

    @When("l'utente (ri)attiva la finalità in stato {string} per quell'e-service")
    public void userActivatesPurposeInStateForThatEService(String state) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        DelegationRef delegationRef = new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId());
        activatePurposeInStateForThatEServiceWithDelegate(state, delegationRef);
    }

    @When("l'utente {string} di {string} (ri)attiva la finalità in stato {string} per quell'e-service")
    public void userActivatesPurposeInStateForThatEService(String role, String tenant, String state) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        DelegationRef delegationRef = new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId());
        activatePurposeInStateForThatEServiceWithDelegate(state, delegationRef);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'utente {string} di {string} (ri)attiva la finalità in stato {string} per quell'e-service con successo")
    public void successfullyUserActivatesPurposeInStateForThatEService(String role, String tenant, String state) {
        userActivatesPurposeInStateForThatEService(role, tenant, state);
        if(httpCallExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException("L'attivazione della finalità non è avvenuta con successo");
        }
    }

    @When("l'utente {delegationRole} (ri)attiva la finalità in stato {string} per quell'e-service")
    public void userActivatesPurposeInStateForThatEServiceWithDelegate(DelegationRole delegationRole, String state) throws InterruptedException {
        Thread.sleep(2000);
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, null));
        activatePurposeInStateForThatEServiceWithDelegate(state, new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    private void activatePurposeInStateForThatEServiceWithDelegate(String state, DelegationRef delegationRef) {
        String versionId = switch (state) {
            case "WAITING_FOR_APPROVAL", "REJECTED" -> sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionId();
            default -> sharedStepsContext.getPurposeCommonContext().getVersionId();
        };

        if (versionId == null) throw new IllegalArgumentException("No versionId found!");
        httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(
                UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()), UUID.fromString(versionId), delegationRef));
//        pollingService.makePolling(() -> purposeApiClient.getPurpose(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())),
//                res -> Optional.ofNullable(res.getCurrentVersion()).map(PurposeVersion::getState).filter(status -> status == PurposeVersionState.ACTIVE).isPresent(),
//                "There was an error while activating the purpose!");
    }

    @When("l'utente (ri)attiva la prima finalità in stato {string} per quell'e-service")
    public void userActivatesFirstPurposeInStateForThatEService(String state) {
        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposesIds().get(0);
        String waitingForApprovalVersionId = sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionIds().get(0);
        String currentVersionId = sharedStepsContext.getPurposeCommonContext().getCurrentVersionIds().get(0);

        String versionId = switch (state) {
            case "WAITING_FOR_APPROVAL", "REJECTED" -> waitingForApprovalVersionId;
            default -> currentVersionId;
        };

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().activatePurposeVersion(UUID.fromString(purposeId), UUID.fromString(versionId))
        );
    }

    @Then("si ottiene status code {int} e la finalità in stato {string}")
    public void verifyStatusCodeAndPurposeState(int statusCode, String desiredState) {
        Assertions.assertEquals(statusCode, httpCallExecutor.getResponseStatus().value());
        PurposeVersionState purposeVersionState = PurposeVersionState.fromValue(desiredState);
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getPurposeApiClient().getPurpose(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()))
                ),
                res -> {
                    Purpose purpose = (Purpose) httpCallExecutor.getResponse();
                    if (purpose == null) return false;
                    return switch (desiredState) {
                        case "WAITING_FOR_APPROVAL" -> purpose.getWaitingForApprovalVersion() != null &&
                                purpose.getWaitingForApprovalVersion().getState() == purposeVersionState;
                        case "REJECTED" -> purpose.getRejectedVersion() != null &&
                                purpose.getRejectedVersion().getState() == purposeVersionState;
                        default -> purpose.getCurrentVersion() != null &&
                                purpose.getCurrentVersion().getState() == purposeVersionState;
                    };
                },
                "Purpose with desired state not found!"
        );
    }
}
