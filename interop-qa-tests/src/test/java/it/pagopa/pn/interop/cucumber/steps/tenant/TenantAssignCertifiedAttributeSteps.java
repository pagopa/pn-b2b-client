package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Slf4j
public class TenantAssignCertifiedAttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public TenantAssignCertifiedAttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = this.sharedStepsContext.getIdentityService();
    }

    @When("l'utente assegna a {string} l'attributo certificato precedentemente creato")
    public void assignCertifiedAttribute(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID lastAttributeId = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).get(
                sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).size() - 1
        );

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().addCertifiedAttribute(
                                tenantId,
                                new CertifiedTenantAttributeSeed().id(lastAttributeId)
                        )
                ),
                HttpStatus::is2xxSuccessful,
                "There was an error while assigning the certified attribute"
        );

        if(sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()){
            sharedStepsContext.getPollingService().makePolling(
                    () -> clientTokenConfigurator.getTenantsApi().getCertifiedAttributes(tenantId),
                    res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(lastAttributeId)),
                    "There was an error while retrieving the attributes"
            );
        }
    }

    @When("l'utente assegna a {string} gli attributi certificati precedentemente creati")
    public void assignCertifiedAttributes(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().forEach(attributeIDs -> {
            attributeIDs.forEach(attributeId -> {

                sharedStepsContext.getPollingService().makePolling(
                        () -> sharedStepsContext.getHttpCallExecutor().performCall(
                                () -> clientTokenConfigurator.getTenantsApi().addCertifiedAttribute(
                                        tenantId,
                                        new CertifiedTenantAttributeSeed().id(attributeId)
                                )
                        ),
                        HttpStatus::is2xxSuccessful,
                        "There was an error assigning the certified attribute"
                );
            });
        });
    }

    @When("l'utente tenta di assegnare a {string} l'attributo certificato discreto precedentemente creato con un valore discreto di {int}")
    public void tryToAssignCertifiedDiscreteAttribute(String tenantType, Integer discreteValue) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID lastAttributeId = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).get(
                sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).size() - 1
        );

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getAttributeApiClient().getAttributeById(lastAttributeId)),
                res -> {
                    return res.is2xxSuccessful() || !sharedStepsContext.getHttpCallExecutor().ongoingOperationConflict();
                },
                "Impossibile recuperare l'attributo");

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().addCertifiedDiscreteAttribute(
                                tenantId,
                                new CertifiedDiscreteTenantAttributeSeed()
                                        .id(lastAttributeId)
                                        .certifiedDiscreteValue(discreteValue)
                        )
                ),
                res -> {
                    return res.is2xxSuccessful() || !sharedStepsContext.getHttpCallExecutor().ongoingOperationConflict();
                },
                "Error while assigning certified discrete attribute"
        );

    }

    @When("l'utente assegna a {string} l'attributo certificato discreto precedentemente creato con un valore discreto di {int}")
    public void assignCertifiedDiscreteAttribute(String tenantType, Integer discreteValue) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID lastAttributeId = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).get(
                sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).size() - 1
        );

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getAttributeApiClient().getAttributeById(lastAttributeId)),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
                "Impossibile recuperare l'attributo");

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().addCertifiedDiscreteAttribute(
                                tenantId,
                                new CertifiedDiscreteTenantAttributeSeed()
                                        .id(lastAttributeId)
                                        .certifiedDiscreteValue(discreteValue)
                        )
                ),
                res -> res.is2xxSuccessful() || !sharedStepsContext.getHttpCallExecutor().ongoingOperationConflict(),
                "There was an error while retrieving the attributes"
        );

        if(sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()){
            sharedStepsContext.getPollingService().makePolling(
                    () -> sharedStepsContext.getHttpCallExecutor().performCall(
                            () -> clientTokenConfigurator.getTenantsApi().getCertifiedAttributes(tenantId)
                    ),
                    res -> res.is2xxSuccessful()
                            && ((CertifiedAttributesResponse) sharedStepsContext.getHttpCallExecutor().getResponse())
                                .getAttributes().stream()
                                .anyMatch(attr -> attr.getId().equals(lastAttributeId)),
                    "There was an error while retrieving the attributes"
            );

            CertifiedAttributesResponse attrs = (CertifiedAttributesResponse) sharedStepsContext.getHttpCallExecutor().getResponse();
            CertifiedDiscreteTenantAttribute discrCertAttr = attrs.getAttributes().stream()
                    .filter(attr2 -> attr2.getId().equals(lastAttributeId))
                    .findFirst()
                    .map(CertifiedDiscreteTenantAttribute.class::cast)
                    .orElse(null);
            Assertions.assertNotNull(discrCertAttr);
            Assertions.assertEquals(discreteValue, discrCertAttr.getDiscreteValue());
        }
    }

    @When("l'utente tenta la modifica dell'attributo certificato discreto precedentemente creato con un valore discreto di {int} a {string}")
    public void updateCertifiedAttribute(Integer discreteValue, String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID attributeId = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).get(
                sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).size() - 1
        );
        UpdateCertifiedDiscreteTenantAttributeSeed seed = new UpdateCertifiedDiscreteTenantAttributeSeed();
        seed.setCertifiedDiscreteValue(discreteValue);

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().updateCertifiedDiscreteAttribute(tenantId, attributeId, seed)
                ),
                res -> res.is2xxSuccessful() || !sharedStepsContext.getHttpCallExecutor().ongoingOperationConflict(),
                "There was an error updating the certified discrete attribute"
        );
    }

    @When("l'utente tenta di modificare l'attributo certificato discreto di {string} utilizzando un ID inesistente per l'attributo")
    public void updateTenantCertifiedAttributeWithInvalidAttributeId(String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID attributeId = UUID.randomUUID();

        var seed = new UpdateCertifiedDiscreteTenantAttributeSeed();
        seed.setCertifiedDiscreteValue(100);

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().updateCertifiedDiscreteAttribute(tenantId, attributeId, seed)
                ),
                HttpStatus::is4xxClientError,
                "There was an error while updating the attribute"
        );
    }

    @When("l'utente tenta di modificare l'attributo certificato discreto di {string} utilizzando un ID inesistente per il tenant")
    public void updateTenantCertifiedAttributeWithInvalidTenantId(String tenantType) {
        UUID tenantId = UUID.randomUUID();
        UUID attributeId = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).get(
                sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).size() - 1
        );

        var seed = new UpdateCertifiedDiscreteTenantAttributeSeed();
        seed.setCertifiedDiscreteValue(100);

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().updateCertifiedDiscreteAttribute(tenantId, attributeId, seed)
                ),
                HttpStatus::is4xxClientError,
                "There was an error while updating the attribute"
        );
    }

    @When("l'utente tenta di modificare l'attributo certificato discreto di {string} utilizzando l'ID dell'attributo certificato creato")
    public void updateTenantCertifiedAttributeWithInvalidAttributeKind(String tenantType) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        var certifiedAttribute = sharedStepsContext.getAttributeCommonContext().getCreatedAttributes().get(
                sharedStepsContext.getAttributeCommonContext().getCreatedAttributes().size() - 1
        );
        Assertions.assertEquals(AttributeKind.CERTIFIED, certifiedAttribute.getKind());

        UUID attributeId = certifiedAttribute.getId();

        var seed = new UpdateCertifiedDiscreteTenantAttributeSeed();
        seed.setCertifiedDiscreteValue(100);

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().updateCertifiedDiscreteAttribute(tenantId, attributeId, seed)
                ),
                HttpStatus::is4xxClientError,
                "There was an error while updating the attribute"
        );
    }

    @When("l'attributo certificato discreto è assegnato a {string} e ha il valore discreto di {int}")
    public void checkTenantCertifiedAttribute(String tenantType, Integer discreteValue) {
        UUID tenantId = identityService.getOrganizationId(tenantType);
        UUID attributeId = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).get(
                sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().get(0).size() - 1
        );

        sharedStepsContext.getPollingService().makePolling(
            () -> sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getCertifiedAttributes(tenantId)
            ),
            res -> res.is2xxSuccessful()
                && ((CertifiedAttributesResponse) sharedStepsContext.getHttpCallExecutor().getResponse())
                    .getAttributes().stream()
                                .anyMatch(attr -> attr.getId().equals(attributeId)
                                        && attr instanceof CertifiedDiscreteTenantAttribute
                                        && discreteValue.equals(((CertifiedDiscreteTenantAttribute) attr).getDiscreteValue())),
                "There was an error while retrieving the attributes"
        );

        CertifiedAttributesResponse attrs = (CertifiedAttributesResponse) sharedStepsContext.getHttpCallExecutor().getResponse();

        CertifiedDiscreteTenantAttribute discrCertAttr = attrs.getAttributes().stream()
            .filter(attr2 -> attr2.getId().equals(attributeId))
            .findFirst()
            .map(CertifiedDiscreteTenantAttribute.class::cast)
            .orElse(null);
        Assertions.assertNotNull(discrCertAttr);
        Assertions.assertEquals(discreteValue, discrCertAttr.getDiscreteValue());
    }
}
