package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgreementCreationStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;

    public AgreementCreationStep(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha già creato un e-service in stato {string} che richiede quell'attributo certificato con approvazione {agreementApprovalPolicy}")
    public void createEServiceWithCertifiedAttribute(String tenantType, String descriptorState, AgreementApprovalPolicy agreementApprovalPolicy) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed(),
                new UpdateEServiceDescriptorSeed()
                        .agreementApprovalPolicy(agreementApprovalPolicy)
                        .attributes(new DescriptorAttributesSeed()
                                .certified(List.of(List.of(new DescriptorAttributeSeed()
                                        .id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                        .explicitAttributeVerification(true)
                                )))
                        )
        );
        dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(),
                EServiceDescriptorState.fromValue(descriptorState), false);
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    @Given("{string} ha già rifiutato quella richiesta di fruizione")
    public void tenantHasDeclinedThatRequest(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.rejectAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId());
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
        sharedStepsContext.getAgreementCommonContext().setAgreementId(agreementId.orElse(null));
    }

    @Given("{string} ha già creato e inviato una richiesta di fruizione per quell'e-service ed è in attesa di approvazione")
    public void requestForServiceAlreadySubmittedAndPendingApproval(String tenantType) {
        agreementProcessRequest(identityService.getToken(tenantType, null), null);
    }

    @Given("il {delegationRole} ha già creato e inviato una richiesta di fruizione in delega ed è in attesa di approvazione")
    public void delegationRequestForServiceAlreadySubmittedAndPendingApproval(DelegationRole delegationRole) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
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
        sharedStepsContext.getAgreementCommonContext().setAgreementId(agreementId);

        dataPreparationService.submitAgreement(agreementId, AgreementState.PENDING);
    }

    /* FIXME momentaneamente disabilitato per togliere l'ambiguità con lo step tenantHasAlreadyCreateEServiceWhichRequireCertifiedAttribute e poter definire i test senza errori
    @Given("{string} ha già creato un e-service in stato {string} che richiede quell'attributo certificato con approvazione {agreementApprovalPolicy}")
    public void tenantHasAlreadyCreateEServiceWhichRequireCertifiedAttribute(String tenantType, String descriptorState, AgreementApprovalPolicy agreementApprovalPolicy) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed(),
                new UpdateEServiceDescriptorSeed()
                        .attributes(new DescriptorAttributesSeed()
                                .addCertifiedItem(List.of(new DescriptorAttributeSeed()
                                .setId(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                .explicitAttributeVerification(true)))
                        )
                        .agreementApprovalPolicy(agreementApprovalPolicy)
        );
        dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(),
                EServiceDescriptorState.valueOf(descriptorState), false);
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(eServiceDescriptor.getDescriptorId());
    } */

    @Given("{string} ha già revocato quell'attributo a {string}")
    public void tenantHasAlreadyRevokedAttributeToSpecificTenant(String certifier, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(certifier, null));
        UUID tenantId = identityService.getOrganizationId(tenantType);

        dataPreparationService.revokeCertifiedAttributeToTenant(tenantId, sharedStepsContext.getAttributeCommonContext().getAttributeId());
    }

    @And("la richiesta di fruizione è in stato {string}")
    @Given("la richiesta di fruizione è passata in stato {string}")
    public void verifyAgreementState(String agreementState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementById(sharedStepsContext.getAgreementCommonContext().getAgreementId()),
                res -> res.getState().getValue().equals(agreementState),
                String.format("The agreement is not in the expected state %s", agreementState)
        );
    }

    @When("l'utente crea una richiesta di fruizione in bozza per (la penultima)(l'ultima) versione di quell'e-service")
    public void createDraftAgreementRequestForLatestVersion() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().createAgreement(
                        new AgreementPayload()
                                .eserviceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
                                .descriptorId(sharedStepsContext.getEServicesCommonContext().getDescriptorId())
                ));
    }

    @Given("{string} ha creato un attributo certificato e non lo ha assegnato a {string}")
    public void tenantHasAlreadyCreatedCertifiedAttributeNotAssigned(String certifier, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(certifier, null));

        sharedStepsContext.getAttributeCommonContext().setAttributeId(
                dataPreparationService.createAttribute(AttributeKind.CERTIFIED, null).getId()
        );
    }

    @Given("{string} ha già pubblicato una nuova versione per quell'e-service")
    public void tenantHasAlreadyPublishedNewEServiceVersion(String tenantType) {
        bringDescriptorToGivenState(tenantType, EServiceDescriptorState.PUBLISHED, false);
    }

    @Given("{string} ha già pubblicato una nuova versione per quell'e-service asincrono")
    public void tenantHasAlreadyPublishedNewAsyncEServiceVersion(String tenantType) {
        bringDescriptorToGivenState(tenantType, EServiceDescriptorState.PUBLISHED, true);
    }

    private void bringDescriptorToGivenState(String tenantType, EServiceDescriptorState state, boolean isAsync) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setOldDescriptorId(eServicesCommonContext.getDescriptorId());
        eServicesCommonContext.setDescriptorId(dataPreparationService.createNextDraftDescriptor(eServicesCommonContext.getEserviceId()));
        dataPreparationService.bringDescriptorToGivenState(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(),
                state, false, isAsync);
    }
}
