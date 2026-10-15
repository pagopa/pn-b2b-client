package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateVerifiedTenantAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TenantUpdateVerifiedExpirationDateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    public TenantUpdateVerifiedExpirationDateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                   SharedStepsContext sharedStepsContext,
                                                   BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha già verificato l'attributo verificato a {string} con una data di scadenza nel futuro")
    public void verifiedTheVerifyAttributeWithFutureExpirationDate(String verifierTenantType, String targetTenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(verifierTenantType, null));
        OffsetDateTime date = OffsetDateTime.now().plusDays(7);
        UUID verifierId = identityService.getOrganizationId(verifierTenantType);
        UUID tenantId = identityService.getOrganizationId(targetTenantType);

        dataPreparationService.assignVerifiedAttributeToTenant(tenantId, verifierId,
                sharedStepsContext.getAttributeCommonContext().getAttributeId(), sharedStepsContext.getAgreementCommonContext().getAgreementId(), date.toString());
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'utente richiede l'aggiornamento di quell'attributo di {string} con una data di scadenza nel futuro")
    public void updateAttributeWithFutureExpirationDate(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        OffsetDateTime date = OffsetDateTime.now().plusDays(7);
        UUID tenantId = identityService.getOrganizationId(tenantType);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().updateVerifiedAttribute(
                        tenantId, sharedStepsContext.getAttributeCommonContext().getAttributeId(), new UpdateVerifiedTenantAttributeSeed().expirationDate(date.toString()))
        );
    }

    @When("l'utente richiede l'aggiornamento di quell'attributo di {string} rimuovendo la data di scadenza")
    public void updateAttributeWithoutPassingExpirationDate(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().updateVerifiedAttribute(
                        tenantId, sharedStepsContext.getAttributeCommonContext().getAttributeId(), new UpdateVerifiedTenantAttributeSeed())
        );
    }

    @When("l'utente richiede l'aggiornamento di quell'attributo di {string} con una data di scadenza nel passato")
    public void updateAttributeWithPastExpirationDate(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        LocalDate date = LocalDate.now().minusDays(7);
        UUID tenantId = identityService.getOrganizationId(tenantType);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().updateVerifiedAttribute(
                        tenantId, sharedStepsContext.getAttributeCommonContext().getAttributeId(), new UpdateVerifiedTenantAttributeSeed().expirationDate(date.toString()))
        );

    }
}
