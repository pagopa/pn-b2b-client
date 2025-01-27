package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Builder;
import lombok.Data;

@Data
public class AgreementCommonSteps {
    private ClientTokenConfigurator clientTokenConfigurator;
    private DataPreparationService dataPreparationService;
    private IdentityService identityService;
    private SharedStepsContext sharedStepsContext;

    public AgreementCommonSteps(ClientTokenConfigurator clientTokenConfigurator,
                                DataPreparationService dataPreparationService,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @Data
    @Builder
    public static class EServiceConfig {
        private Boolean delegable;
        private Boolean clientAccessDelegable;
    }

    @Given("{string} ha una richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantAlreadyHasFruitionRequestWithState(String tenant, String agreementState) {
        tenantAlreadyHasFruitionRequestWithState(agreementState, tenant, null);
    }

    @Given("il {delegationRole} ha una richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantAlreadyHasFruitionRequestWithState(DelegationRole delegationRole, String agreementState) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        UUID delegationId = sharedStepsContext.getDelegationCommonContext().getDelegationId();
        tenantAlreadyHasFruitionRequestWithState(agreementState, tenant, delegationId);
    }

    private void tenantAlreadyHasFruitionRequestWithState(String agreementState, String tenant, UUID delegationId) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        UUID agreementId = dataPreparationService.createAgreementWithGivenState(
            AgreementState.fromValue(agreementState),
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
            delegationId,
            null);
        sharedStepsContext.setAgreementId(agreementId);
    }

    @Given("{string} ha creato un attributo certificato e lo ha assegnato a {string}")
    public void tenantHasCreatedCertifiedAttribute(String certifier, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID attributeId = dataPreparationService.createAttribute(AttributeKind.CERTIFIED, null);
        dataPreparationService.assignCertifiedAttributeToTenant(tenantId, attributeId);
    }

    @Given("{string} ha già creato e pubblicato {int} e-service(s)")
    public void tenantHasAlreadyCreatedAndPublishedEService(String tenantType, int totalEservices) {
        tenantHasAlreadyCreatedAndPublishedEService(tenantType, totalEservices, Optional.empty());
    }

    @Given("{string} ha già creato e pubblicato {int} e-service(s) delegabile(i) in fruizione")
    public void tenantHasAlreadyCreatedAndPublishedDelegableEService(String tenantType, int totalEservices) {
        EServiceConfig build = EServiceConfig.builder()
            .delegable(true)
            .build();
        tenantHasAlreadyCreatedAndPublishedEService(tenantType, totalEservices, Optional.of(build));
    }

    @Given("{string} ha già creato e pubblicato {int} e-service(s) delegabile(i) in fruizione con client del delegato utilizzabile")
    public void tenantHasAlreadyCreatedAndPublishedDelegableEServiceWithClientAccessDelegable(String tenantType, int totalEservices) {
        EServiceConfig build = EServiceConfig.builder()
            .delegable(true)
            .clientAccessDelegable(true)
            .build();
        tenantHasAlreadyCreatedAndPublishedEService(tenantType, totalEservices, Optional.of(build));
    }

    public void tenantHasAlreadyCreatedAndPublishedEService(String tenantType, int totalEservices, Optional<EServiceConfig> eServiceConfig) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        // Create e-services and publish descriptors
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        for (int i = 0; i < totalEservices; i++) {
            // Create e-service and descriptor
            int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            String eserviceName = String.format("eservice-%d-%d-%d", i, sharedStepsContext.getTestSeed(), randomInt);
            EServiceSeed eserviceSeed = new EServiceSeed()
                .name(eserviceName)
                .isDelegable(eServiceConfig.map(EServiceConfig::getDelegable).orElse(null))
                .isClientAccessDelegable(eServiceConfig.map(EServiceConfig::getClientAccessDelegable).orElse(null));
            EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                eserviceSeed, new UpdateEServiceDescriptorSeed());
            // Set the descriptor to "PUBLISHED" state
            dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.PUBLISHED, false);
            // Add the e-service to the list of published ones
            eServicesCommonContext.getPublishedEservicesIds().add(eServiceDescriptor);
        }
        // Set the first e-service and descriptor
        if (!eServicesCommonContext.getPublishedEservicesIds().isEmpty()) {
            EServiceDescriptor firstDescriptor = eServicesCommonContext.getPublishedEservicesIds().get(0);
            eServicesCommonContext.setEserviceId(firstDescriptor.getEServiceId());
            eServicesCommonContext.setDescriptorId(firstDescriptor.getDescriptorId());
        }
    }

    @Given("l'ente {delegationRole} ha già creato e pubblicato {int} e-service(s)")
    public void tenantHasAlreadyCreatedAndPublishedEService(DelegationRole delegationRole, int totalEservices) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        tenantHasAlreadyCreatedAndPublishedEService(tenantType, totalEservices);
    }

    @Given("{string} ha già creato un e-service in stato {string} con approvazione {string}")
    public void tenantHasAlreadyCreatedEServiceWithStatusAndApproval(String tenantType, String descriptorState, String agreementApprovalPolicy) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed(),
                new UpdateEServiceDescriptorSeed().agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(agreementApprovalPolicy)));

        dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.valueOf(descriptorState), false);
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(eServiceDescriptor.getDescriptorId());
    }
}
