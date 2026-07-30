package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.TenantType;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TenantRevokeCertifiedAttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;

    public TenantRevokeCertifiedAttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente revoca l'attributo precedentemente creato e assegnato")
    public void revokeAttributePreviouslyCreated() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeCertifiedAttribute(
                        identityService.getOrganizationId(sharedStepsContext.getTenantType()),
                        sharedStepsContext.getAttributeCommonContext().getAttributeId())
        );
    }

    @When("l'utente revoca l'attributo certificato {int}-esimo nel gruppo {int}-esimo precedentemente creato e assegnato a {string}")
    public void revokeCertifiedAttribute(int attributeIndex, int groupIndex, String consumerTenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeCertifiedAttribute(
                        identityService.getOrganizationId(consumerTenant),
                        sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(groupIndex).get(attributeIndex))
        );
    }

    @When("{tenantType} ha creato un attributo certificato discreto")
    public void tenantHasCreatedCertifiedAttribute(TenantType tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType.name(), null));

        AttributeSeed seed = new AttributeSeed()
            .name("new certified discrete attribute %d".formatted(RandomUtils.insecure().randomInt()))
            .description("description test");
             httpCallExecutor.performCall(
                 () -> clientTokenConfigurator.getAttributeApiClient().createCertifiedDiscreteAttribute(seed)
             );

        Assertions.assertTrue(httpCallExecutor.getResponseStatus().is2xxSuccessful());
        Attribute attribute = ((Attribute) httpCallExecutor.getResponse());
        sharedStepsContext.getPollingService().makePolling(
            () -> httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getAttributeApiClient().getAttributeById(attribute.getId())),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
            "L'attributo non è stato creato correttamente!"
        );
    }

    @When("l'utente revoca a {string} l'attributo certificato discreto precedentemente creato e assegnato")
    public void revokeCertifiedDiscreteAttribute(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID lastAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();

        ensureAttributeHasBeenAssignedToTenant(tenantId, lastAttributeId);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeCertifiedDiscreteAttribute(tenantId, lastAttributeId)
        );

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            ensureAttributeHasBeenRevokedToTenant(tenantId, lastAttributeId);
        }
    }

    @When("l'utente revoca a {string} l'attributo precedentemente creato ma non associato")
    public void revokeAttributePreviouslyCreatedButNotAssociated(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID lastAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();
        this.ensureAttributeHasBeenCreated(sharedStepsContext.getAttributeCommonContext().getAttributeId());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeCertifiedAttribute(
                        identityService.getOrganizationId(tenantType),
                        lastAttributeId)
        );
    }

    @When("l'utente revoca l'attributo precedentemente creato ad un ente non esistente")
    public void revokeAttributePreviouslyCreatedToNonExistingEntity() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID lastAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();
        ensureAttributeHasBeenCreated(lastAttributeId);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().revokeCertifiedAttribute(
                        UUID.randomUUID(),
                        lastAttributeId)
        );
    }

    private void ensureAttributeHasBeenAssignedToTenant(UUID tenantId, UUID lastAttributeId) {
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getTenantsApi().getTenant(tenantId)),
                res -> res.is2xxSuccessful() && ((Tenant) httpCallExecutor.getResponse()).getAttributes().getCertified().stream()
                            .anyMatch(a -> a.getId().equals(lastAttributeId)),
                "L'attributo non è stato associato al tenant");
    }

    private void ensureAttributeHasBeenRevokedToTenant(UUID tenantId, UUID lastAttributeId) {
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getTenantsApi().getTenant(tenantId)),
                        res -> res.is2xxSuccessful() && ((Tenant) httpCallExecutor.getResponse()).getAttributes().getCertified().stream()
                            .anyMatch(a -> a.getId().equals(lastAttributeId) && a.getRevocationTimestamp() != null && !a.getRevocationTimestamp().isEmpty()),
                "L'attributo non è stato revocato al tenant");
    }

    private void ensureAttributeHasBeenCreated(UUID lastAttributeId) {
        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getAttributeApiClient().getAttributeById(lastAttributeId)),
                HttpStatus::is2xxSuccessful,
                "Impossibile recuperare l'attributo");
    }
}
