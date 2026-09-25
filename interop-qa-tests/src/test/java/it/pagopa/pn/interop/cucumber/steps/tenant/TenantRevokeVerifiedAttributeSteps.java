package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;

public class TenantRevokeVerifiedAttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    private UUID otherAgreementId;

    public TenantRevokeVerifiedAttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                              SharedStepsContext sharedStepsContext,
                                              BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente revoca l'attributo precedentemente verificato")
    public void revokeAttributePreviouslyCreated() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        revokeAttribute();
    }

    @When("{string} revoca l'attributo precedentemente verificato")
    public void revokeAttributePreviouslyCreated(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        revokeAttribute();
    }

    private void revokeAttribute() {
        String consumerTenant = sharedStepsContext.getAttributeCommonContext().getAttributeConsumerTenant();
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getTenantsApi().revokeVerifiedAttribute(
                identityService.getOrganizationId(consumerTenant),
                sharedStepsContext.getAttributeCommonContext().getAttributeId(),
                sharedStepsContext.getAgreementCommonContext().getAgreementId())
        );
    }

    @Then("l'attributo di {string} rimane verificato da {string}")
    public void attributeRemainVerified(String tenantType, String tenantTypeVerifier) {
        UUID consumer = identityService.getOrganizationId(tenantType);
        UUID verifier = identityService.getOrganizationId(tenantTypeVerifier);

        AtomicReference<Optional<VerifiedTenantAttribute>> attribute = new AtomicReference<>();

        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getTenantsApi().getVerifiedAttributes(consumer),
                res -> {
                    attribute.set(res.getAttributes().stream()
                            .filter(attr -> attr.getId().equals(sharedStepsContext.getAttributeCommonContext().getAttributeId()))
                            .findFirst());
                    return attribute.get().map(VerifiedTenantAttribute::getRevokedBy)
                            .map(List::size).orElse(0) != 0;
                },
                "There was an error while retrieving the verified attributes by " + tenantTypeVerifier
        );

        Assertions.assertTrue(attribute.get().map(VerifiedTenantAttribute::getVerifiedBy).orElse(List.of())
                .stream().anyMatch(tenantVerifier -> tenantVerifier.getId().equals(verifier)),
                String.format("L'attributo non è verificato da %s", tenantTypeVerifier)
        );
    }

    @Then("l'attributo di {string} risulta revocato da {string}")
    public void verifyAttributeIsRevokedBy(String tenantType, String tenantTypeRevoker) {
        UUID consumer = identityService.getOrganizationId(tenantType);
        UUID revoker = identityService.getOrganizationId(tenantTypeRevoker);
        AtomicReference<Optional<VerifiedTenantAttribute>> attribute = new AtomicReference<>();

        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getTenantsApi().getVerifiedAttributes(consumer),
                res -> {
                    attribute.set(res.getAttributes().stream()
                            .filter(attr -> attr.getId().equals(sharedStepsContext.getAttributeCommonContext().getAttributeId()))
                            .findFirst());
                    return attribute.get().map(VerifiedTenantAttribute::getRevokedBy)
                            .map(List::size).orElse(0) != 0;
                },
                "There was an error while retrieving the verified attributes by " + tenantTypeRevoker
        );

        Assertions.assertTrue(attribute.get().map(VerifiedTenantAttribute::getRevokedBy).orElse(List.of())
                        .stream().anyMatch(tenantVerifier -> tenantVerifier.getId().equals(revoker)),
                String.format("L'attributo non è verificato da %s", tenantTypeRevoker)
        );
    }

    @Given("{string} ha un'altra richiesta di fruizione in stato {string} per quell'e-service")
    public void tenantHasAlreadyAnAgreementWithState(String consumer, String agreementState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(consumer, null));
        otherAgreementId = dataPreparationService.createAgreementWithGivenState(
                AgreementState.fromValue(agreementState),
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                null
        );
    }

    @Given("{string} ha già verificato l'attributo verificato a {string} sull'altra richiesta di fruizione")
    public void tenantHasAlreadyVerifiedTheVerifiedAttribute(String verifier, String consumer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(verifier, null));
        UUID consumerId = identityService.getOrganizationId(consumer);
        UUID verifierId = identityService.getOrganizationId(verifier);

        dataPreparationService.assignVerifiedAttributeToTenant(
                consumerId,
                verifierId,
                sharedStepsContext.getAttributeCommonContext().getAttributeId(),
                otherAgreementId,
                null
        );
    }
}
