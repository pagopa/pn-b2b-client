package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class AgreementActivateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final BFFDataPreparationService dataPreparationService;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public AgreementActivateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                  BFFDataPreparationService dataPreparationService,
                                  SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @Given("{string} ha già sospeso quella richiesta di fruizione come {clientType}")
    public void tenantHasAlreadySuspendedThatRequest(String tenantType, ClientType status) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.suspendAgreement(sharedStepsContext.getAgreementId(), status);
    }

    @Given("{string} ha già approvato quella richiesta di fruizione")
    public void tenantHasAlreadyAcceptedThatRequest(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.activateAgreement(sharedStepsContext.getAgreementId(), null, null);
    }

    @Given("l'ente {delegationRole} ha già approvato quella richiesta di fruizione")
    public void tenantHasAlreadyAcceptedThatRequest(DelegationRole delegationRole) throws InterruptedException {
        Thread.sleep(5000);
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        clientTokenConfigurator.setBearerToken(token);
        dataPreparationService.activateAgreement(sharedStepsContext.getAgreementId(), null, new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    @Given("{string} ha già creato un e-service in stato {string} che richiede quegli attributi con approvazione {string}")
    public void tenantHasAlreadyCreateEservice(String tenantType, String descriptorState, String approvalAgreementPolicy) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        List<List<UUID>> requiredCertifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes();
        List<List<UUID>> requiredDeclaredAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes();
        List<List<UUID>> requiredVerifiedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes();

        EServiceDescriptor result = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed(),
                new UpdateEServiceDescriptorSeed().attributes(new DescriptorAttributesSeed()
                                .addCertifiedItem(
                                        requiredCertifiedAttributes.stream()
                                                .flatMap(group -> group.stream()
                                                        .map(attrId -> new DescriptorAttributeSeed().id(attrId).explicitAttributeVerification(true)))
                                                .collect(Collectors.toList()))
                                .addDeclaredItem(
                                        requiredDeclaredAttributes.stream()
                                        .flatMap(group -> group.stream()
                                                .map(attrId -> new DescriptorAttributeSeed().id(attrId).explicitAttributeVerification(true)))
                                        .collect(Collectors.toList()))
                                .addVerifiedItem(
                                        requiredVerifiedAttributes.stream()
                                                .flatMap(group -> group.stream()
                                                        .map(attrId -> new DescriptorAttributeSeed().id(attrId).explicitAttributeVerification(true)))
                                                .collect(Collectors.toList())
                                ))
                        .agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(approvalAgreementPolicy))
        );
        UUID eserviceId = result.getEServiceId();
        UUID descriptorId = result.getDescriptorId();
        dataPreparationService.bringDescriptorToGivenState(eserviceId, descriptorId, EServiceDescriptorState.valueOf(descriptorState), false);
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eserviceId);
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(descriptorId);
    }

    @Given("{string} ha già creato un attributo verificato")
    public void tenantHasAlreadyCreatedVerifiedAttribute(String consumer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(consumer, null));
        UUID attributeId = dataPreparationService.createAttribute(AttributeKind.VERIFIED, null).getId();
        sharedStepsContext.getAttributeCommonContext().setAttributeId(attributeId);
        sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes().add(List.of(attributeId));
    }

    @Given("{string} ha già verificato l'attributo verificato a {string}")
    public void tenantHasAlreadyVerifiedAttribute(String verifier, String consumer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(verifier, null));
        UUID consumerId = identityService.getOrganizationId(consumer);
        sharedStepsContext.getAttributeCommonContext().setAttributeConsumerTenant(consumer);

        UUID verifierId = identityService.getOrganizationId(verifier);

        dataPreparationService.assignVerifiedAttributeToTenant(consumerId, verifierId,
                sharedStepsContext.getAttributeCommonContext().getAttributeId(),
                sharedStepsContext.getAgreementId(), null);
    }

    @When("l'utente richiede una operazione di attivazione di quella richiesta di fruizione")
    public void userRequiresAgreementActivation() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .activateAgreement(sharedStepsContext.getAgreementId()));
    }

    @When("l'ente {delegationRole} richiede una operazione di attivazione di quella richiesta di fruizione")
    public void userRequiresAgreementActivationWithDelegate(DelegationRole delegationRole) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .activateAgreement(sharedStepsContext.getAgreementId(), new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId())));
    }

    @Given("due gruppi di due attributi certificati da {string}, dei quali {string} ne possiede uno per gruppo")
    public void tenantHasTwoCertifiedAttributeGroups(String certifier, String consumer) {
        UUID consumerId = identityService.getOrganizationId(consumer);
        clientTokenConfigurator.setBearerToken(identityService.getToken(certifier, null));
/*
        List<List<UUID>> requiredCertifiedAttributes = new ArrayList<>();
        for (int groupIdx = 0; groupIdx < 2; groupIdx++) {
            List<UUID> attributeGroup = new ArrayList<>();

            for (int attrIdx = 0; attrIdx < 2; attrIdx++) {
                UUID attributeId = dataPreparationService.createAttribute(AttributeKind.CERTIFIED, null);

                if (attrIdx % 2 == 0) {
                    dataPreparationService.assignCertifiedAttributeToTenant(consumerId, attributeId);
                }

                attributeGroup.add(attributeId);
            }
            requiredCertifiedAttributes.add(attributeGroup);
        }
        sharedStepsContext.getAttributeCommonContext().setRequiredCertifiedAttributes(requiredCertifiedAttributes);
 */
        BiConsumer<UUID, UUID> consumerFunction = dataPreparationService::assignCertifiedAttributeToTenant;
        createTwoSpecificAttributeKind(AttributeKind.CERTIFIED, consumerId, consumerFunction);

    }

    @Given("due gruppi di due attributi dichiarati, dei quali {string} ne possiede uno per gruppo")
    public void tenantHasTwoDeclaredAttributeGroups(String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        BiConsumer<UUID, UUID> consumerFunction = dataPreparationService::declareDeclaredAttribute;
        createTwoSpecificAttributeKind(AttributeKind.DECLARED, tenantId, consumerFunction);
    }

    private void createTwoSpecificAttributeKind(AttributeKind attributeKind, UUID tenantId, BiConsumer<UUID, UUID> consumerFunction) {
        List<List<UUID>> requiredAttributes = new ArrayList<>();

        for (int groupIdx = 0; groupIdx < 2; groupIdx++) {
            List<UUID> attributeGroup = new ArrayList<>();

            for (int attrIdx = 0; attrIdx < 2; attrIdx++) {
                UUID attributeId = dataPreparationService.createAttribute(attributeKind, null).getId();

                if (attrIdx % 2 == 0) {
                    consumerFunction.accept(tenantId, attributeId);
                }
                requiredAttributes.add(attributeGroup);
            }
        }
        if ((attributeKind == AttributeKind.VERIFIED)) {
            sharedStepsContext.getAttributeCommonContext().setRequiredVerifiedAttributes(requiredAttributes);
        } else {
            sharedStepsContext.getAttributeCommonContext().setRequiredDeclaredAttributes(requiredAttributes);
        }
    }

    @Given("{string} crea due gruppi di due attributi verificati")
    public void tenantCreatesTwoVerifiedAttributeGroups(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        List<List<UUID>> requiredVerifiedAttributes = new ArrayList<>();
        for (int groupIdx = 0; groupIdx < 2; groupIdx++) {
            List<UUID> attributeGroup = new ArrayList<>();
            for (int attrIdx = 0; attrIdx < 2; attrIdx++) {
                UUID attributeId = dataPreparationService.createAttribute(AttributeKind.VERIFIED, null).getId();
                attributeGroup.add(attributeId);
            }

            requiredVerifiedAttributes.add(attributeGroup);
        }
        sharedStepsContext.getAttributeCommonContext().setRequiredVerifiedAttributes(requiredVerifiedAttributes);
    }

    @Given("{string} verifica un attributo per ogni gruppo di attributi verificati a {string}")
    public void tenantVerifyAttributeForEachGroup(String verifier, String consumer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(verifier, null));
        UUID verifierId = identityService.getOrganizationId(verifier);
        UUID consumerId = identityService.getOrganizationId(consumer);

        List<UUID> attributeIdsToVerify = sharedStepsContext.getAttributeCommonContext().getRequiredVerifiedAttributes().stream()
                .map(group -> group.get(0))
                .toList();
        for (UUID attributeId : attributeIdsToVerify) {
            dataPreparationService.assignVerifiedAttributeToTenant(consumerId, verifierId, attributeId, sharedStepsContext.getAgreementId(), null);
        }
    }
}
