package it.pagopa.pn.interop.cucumber.steps.agreement;

import static java.time.OffsetDateTime.now;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Builder;
import lombok.Data;

@Data
public class AgreementCommonSteps {
    private ClientTokenConfigurator clientTokenConfigurator;
    private BFFDataPreparationService dataPreparationService;
    private IdentityService identityService;
    private SharedStepsContext sharedStepsContext;
    private IAgreementClient agreementClient;
    private IEServiceClient eserviceClient;
    private PollingService pollingService;
    private IHttpExecutor httpCallExecutor;

    public AgreementCommonSteps(ClientTokenConfigurator clientTokenConfigurator,
                                BFFDataPreparationService dataPreparationService,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.agreementClient = clientTokenConfigurator.getAgreementClient();
        this.eserviceClient = clientTokenConfigurator.getEServiceClient();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Data
    @Builder
    public static class EServiceConfig {
        private Boolean delegable;
        private Boolean clientAccessDelegable;
        private AgreementApprovalPolicy agreementApprovalPolicy;
    }

    @Given("{string} ha una richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantAlreadyHasFruitionRequestWithState(String consumer, String agreementState) {
        String token = identityService.getToken(consumer, null);
        tenantAlreadyHasFruitionRequestWithState(agreementState, token, null);
    }

    @Given("il {delegationRole} ha una richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantAlreadyHasFruitionRequestWithState(DelegationRole delegationRole, String agreementState) {
        String tenant = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        String token = identityService.getToken(tenant, null);
        UUID delegationId = sharedStepsContext.getDelegationCommonContext().getDelegationId();
        tenantAlreadyHasFruitionRequestWithState(agreementState, token, delegationId);
    }

    private void tenantAlreadyHasFruitionRequestWithState(String agreementState, String token, UUID delegationId) {
        clientTokenConfigurator.setBearerToken(token);
        UUID agreementId = dataPreparationService.createAgreementWithGivenState(
            AgreementState.fromValue(agreementState),
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
            delegationId,
            null);
        sharedStepsContext.setAgreementId(agreementId);
        sharedStepsContext.getAgreementCommonContext().setAgreementCreationTime(now());
    }

    @Given("{string} ha creato un attributo certificato e lo ha assegnato a {string}")
    public void tenantHasCreatedCertifiedAttribute(String certifier, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(certifier, null));
        UUID tenantId = identityService.getOrganizationId(tenantType);
        Attribute attribute = dataPreparationService.createAttribute(AttributeKind.CERTIFIED, null);
        sharedStepsContext.getAttributeCommonContext().addCreatedAttribute(attribute);
        dataPreparationService.assignCertifiedAttributeToTenant(tenantId, attribute.getId());
    }

    /* NOTA 26/03/2025: al momento usato solo in scenari di test negativi (in altri termini: non
     * è stato testato in situazioni in cui ci si aspetta che funzioni) */
    @Given("{string} ha creato un attributo dichiarato e lo ha assegnato a {string}")
    public void tenantHasCreatedDeclaredAttribute(String certifier, String tenantType) {
        AttributeKind attributeKind = AttributeKind.DECLARED;
        clientTokenConfigurator.setBearerToken(identityService.getToken(certifier, null));
        UUID tenantId = identityService.getOrganizationId(tenantType);
        Attribute attribute = dataPreparationService.createAttribute(
            attributeKind, null);
        sharedStepsContext.getAttributeCommonContext().addCreatedAttribute(attribute);
        dataPreparationService.assignDeclaredAttributeToTenant(tenantId, attribute.getId());
    }

    @Given("{string} ha già creato e pubblicato {int} e-service(s)")
    public void tenantHasAlreadyCreatedAndPublishedEService(String tenantType, int totalEservices) {
        tenantHasAlreadyCreatedAndPublishedEService(tenantType, totalEservices, Optional.empty());
    }

    @Given("{string} ha già creato e pubblicato {int} e-service(s) delegabile(i) in fruizione con approvazione {agreementApprovalPolicy}")
    public void tenantHasAlreadyCreatedAndPublishedDelegableEService(String tenantType, int totalEservices, AgreementApprovalPolicy agreementApprovalPolicy) {
        EServiceConfig build = EServiceConfig.builder()
                .delegable(true)
                .agreementApprovalPolicy(agreementApprovalPolicy)
                .build();
        tenantHasAlreadyCreatedAndPublishedEService(tenantType, totalEservices, Optional.of(build));
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
        List<EServiceDescriptor> eServiceDescriptorList = new ArrayList<>();
        for (int i = 0; i < totalEservices; i++) {
            // Create e-service and descriptor
            int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            String eserviceName = String.format("eservice-%d-%d-%d", i, sharedStepsContext.getTestSeed(), randomInt);
            EServiceSeed eserviceSeed = new EServiceSeed()
                .name(eserviceName)
                .isConsumerDelegable(eServiceConfig.map(EServiceConfig::getDelegable).orElse(null))
                .isClientAccessDelegable(eServiceConfig.map(EServiceConfig::getClientAccessDelegable).orElse(null));
            EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                eserviceSeed, new UpdateEServiceDescriptorSeed().agreementApprovalPolicy(eServiceConfig.map(EServiceConfig::getAgreementApprovalPolicy).orElse(null)));
            sharedStepsContext.getEServicesCommonContext().setCreationTimestamp(OffsetDateTime.now());

            // Set the descriptor to "PUBLISHED" state
            dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.PUBLISHED, false);
            sharedStepsContext.getEServicesCommonContext().setPublicationTimestamp(OffsetDateTime.now());
            // Add the e-service to the list of published ones
            eServiceDescriptorList.add(eServiceDescriptor);
        }
        // Set the first e-service and descriptor
        if (!eServiceDescriptorList.isEmpty()) {
            EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
            eServicesCommonContext.setPublishedEservicesIds(eServiceDescriptorList);
            EServiceDescriptor firstDescriptor = eServiceDescriptorList.get(0);
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

    @And("il {delegationRole} controlla che la richiesta di fruizione sia stata archiviata")
    public void verifyAgreementIsArchived(DelegationRole delegationRole) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        pollingService.makePolling(
                () -> agreementClient.getAgreementById(sharedStepsContext.getAgreementId()),
                res -> res.getState().equals(AgreementState.ARCHIVED),
                "The agreement was not archived"
        );
    }

    @Given("{string} ha già dichiarato un attributo")
    public void tenantDeclaresAnAttribute(String tenantType) {
        UUID tenantId = this.identityService.getOrganizationId(tenantType);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID attributeId = dataPreparationService.createAttribute(AttributeKind.DECLARED, null).getId();
        dataPreparationService.declareDeclaredAttribute(tenantId, attributeId);
        sharedStepsContext.getAttributeCommonContext().getRequiredDeclaredAttributes().add(List.of(attributeId));
        sharedStepsContext.getAttributeCommonContext().setAttributeId(attributeId);
    }

    @Given("{string} ha già creato una richiesta di fruizione in stato {string} con un documento allegato")
    public void tenantHasAlreadyCreatedAgreementWithSpecificStateAndAttachments(String consumer, String agreementState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(consumer, null));
        Map<String, UUID> result = dataPreparationService.createAgreementWithGivenStateAndDocument(
                AgreementState.fromValue(agreementState), sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId());
        sharedStepsContext.setAgreementId(result.get("agreementId"));
        sharedStepsContext.getAgreementCommonContext().setDocumentId(result.get("documentId"));
    }

    @Given("{string} ha una richiesta di fruizione in stato {string} per ognuno di quegli e-services")
    public void tenantHasAlreadyAnAgreementForEachEService(String consumer, String agreementState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(consumer, null));

        List<UUID> agreementIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds()
                        .stream()
                        .map(eServiceDescriptor -> dataPreparationService.createAgreementWithGivenState(
                                AgreementState.fromValue(agreementState),
                                eServiceDescriptor.getEServiceId(),
                                eServiceDescriptor.getDescriptorId(),
                                null))
                        .toList();
        sharedStepsContext.getAgreementCommonContext().setAgreementIds(agreementIds);
    }

    @When("l'utente tenta la modifica di agreementApprovalPolicy in {string}")
    public void editAgreementApprovalPolicy(String agreementApprovalPolicy) {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        editAgreementApprovalPolicy(agreementApprovalPolicy, eserviceId, descriptorId);
    }

    @When("l'utente tenta la modifica di agreementApprovalPolicy specificando un valore vuoto")
    public void editAgreementApprovalPolicyWithEmptyValue() {
        editAgreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC.toString(), null, null);
    }


    @When("l'utente tenta la modifica di agreementApprovalPolicy di un e-service inesistente")
    public void editAgreementApprovalPolicyOfNonExistentEService() {
        UUID eserviceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        editAgreementApprovalPolicy("AUTOMATIC", eserviceId, descriptorId);
    }

    private void editAgreementApprovalPolicy(String agreementApprovalPolicy, UUID eserviceId, UUID descriptorId) {
        httpCallExecutor.performCall(() -> eserviceClient.editAgreementApprovalPolicy(
            eserviceId,
            descriptorId,
            AgreementApprovalPolicy.fromValue(agreementApprovalPolicy)));
    }

    @Then("il valore di agreementApprovalPolicy dell'e-service è adesso {string}")
    public void checkAgreementApprovalPolicy(String agreementApprovalPolicy) {
        pollingService.makePolling(() -> eserviceClient.getEServiceDescriptor(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId())
            .getAgreementApprovalPolicy(),
            res -> res.equals(AgreementApprovalPolicy.fromValue(agreementApprovalPolicy)),
            "The agreementApprovalPolicy was not updated");
    }

    @And("l'utente crea una nuova versione dell'e-service")
    public void createNewVersionOfEService() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID newVersion = dataPreparationService.createNextDraftDescriptor(eServiceId);
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(newVersion);
    }

    @And("l'utente delegato pubblica la versione dell'e-service")
    public void publishNewVersionOfEService() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        dataPreparationService.bringDescriptorToGivenState(eServiceId, descriptorId, EServiceDescriptorState.WAITING_FOR_APPROVAL, false);
    }

    @And("l'utente delegante approva la versione dell'e-service")
    public void approveNewVersionOfEService() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        dataPreparationService.approveDelegatedEServiceDescriptor(eServiceId, descriptorId);
    }
}
