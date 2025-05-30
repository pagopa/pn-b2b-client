package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.RequesterCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.RequesterCertifiedAttributes;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TenantRequesterCertifiedAttributesSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    private final List<RequesterCertifiedAttribute> results = new ArrayList<>();

    public TenantRequesterCertifiedAttributesSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                   SharedStepsContext sharedStepsContext,
                                                   CommonUtils commonUtils) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede una operazione di listing degli attributi certificati assegnati")
    public void listCertiedAttributes() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getRequesterCertifiedAttributes(0, 50)
        );
        if (httpCallExecutor.getClientResponse().is2xxSuccessful()) {
            int size = 50;
            AtomicInteger offset = new AtomicInteger(0);
            while (size == 50) {
                httpCallExecutor.performCall(() -> clientTokenConfigurator.getTenantsApi().getRequesterCertifiedAttributes(offset.get(), 50));
                commonUtils.assertValidResponse();
                RequesterCertifiedAttributes requesterCertifiedAttributes = ((RequesterCertifiedAttributes) httpCallExecutor.getResponse());
                results.addAll(requesterCertifiedAttributes.getResults());
                size = requesterCertifiedAttributes.getResults().size();
                offset.set(offset.get() + size);
            }
        }
    }

    @Then("si ottiene status code 200 e la lista degli attributi contenente l'attributo assegnato a {string}")
    public void verifyStatusCodeAndAttributeList(String tenantType) {
        Assertions.assertEquals(200, httpCallExecutor.getClientResponse().value());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        Assertions.assertTrue(((RequesterCertifiedAttributes) httpCallExecutor.getResponse()).getResults()
                        .stream().anyMatch(attr -> attr.getAttributeId().equals(sharedStepsContext.getAttributeCommonContext().getAttributeId())
                        && attr.getTenantId().equals(tenantId)),
                "L'attributo assegnato non è presente nella lista degli attributi certificati");
    }

    @When("l'utente richiede una operazione di listing senza paginazione degli attributi certificati assegnati")
    public void requireCertifiedAttributeListingOperation() {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getRequesterCertifiedAttributes(0, 50)
        );
    }

}
