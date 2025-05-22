package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;

public class VoucherGenerationPurposeSteps {

    private final SharedStepsContext sharedStepsContext;
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;

    public VoucherGenerationPurposeSteps(
        SharedStepsContext sharedStepsContext,
        DataPreparationService dataPreparationService,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @Given("{string} ha già richiesto la cancellazione della richiesta di aggiornamento della stima di carico")
    public void tenantAlreadyRequestedCancellationOfTheUpdateRequest(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        dataPreparationService.deletePurposeVersion(
            sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
            sharedStepsContext.getPurposeCommonContext().getWaitingForApprovalVersionIdAsUUID());
    }

    @Given("{string} ha già approvato la richiesta di aggiornamento della stima di carico")
    public void tenantAlreadyApprovedTheUpdateRequest(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeId();
        String waitingForApprovalVersionId = sharedStepsContext.getPurposeCommonContext()
            .getWaitingForApprovalVersionId();

        dataPreparationService.activatePurposeVersion(
            UUID.fromString(purposeId),
            UUID.fromString(waitingForApprovalVersionId));

        sharedStepsContext.getPurposeCommonContext().getCurrentVersionIds().add(waitingForApprovalVersionId);
    }

    @Given("{string} ha già rifiutato la richiesta di aggiornamento della stima di carico")
    public void tenantAlreadyRefusedTheUpdateRequest(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeId();
        String waitingForApprovalVersionId = sharedStepsContext.getPurposeCommonContext()
            .getWaitingForApprovalVersionId();

        dataPreparationService.rejectPurposeVersion(
            UUID.fromString(purposeId),
            UUID.fromString(waitingForApprovalVersionId));
    }

    @Given("{string} ha già sospeso la finalità che risulta sospesa dal fruitore")
    public void tenantAlreadySuspendedThePurpose(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));

        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeId();
        String currentVersionId = sharedStepsContext.getPurposeCommonContext().getCurrentVersionId();

        dataPreparationService.suspendPurpose(
            UUID.fromString(purposeId),
            UUID.fromString(currentVersionId),
            ClientType.CONSUMER);
    }

    @Given("{string} ha già sospeso la finalità che risulta sospesa dall'erogatore")
    public void tenantAlreadySuspendedThePurposeByProvider(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));

        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeId();
        String currentVersionId = sharedStepsContext.getPurposeCommonContext().getCurrentVersionId();

        dataPreparationService.suspendPurpose(
            UUID.fromString(purposeId),
            UUID.fromString(currentVersionId),
            ClientType.PRODUCER);
    }

    @Given("{string} ha già riattivato la finalità sospesa dal fruitore")
    public void tenantAlreadyReactivatedTheSuspendedPurpose(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeId();
        String currentVersionId = sharedStepsContext.getPurposeCommonContext().getCurrentVersionId();

        dataPreparationService.activatePurposeVersion(
            UUID.fromString(purposeId),
            UUID.fromString(currentVersionId),
            ClientType.CONSUMER);
    }

    @Given("{string} ha già riattivato la finalità sospesa dall'erogatore")
    public void tenantAlreadyReactivatedTheSuspendedPurposeByProvider(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeId();
        String currentVersionId = sharedStepsContext.getPurposeCommonContext().getCurrentVersionId();

        dataPreparationService.activatePurposeVersion(
            UUID.fromString(purposeId),
            UUID.fromString(currentVersionId),
            ClientType.PRODUCER);
    }

    @Given("l'utente possiede un identificativo di una purpose che non esiste")
    public void userHasAnIdOfAPurposeThatDoesNotExist() {
        String purposeId = UUID.randomUUID().toString();
        sharedStepsContext.getPurposeCommonContext().setPurposeId(purposeId);
    }
}
