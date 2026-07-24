package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.agreement.model.EServiceAttributeSpec;
import it.pagopa.pn.interop.cucumber.steps.agreement.utils.AgreementResolver;
import it.pagopa.pn.interop.cucumber.steps.catalog.DescriptorUpdateSteps;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.util.*;
import java.util.function.BiConsumer;

import static java.util.Objects.nonNull;

@Slf4j
public class AgreementActivateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final BFFDataPreparationService dataPreparationService;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final AgreementResolver agreementResolver;

    public AgreementActivateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                  BFFDataPreparationService dataPreparationService,
                                  SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.agreementResolver = new AgreementResolver(sharedStepsContext);
    }

    @Given("{string} ha già sospeso quella richiesta di fruizione come {clientType}")
    public void tenantHasAlreadySuspendedThatRequest(String tenantType, ClientType status) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.suspendAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), status);
    }

    @Given("{string} ha già approvato quella richiesta di fruizione")
    public void tenantHasAlreadyAcceptedThatRequest(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.approveAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), null);
    }

    @Given("{string} ha già riattivato quella richiesta di fruizione come {clientType}")
    public void tenantHasAlreadyUnsuspendedThatRequest(String tenantType, ClientType status) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.unsuspendAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), status, null);
    }

    @Given("l'ente {delegationRole} ha già approvato quella richiesta di fruizione")
    public void tenantHasAlreadyAcceptedThatRequest(DelegationRole delegationRole) throws InterruptedException {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        clientTokenConfigurator.setBearerToken(token);
        dataPreparationService.approveAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    @Given("l'ente {delegationRole} ha già riattivato quella richiesta di fruizione come {clientType}")
    public void tenantHasAlreadyUnsuspendedThatRequest(DelegationRole delegationRole, ClientType status) throws InterruptedException {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        clientTokenConfigurator.setBearerToken(token);
        dataPreparationService.unsuspendAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), status, new DelegationRef().delegationId(sharedStepsContext.getDelegationCommonContext().getDelegationId()));
    }

    @Given("{string} ha già creato un e-service in stato {string} che richiede quegli attributi con approvazione {string}")
    public void tenantHasAlreadyCreateEservice(String tenantType, String descriptorState, String approvalAgreementPolicy) {
        tenantHasAlreadyCreateEservice(tenantType, descriptorState, approvalAgreementPolicy, 50, 100);
    }

    @Given("{string} ha già creato un e-service in stato {string} che richiede quegli attributi con approvazione {string} con dailyCallsPerConsumer uguale a {int} e dailyCallsTotal uguale a {int}")
    public void tenantHasAlreadyCreateEservice(String tenantType, String descriptorState, String approvalAgreementPolicy, Integer dailyCallsPerConsumer, Integer dailyCallsTotal) {

        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        sharedStepsContext.getEServicesCommonContext().setProducerName(identityService.getTenantName(tenantType));

        AttributeCommonContext attributeCommonContext = sharedStepsContext.getAttributeCommonContext();

        DescriptorAttributesSeed descriptorAttributesSeed = new DescriptorAttributesSeed();
        descriptorAttributesSeed.setCertified(
                attributeCommonContext.mapAttributesWithDefaultValues(attributeCommonContext.getRequiredCertifiedAttributes())
        );
        descriptorAttributesSeed.setDeclared(
                attributeCommonContext.mapAttributesWithDefaultValues(attributeCommonContext.getRequiredDeclaredAttributes())
        );
        descriptorAttributesSeed.setVerified(
                attributeCommonContext.mapAttributesWithDefaultValues(attributeCommonContext.getRequiredVerifiedAttributes())
        );

        UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed = new UpdateEServiceDescriptorSeed()
                .attributes(descriptorAttributesSeed)
                .agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(approvalAgreementPolicy))
                .dailyCallsPerConsumer(dailyCallsPerConsumer)
                .dailyCallsTotal(dailyCallsTotal);

        try {
            EServiceDescriptor result = dataPreparationService.createEServiceAndDraftDescriptor(
                    new EServiceSeed(), updateEServiceDescriptorSeed
            );
            UUID eserviceId = result.getEServiceId();
            UUID descriptorId = result.getDescriptorId();
            dataPreparationService.bringDescriptorToGivenState(eserviceId, descriptorId, EServiceDescriptorState.valueOf(descriptorState), false);
            sharedStepsContext.getEServicesCommonContext().setEserviceId(eserviceId);
            sharedStepsContext.getEServicesCommonContext().setDescriptorId(descriptorId);
        } catch (AssertionFailedError e) {
            log.warn(e.getMessage());
        }
    }

    @When("{string} ha già creato un e-service in stato {string} con approvazione {string} con dailyCallsPerConsumer uguale a {int} e dailyCallsTotal uguale a {int} e con i seguenti attributi:")
    public void tenantHasAlreadyCreateEservice(String tenantType, String descriptorState, String approvalAgreementPolicy, Integer dailyCallsPerConsumer, Integer dailyCallsTotal, List<EServiceAttributeSpec> attributesSpec) {

        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UpdateEServiceDescriptorSeed updateSeed = DescriptorUpdateSteps.createUpdateEServiceDescriptorSeedAndUpdateContext(
                sharedStepsContext, dataPreparationService, attributesSpec
        );
        updateSeed.agreementApprovalPolicy(AgreementApprovalPolicy.valueOf(approvalAgreementPolicy))
                .dailyCallsPerConsumer(dailyCallsPerConsumer)
                .dailyCallsTotal(dailyCallsTotal);

        try {
            EServiceDescriptor result = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed(), updateSeed);
            dataPreparationService.bringDescriptorToGivenState(result.getEServiceId(), result.getDescriptorId(), EServiceDescriptorState.valueOf(descriptorState), false);

            sharedStepsContext.getEServicesCommonContext().setEserviceId(result.getEServiceId());
            sharedStepsContext.getEServicesCommonContext().setDescriptorId(result.getDescriptorId());
        } catch (AssertionFailedError e) {
            log.warn("Errore durante la creazione dell'e-service: {}", e.getMessage());
        }
    }

    @Given("l'e-service ha questa configurazione:")
    public void eServiceHasThisConfiguration(DataTable dataTable) {

        Map<String, String> attributes = dataTable.asMap();

        boolean waitForAsyncProps = attributes.keySet().stream()
                .anyMatch(key -> key.startsWith("asyncExchangeProperties."));

        ProducerEServiceDescriptor eServiceDescriptor = sharedStepsContext.getPollingService().makePolling(
                () -> this.clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                ),
                res -> (!waitForAsyncProps) || nonNull(res.getAsyncExchangeProperties()),
                res -> "Le async property del descrittore risultano non impostate"
        );

        if (attributes.containsKey("dailyCallsPerConsumer")) {
            Assertions.assertEquals(Integer.parseInt(attributes.get("dailyCallsPerConsumer")), eServiceDescriptor.getDailyCallsPerConsumer());
        }
        if (attributes.containsKey("dailyCallsTotal")) {
            Assertions.assertEquals(Integer.parseInt(attributes.get("dailyCallsTotal")), eServiceDescriptor.getDailyCallsTotal());
        }
        if (attributes.containsKey("asyncExchangeProperties.responseTime")) {
            Assertions.assertNotNull(eServiceDescriptor.getAsyncExchangeProperties());
            Assertions.assertEquals(Integer.parseInt(attributes.get("asyncExchangeProperties.responseTime")), eServiceDescriptor.getAsyncExchangeProperties().getResponseTime());
        }
        if (attributes.containsKey("asyncExchangeProperties.resourceAvailableTime")) {
            Assertions.assertNotNull(eServiceDescriptor.getAsyncExchangeProperties());
            Assertions.assertEquals(Integer.parseInt(attributes.get("asyncExchangeProperties.resourceAvailableTime")), eServiceDescriptor.getAsyncExchangeProperties().getResourceAvailableTime());
        }
        if (attributes.containsKey("asyncExchangeProperties.confirmation")) {
            Assertions.assertNotNull(eServiceDescriptor.getAsyncExchangeProperties());
            Assertions.assertEquals(Boolean.parseBoolean(attributes.get("asyncExchangeProperties.confirmation")), eServiceDescriptor.getAsyncExchangeProperties().getConfirmation());
        }
        if (attributes.containsKey("asyncExchangeProperties.bulk")) {
            Assertions.assertNotNull(eServiceDescriptor.getAsyncExchangeProperties());
            Assertions.assertEquals(Boolean.parseBoolean(attributes.get("asyncExchangeProperties.bulk")), eServiceDescriptor.getAsyncExchangeProperties().getBulk());
        }
        if (attributes.containsKey("asyncExchangeProperties.maxResultSet")) {
            Assertions.assertNotNull(eServiceDescriptor.getAsyncExchangeProperties());
            Assertions.assertEquals(Integer.parseInt(attributes.get("asyncExchangeProperties.maxResultSet")), eServiceDescriptor.getAsyncExchangeProperties().getMaxResultSet());
        }
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
                sharedStepsContext.getAgreementCommonContext().getAgreementId(), null);
    }

    @When("l'utente richiede una operazione di approvazione della richiesta di fruizione con id {string}")
    public void userRequiresAgreementApprovalWithId(String agreementId) {
        UUID resolvedAgreementId = agreementResolver.resolveAgreementId(agreementId);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .approveAgreement(resolvedAgreementId, null));
    }

    @When("l'utente richiede una operazione di riattivazione della richiesta di fruizione con id {string}")
    public void userRequiresAgreementUnsuspensionWithId(String agreementId) {
        UUID resolvedAgreementId = agreementResolver.resolveAgreementId(agreementId);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .unsuspendAgreement(resolvedAgreementId, null));
    }

    @When("l'utente {string} di {string} richiede una operazione di approvazione della richiesta di fruizione con id {string}")
    public void userRequiresAgreementApproval(String role, String tenant, String agreementId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        UUID resolvedAgreementId = agreementResolver.resolveAgreementId(agreementId);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .approveAgreement(resolvedAgreementId, null));
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'utente {string} di {string} richiede una operazione di riattivazione di quella richiesta di fruizione")
    public void userRequiresAgreementUnsuspension(String role, String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .unsuspendAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), null));
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'ente {delegationRole} con id della delega {string} richiede una operazione di approvazione di quella richiesta di fruizione")
    public void userRequiresAgreementApprovalWithDelegate(DelegationRole delegationRole, String delegationId) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .approveAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), new DelegationRef().delegationId(agreementResolver.resolveDelegationId(delegationId))));
    }

    @When("l'ente {delegationRole} con id della delega {string} richiede una operazione di riattivazione di quella richiesta di fruizione")
    public void userRequiresAgreementUnsuspensionWithDelegate(DelegationRole delegationRole, String delegationId) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        clientTokenConfigurator.setBearerToken(token);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient()
                        .unsuspendAgreement(sharedStepsContext.getAgreementCommonContext().getAgreementId(), new DelegationRef().delegationId(agreementResolver.resolveDelegationId(delegationId))));
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
        createTwoSpecificAttributeKind(AttributeKind.CERTIFIED, consumerId, consumerFunction, true);
    }

    @Given("due gruppi di due attributi certificati da {string}, dei quali {string} li possiede tutti")
    public void tenantHasAllCertifiedAttributeGroups(String certifier, String consumer) {
        UUID consumerId = identityService.getOrganizationId(consumer);
        clientTokenConfigurator.setBearerToken(identityService.getToken(certifier, null));
        BiConsumer<UUID, UUID> consumerFunction = dataPreparationService::assignCertifiedAttributeToTenant;
        createTwoSpecificAttributeKind(AttributeKind.CERTIFIED, consumerId, consumerFunction, false);
    }

    @Given("due gruppi di due attributi dichiarati, dei quali {string} ne possiede uno per gruppo")
    public void tenantHasTwoDeclaredAttributeGroups(String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        BiConsumer<UUID, UUID> consumerFunction = dataPreparationService::declareDeclaredAttribute;
        createTwoSpecificAttributeKind(AttributeKind.DECLARED, tenantId, consumerFunction, true);
    }

    private void createTwoSpecificAttributeKind(AttributeKind attributeKind, UUID tenantId, BiConsumer<UUID, UUID> consumerFunction, boolean assignOnlyFirstAttribute) {
        List<List<UUID>> requiredAttributes = new ArrayList<>();

        for (int groupIdx = 0; groupIdx < 2; groupIdx++) {
            List<UUID> attributeGroup = new ArrayList<>();

            for (int attrIdx = 0; attrIdx < 2; attrIdx++) {
                UUID attributeId = dataPreparationService.createAttribute(attributeKind, null).getId();
                attributeGroup.add(attributeId);

                if (attrIdx == 0 || !assignOnlyFirstAttribute) {
                    consumerFunction.accept(tenantId, attributeId);
                }
            }

            requiredAttributes.add(attributeGroup);
        }
        if (attributeKind == AttributeKind.VERIFIED)
            sharedStepsContext.getAttributeCommonContext().setRequiredVerifiedAttributes(requiredAttributes);
        else if (attributeKind == AttributeKind.CERTIFIED)
            sharedStepsContext.getAttributeCommonContext().setRequiredCertifiedAttributes(requiredAttributes);
        else if (attributeKind == AttributeKind.DECLARED)
            sharedStepsContext.getAttributeCommonContext().setRequiredDeclaredAttributes(requiredAttributes);
        else
            throw new IllegalArgumentException("Unsupported AttributeKind: " + attributeKind);
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
            dataPreparationService.assignVerifiedAttributeToTenant(consumerId, verifierId, attributeId, sharedStepsContext.getAgreementCommonContext().getAgreementId(), null);
        }
    }
}
