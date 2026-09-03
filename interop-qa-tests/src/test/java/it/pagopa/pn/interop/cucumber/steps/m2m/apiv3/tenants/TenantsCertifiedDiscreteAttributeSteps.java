package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.tenants;

import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MV3TenantClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TenantsCertifiedDiscreteAttributeSteps {

    private final IM2MV3TenantClient tenantClient;
    private final IdentityService identityService;
    private final AttributeCommonContext attributeCommonContext;
    private final SharedStepsContext sharedStepsContext;

    public TenantsCertifiedDiscreteAttributeSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.tenantClient = clientTokenConfigurator.getM2mV3TenantClient();
        this.identityService = sharedStepsContext.getIdentityService();
        this.attributeCommonContext = sharedStepsContext.getAttributeCommonContext();
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente assegna a {string} l'attributo certificato discreto creato con un valore discreto di {int}")
    public void assignTenantCertifiedDiscreteAttribute(String tenantType, Integer discreteValue) {
        UUID tenantId = identityService.getOrganizationId(tenantType);

        CertifiedDiscreteAttribute lastCreated = getLastCreatedCertifiedDiscreteAttribute();

        TenantCertifiedDiscreteAttributeSeed seed = new TenantCertifiedDiscreteAttributeSeed();
        assert lastCreated != null;
        seed.setId(lastCreated.getId());
        seed.setCertifiedDiscreteValue(discreteValue);

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.assignTenantCertifiedDiscreteAttribute(tenantId, seed)
        );
    }

    @When("l'utente assegna a {string} l'attributo certificato discreto creato con un valore discreto di {int}, utilizzando per l'ente un UUID {entityIdType}")
    public void assignTenantCertifiedDiscreteAttributeWithInvalidUuid(String tenantType, Integer discreteValue, EntityIdType entityIdType) {
        CertifiedDiscreteAttribute lastCreated = getLastCreatedCertifiedDiscreteAttribute();

        TenantCertifiedDiscreteAttributeSeed seed = new TenantCertifiedDiscreteAttributeSeed();
        assert lastCreated != null;
        seed.setId(lastCreated.getId());
        seed.setCertifiedDiscreteValue(discreteValue);

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.assignTenantCertifiedDiscreteAttribute(getEntityId(entityIdType), seed)
        );
    }

    @When("l'utente assegna a {string} l'attributo certificato discreto creato con un valore discreto di {int} con successo")
    public void assignTenantCertifiedDiscreteAttributeSuccessfully(String tenantType, Integer discreteValue) {
        this.assignTenantCertifiedDiscreteAttribute(tenantType, discreteValue);
        assert sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful();

        UUID tenantId = identityService.getOrganizationId(tenantType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        sharedStepsContext.getPollingService().makePolling(
                () -> {
                    assert lastCreated != null;
                    return findTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId());
                },
                Objects::nonNull,
                "Tenant certified discrete attribute not found"
        );
    }

    @When("l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a {string}, impostando il valore discreto a {int}")
    public void modifyTenantCertifiedDiscreteAttribute(String tenantType, Integer discreteValue) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        var seed = new UpdateTenantCertifiedDiscreteAttributeSeed();
        seed.setCertifiedDiscreteValue(discreteValue);
        assert lastCreated != null;
        this.sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.replaceTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId(), seed)
        );
    }

    @When("l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a {string}, impostando il valore discreto a {int}, utilizzando per l'ente un UUID {entityIdType}")
    public void modifyTenantCertifiedDiscreteAttributeWithInvalidUuid(String tenantType, Integer discreteValue, EntityIdType entityIdType) {
        UUID tenantId = getEntityId(entityIdType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        var seed = new UpdateTenantCertifiedDiscreteAttributeSeed();
        seed.setCertifiedDiscreteValue(discreteValue);
        assert lastCreated != null;
        this.sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.replaceTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId(), seed)
        );
    }

    @When("l'utente tenta la modifica dell'attributo certificato discreto precedentemente associato a {string}, impostando il valore discreto a {int}, utilizzando un UUID {entityIdType}")
    public void modifyTenantCertifiedDiscreteAttributeWithInvalidAttributeUuid(String tenantType, Integer discreteValue, EntityIdType entityIdType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        var seed = new UpdateTenantCertifiedDiscreteAttributeSeed();
        seed.setCertifiedDiscreteValue(discreteValue);
        this.sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.replaceTenantCertifiedDiscreteAttribute(tenantId, getEntityId(entityIdType), seed)
        );
    }

    @When("l'utente modifica a {string} l'attributo certificato discreto precedentemente associato, impostando il valore discreto a {int} con successo")
    public void modifyTenantCertifiedDiscreteAttributeSuccessfully(String tenantType, Integer discreteValue) {
        modifyTenantCertifiedDiscreteAttribute(tenantType, discreteValue);
        assert sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful();

        UUID tenantId = identityService.getOrganizationId(tenantType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        TenantCertifiedDiscreteAttribute actual = sharedStepsContext.getPollingService().makePolling(
                () -> {
                    assert lastCreated != null;
                    return findTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId());
                },
                res -> res.getDiscreteValue().equals(discreteValue),
                "Tenant certified discrete attribute not updated"
        );

        assert actual.getDiscreteValue().equals(discreteValue);
    }

    @When("l'utente tenta di revocare a {string} l'attributo certificato discreto precedentemente associato")
    public void revokeTenantCertifiedDiscreteAttribute(String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        assert lastCreated != null;
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.revokeTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId())
        );
    }

    @When("l'utente revoca a {string} l'attributo certificato discreto precedentemente associato con successo")
    public void revokeTenantCertifiedDiscreteAttributeSuccessfully(String tenantType) {
        this.revokeTenantCertifiedDiscreteAttribute(tenantType);
        assert sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful();

        UUID tenantId = identityService.getOrganizationId(tenantType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        sharedStepsContext.getPollingService().makePolling(
                () -> {
                    assert lastCreated != null;
                    return findTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId());
                },
                res -> {
                    assert res.getRevokedAt() != null;
                    return ! res.getRevokedAt().isEmpty();
                },
                "Tenant certified discrete attribute not revoked"
        );
    }

    @When("l'utente tenta di revocare a {string} l'attributo certificato discreto precedentemente associato, utilizzando per l'ente un UUID {entityIdType}")
    public void revokeTenantCertifiedDiscreteAttributeWithInvalidTenantUuid(String tenantType, EntityIdType entityIdType) {
        UUID tenantId = getEntityId(entityIdType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        assert lastCreated != null;
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.revokeTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId())
        );
    }

    @When("l'utente tenta di revocare a {string} l'attributo certificato discreto precedentemente associato, utilizzando per l'attributo un UUID {entityIdType}")
    public void revokeTenantCertifiedDiscreteAttributeWithInvalidAttributeUuid(String tenantType, EntityIdType entityIdType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.revokeTenantCertifiedDiscreteAttribute(tenantId, getEntityId(entityIdType))
        );
    }

    @When("l'utente richiede l'elenco degli attributi certificati discreti di {string} e l'ultimo creato è associato con il valore discreto di {int}")
    public void getTenantCertifiedDiscreteAttributes(String tenantType, Integer discreteValue) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        CertifiedDiscreteAttribute lastCreated = this.getLastCreatedCertifiedDiscreteAttribute();
        Assertions.assertNotNull(lastCreated, "Nessun attributo certificato discreto creato");

        TenantCertifiedDiscreteAttribute foundAttribute = findTenantCertifiedDiscreteAttribute(tenantId, lastCreated.getId());

        Assertions.assertNotNull(foundAttribute,
                "L'ultimo attributo certificato discreto creato non è presente nella risposta paginata");
        Assertions.assertEquals(discreteValue, foundAttribute.getDiscreteValue());
    }

    @When("l'utente richiede l'elenco degli attributi certificati discreti di {string} utilizzando un UUID {entityIdType}")
    public void getTenantCertifiedDiscreteAttributesWithInvalidUuid(String tenantType, EntityIdType entityIdType) {
        sharedStepsContext.getHttpCallExecutor().performCall(
            () ->this.tenantClient.getTenantCertifiedDiscreteAttributes(getEntityId(entityIdType), 0, 50)
        );
    }

    /**
     * Recupera una specifica pagina della lista degli attributi certificati discreti
     * associati a un tenant.
     *
     * @param pageIndex numero della pagina da recuperare; è da intendersi come pagina,
     *                 non come offset, e parte da 1
     * @param pageSize numero massimo di elementi da includere nella pagina richiesta
     * @param tenantType tipo di tenant di cui recuperare la lista degli attributi
     */
    @When("l'utente tenta di recuperare la pagina {int} con un limite di {int} elementi della lista di attributi certificati discreti associati a {string}")
    public void getTenantCertifiedDiscreteAttributePage(int pageIndex, int pageSize, String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        int offset = (pageIndex - 1) * pageSize;
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.tenantClient.getTenantCertifiedDiscreteAttributes(tenantId, offset, pageSize)
        );
    }

    private UUID getEntityId(EntityIdType entityIdType) {
        return switch (entityIdType) {
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            case NON_EXISTENT_ID -> UUID.randomUUID();
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }

    private CertifiedDiscreteAttribute getLastCreatedCertifiedDiscreteAttribute() {
        List<CertifiedDiscreteAttribute> certifiedDiscretePublished = this.attributeCommonContext.getCertifiedDiscretePublished();
        return certifiedDiscretePublished.isEmpty() ? null : certifiedDiscretePublished.get(certifiedDiscretePublished.size() - 1);
    }

    private TenantCertifiedDiscreteAttribute findTenantCertifiedDiscreteAttribute(UUID tenantId, UUID attributeId) {

        int offset = 0;
        int limit = 50;
        TenantCertifiedDiscreteAttribute foundAttribute = null;

        while (foundAttribute == null) {
            TenantCertifiedDiscreteAttributes attributes = this.tenantClient.getTenantCertifiedDiscreteAttributes(tenantId, offset, limit);
            List<TenantCertifiedDiscreteAttribute> pageResults = attributes.getResults();

            foundAttribute = pageResults.stream()
                    .filter(attribute -> attribute.getId().equals(attributeId))
                    .findFirst()
                    .orElse(null);

            if (foundAttribute == null && pageResults.isEmpty()) {
                break;
            }
            offset += pageResults.size();
        }

        return foundAttribute;
    }
}
