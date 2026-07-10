package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;

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
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().addCertifiedAttribute(
                        tenantId,
                        new CertifiedTenantAttributeSeed().id(lastAttributeId)
                )
        );

        if(sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()){
            sharedStepsContext.getPollingService().makePolling(
                    () -> clientTokenConfigurator.getTenantsApi().getCertifiedAttributes(tenantId),
                    res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(lastAttributeId)),
                    "There was an error while retrieving the attributes"
            );
        }
        sharedStepsContext.getAttributeCommonContext().setCertifierName(
                identityService.getTenantName(
                        sharedStepsContext.getTenantType()
                )
        );
    }

    @When("l'utente assegna a {string} gli attributi certificati precedentemente creati")
    public void assignCertifiedAttributes(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes().forEach(attributeIDs -> {
            attributeIDs.forEach(attributeId -> {
                sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> clientTokenConfigurator.getTenantsApi().addCertifiedAttribute(
                                tenantId,
                                new CertifiedTenantAttributeSeed().id(attributeId)
                        )
                );
            });
        });
    }
}
