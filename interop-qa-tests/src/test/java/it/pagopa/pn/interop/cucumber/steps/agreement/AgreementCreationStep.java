package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.UUID;

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

    @Given("{string} ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione")
    public void requestForServiceAlreadySubmittedAndPendingApproval(String tenantType) {
        agreementProcessRequest(tenantType, null);
    }

    @Given("il {delegationRole} ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione")
    public void delegationRequestForServiceAlreadySubmittedAndPendingApproval(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        delegationRequestForServiceAlreadySubmittedAndPendingApproval(tenantType);
    }

    @Given("{string} ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione")
    public void delegationRequestForServiceAlreadySubmittedAndPendingApproval(String tenantType) {
        UUID delegationId = sharedStepsContext.getDelegationCommonContext().getDelegationId();
        agreementProcessRequest(tenantType, delegationId);
    }

    @Given("il {delegationRole} ha già creato e inviato una richiesta di fruizione indicando una delega inesistente")
    public void delegationNotExistRequestForServiceAlreadySubmittedAndPendingApproval(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        UUID delegationId = UUID.randomUUID();
        agreementProcessRequest(tenantType, delegationId);
    }

    @Given("il {delegationRole} ha già creato e inviato una richiesta di fruizione indicando la delega dell'ente terzo")
    public void wrongDelegationRequestForServiceAlreadySubmittedAndPendingApproval(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        UUID delegationId = sharedStepsContext.getDelegationCommonContext().getAuxDelegationId();
        agreementProcessRequest(tenantType, delegationId);
    }

    private void agreementProcessRequest(String tenantType, UUID delegationId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID agreementId = dataPreparationService.createAgreement(
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
            delegationId);
        sharedStepsContext.setAgreementId(agreementId);

        dataPreparationService.submitAgreement(agreementId, AgreementState.PENDING);
    }
}
