package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TenantAssignVerifiedAttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public TenantAssignVerifiedAttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = this.sharedStepsContext.getIdentityService();
    }

    @When("l'utente assegna a {string} l'attributo verificato precedentemente creato")
    public void assignVerifiedAttribute(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().verifyVerifiedAttribute(
                        tenantId,
                        new VerifiedTenantAttributeSeed()
                                .id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                .agreementId(sharedStepsContext.getAgreementCommonContext().getAgreementId())
                )
        );
    }

    @When("il delegato all'erogazione assegna a {string} l'attributo verificato precedentemente creato")
    public void delegatedProducerAssignVerifiedAttribute(String tenantType) {
        delegatedProducerAssignVerifiedAttribute(
                tenantType, sharedStepsContext.getDelegationCommonContext().getDelegationId()
        );
    }

    @When("specificando un ID delega casuale l'utente assegna a {string} l'attributo verificato precedentemente creato")
    public void delegatedProducerAssignVerifiedAttributeWithRandomDelegationId(String tenantType) {
        delegatedProducerAssignVerifiedAttribute(tenantType, UUID.randomUUID());
    }

    private void delegatedProducerAssignVerifiedAttribute(String tenantType, UUID delegationId) {
        String delegateTenant = sharedStepsContext.getDelegationCommonContext().getDelegateTenant();
        // Se non è stato indicato un delegato, usa il tenant corrente
        if (delegateTenant == null) {
            clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        } else {
            clientTokenConfigurator.setBearerToken(identityService.getToken(delegateTenant, null));
        }
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getAttributeCommonContext().setAttributeConsumerTenant(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().verifyVerifiedAttribute(
                        tenantId,
                        new VerifiedTenantAttributeSeed()
                                .id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                .agreementId(sharedStepsContext.getAgreementCommonContext().getAgreementId())
                        //.delegationId(delegationId) TODO si attende implementazione in DEV/QA di PIN-7448 (QA-18355 è in Blocked)
                )
        );
    }

    @When("l'utente assegna a {string} l'attributo verificato precedentemente creato con data di scadenza nel futuro")
    public void assignVerifiedAttributeWithFutureExpireDate(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().verifyVerifiedAttribute(
                        tenantId,
                        new VerifiedTenantAttributeSeed()
                                .id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                .agreementId(sharedStepsContext.getAgreementCommonContext().getAgreementId())
                                .expirationDate(OffsetDateTime.now().plusDays(7).toString())
                )
        );
    }

    @When("l'utente assegna a {string} l'attributo verificato precedentemente creato con data di scadenza nel passato")
    public void assignVerifiedAttributeWithOldExpireDate(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().verifyVerifiedAttribute(
                        tenantId,
                        new VerifiedTenantAttributeSeed()
                                .id(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                                .agreementId(sharedStepsContext.getAgreementCommonContext().getAgreementId())
                                .expirationDate(LocalDate.now().minusDays(7).toString())
                )
        );
    }
}
