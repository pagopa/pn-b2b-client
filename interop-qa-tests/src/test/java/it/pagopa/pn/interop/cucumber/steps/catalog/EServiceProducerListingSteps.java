package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class EServiceProducerListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;

    public EServiceProducerListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede una operazione di listing sui propri e-services erogati")
    public void requireOwnEServiceList() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, 50,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services erogati limitata ai primi {int} e-services")
    public void requireOwnEServiceListWithLimit(int limit) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, limit,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services con offset {int}")
    public void requireOwnEServiceListWithOffset(int offset) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(offset, 12,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services fruiti da {string}")
    public void requireOwnEServiceListForConsumer(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID consumerId = identityService.getOrganizationId(tenantType);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, 12,
                        String.valueOf(sharedStepsContext.getTestSeed()), List.of(consumerId), null)
        );
    }

    @When("l'utente richiede una operazione di listing sui propri e-services filtrando per la keyword {string}")
    public void requireOwnEServiceListWithKeyword(String keyword) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServices(0, 12,
                        String.format("%s-%s", sharedStepsContext.getTestSeed(), keyword), List.of(), null)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} e-service(s) come erogatore")
    public void verifyReceivedResponse(int statusCode, int eServiceNumber) {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(HttpStatus.valueOf(statusCode), httpCallExecutor.getResponseStatus());
        Assertions.assertEquals(eServiceNumber,
                ((ProducerEServices) httpCallExecutor.getResponse()).getResults().size());

    }





}
