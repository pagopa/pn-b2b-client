package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

public class TenantEServiceConsumerListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final CommonUtils commonUtils;
    private int offset;

    public TenantEServiceConsumerListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                              SharedStepsContext sharedStepsContext,
                                              IdentityService identityService,
                                              CommonUtils commonUtils) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = identityService;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede una operazione di listing dei fruitori")
    public void requireConsumerListingOperation() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getConsumers(0, 20, null)
        );
    }

    @Then("si ottiene status code 200 e la lista di aderenti contenente {string}")
    public void verifyStatusCodeAndConsumerList(String tenantType) {
        CompactOrganizations compactOrganizations = (CompactOrganizations) sharedStepsContext.getHttpCallExecutor().getResponse();
        UUID organizationId = identityService.getOrganizationId(tenantType);
        Assertions.assertEquals(200, sharedStepsContext.getHttpCallExecutor().getClientResponse().value());
        Assertions.assertTrue(compactOrganizations.getResults().stream()
                .anyMatch(consumer -> consumer.getId().equals(organizationId)),
                String.format("%s is not present in the consumer list!", tenantType.toUpperCase()));
    }

    @When("l'utente richiede una operazione di listing dei fruitori con limit {int}")
    public void requireListingOperationWithLimit(int limit) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getConsumers(0, limit, null)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} aderent(i)(e)")
    public void verifyStatusCodeAndConsumerListSize(int statusCode, int tenantNum) {
        CompactOrganizations compactOrganizations = (CompactOrganizations) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(statusCode, sharedStepsContext.getHttpCallExecutor().getClientResponse().value());
        Assertions.assertEquals(tenantNum, compactOrganizations.getResults().size());
    }

    @When("l'utente richiede una operazione di listing dei fruitori con offset {int}")
    public void requireListingOperationWithOffset(int offset) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getConsumers(offset, 20, null)
        );
        this.offset = offset;
    }

    @Then("si ottiene status code 200 e il giusto numero di fruitori in base all'offset richiesto")
    public void verifyStatusCodeAndConsumerNumberBasedOnOffset() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getConsumers(0, 20, null)
        );
        commonUtils.assertValidResponse();
        CompactOrganizations compactOrganizations = (CompactOrganizations) sharedStepsContext.getHttpCallExecutor().getResponse();

        int totalCount = compactOrganizations.getPagination().getTotalCount();
        Assertions.assertEquals(20, sharedStepsContext.getHttpCallExecutor().getClientResponse().value());
        Assertions.assertEquals(totalCount - offset, compactOrganizations.getResults().size());
    }

    @When("l'utente richiede una operazione di listing dei fruitori filtrando per nome aderente {string}")
    public void requireConsumerOperationListingByKeyword(String consumerName) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getConsumers(0, 20, consumerName)
        );
    }
}
