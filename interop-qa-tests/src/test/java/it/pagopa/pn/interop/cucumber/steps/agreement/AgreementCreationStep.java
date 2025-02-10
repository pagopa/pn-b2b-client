package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgreementCreationStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final DataPreparationService dataPreparationService;

    public AgreementCreationStep(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha già rifiutato quella richiesta di fruizione")
    public void tenantHasDeclinedThatRequest(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.rejectAgreement(sharedStepsContext.getAgreementId());
    }

    @Given("il {delegationRole} ha già rifiutato quella richiesta di fruizione")
    public void tenantHasDeclinedThatRequest(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        tenantHasDeclinedThatRequest(tenantType);
    }

    @Given("l'utente crea una richiesta di fruizione")
    public void userCreatesRequestForService() {
        agreementCreationRequest(null);
    }

    private void agreementCreationRequest(UUID delegationId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        Optional<UUID> agreementId = dataPreparationService.createAgreement(
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
            delegationId);
        sharedStepsContext.setAgreementId(agreementId.orElse(null));
    }

    @Given("{string} ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione")
    public void requestForServiceAlreadySubmittedAndPendingApproval(String tenantType) {
        agreementProcessRequest(identityService.getToken(tenantType, null), null);
    }

    @Given("il {delegationRole} ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione")
    public void delegationRequestForServiceAlreadySubmittedAndPendingApproval(DelegationRole delegationRole) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        delegationRequestForServiceAlreadySubmittedAndPendingApproval(token);
    }

    @Given("{string} ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione")
    public void delegationRequestForServiceAlreadySubmittedAndPendingApproval(String token) {
        UUID delegationId = sharedStepsContext.getDelegationCommonContext().getDelegationId();
        agreementProcessRequest(token, delegationId);
    }

    @Given("l'utente ha già creato una richiesta di fruizione indicando una delega inesistente")
    public void delegationNotExistRequestForServiceAlreadySubmittedAndPendingApproval() {
        UUID delegationId = UUID.randomUUID();
        agreementCreationRequest(delegationId);
    }

    @Given("l'utente ha già creato una richiesta di fruizione indicando la delega dell'ente terzo")
    public void wrongDelegationRequestForServiceAlreadySubmittedAndPendingApproval() {
        log.info("Actual delegation context: {}", sharedStepsContext.getDelegationCommonContext());
        UUID delegationId = Objects.requireNonNull(
            sharedStepsContext.getDelegationCommonContext().getAuxDelegationId(),
            "Auxiliary delegation not found");
        agreementCreationRequest(delegationId);
    }

    private void agreementProcessRequest(String token, UUID delegationId) {
        clientTokenConfigurator.setBearerToken(token);
        UUID agreementId = dataPreparationService.createAndCheckAgreement(
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
            delegationId);
        sharedStepsContext.setAgreementId(agreementId);

        dataPreparationService.submitAgreement(agreementId, AgreementState.PENDING);
    }
}
