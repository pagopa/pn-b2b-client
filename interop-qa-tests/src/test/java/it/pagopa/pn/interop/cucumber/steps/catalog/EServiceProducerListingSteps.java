package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;
import java.util.UUID;

public class EServiceProducerListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;

    public EServiceProducerListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede una operazione di listing sui propri e-services erogati")
    public void requireOwnEServiceList() {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, 50,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services erogati limitata ai primi {int} e-services")
    public void requireOwnEServiceListWithLimit(int limit) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, limit,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services con offset {int}")
    public void requireOwnEServiceListWithOffset(int offset) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(offset, 12,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services fruiti da {string}")
    public void requireOwnEServiceListForConsumer(String tenantType) {
        UUID consumerId = identityService.getOrganizationId(tenantType);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, 12,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(consumerId), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services filtrando per la keyword {string}")
    public void requireOwnEServiceListWithKeyword(String keyword) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, 12,
                        String.format("%s-%s", sharedStepsContext.getTestSeed(), keyword), List.of(), null)
        );
    }





}
