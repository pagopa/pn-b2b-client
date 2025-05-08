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

public class TenantEServiceProducerListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;
    private int offset;

    public TenantEServiceProducerListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                              SharedStepsContext sharedStepsContext,
                                              CommonUtils commonUtils) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede una operazione di listing degli erogatori")
    public void requireProducerListingOperation() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getProducers(0, 20, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli erogatori con limit {int}")
    public void requireListingOperationWithLimit(int limit) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getProducers(0, limit, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli erogatori con offset {int}")
    public void requireListingOperationWithOffset(int offset) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getProducers(offset, 20, null)
        );
        this.offset = offset;
    }

    @When("l'utente richiede una operazione di listing degli erogatori filtrando per nome aderente {string}")
    public void requireConsumerOperationListingByKeyword(String producerName) {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getProducers(0, 20, producerName)
        );
    }

    @Then("si ottiene status code 200 e il giusto numero di erogatori in base all'offset richiesto")
    public void verifyStatusCodeAndConsumerNumberBasedOnOffset() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getProducers(0, 20, null)
        );
        commonUtils.assertValidResponse();
        CompactOrganizations compactOrganizations = (CompactOrganizations) sharedStepsContext.getHttpCallExecutor().getResponse();

        int totalCount = compactOrganizations.getPagination().getTotalCount();
        Assertions.assertEquals(20, sharedStepsContext.getHttpCallExecutor().getClientResponse().value());
        Assertions.assertEquals(totalCount - offset, compactOrganizations.getResults().size());
    }
}
