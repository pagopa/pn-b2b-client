package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;
import java.util.UUID;

public class PurposeListingProducerSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;

    public PurposeListingProducerSteps(ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente erogatore richiede una operazione di listing delle finalità limitata alle prime {int} finalità")
    public void requireListingOperationWithLimit(int limit) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getProducerPurposes(
                        0, limit, null, null, null, null)
        );
    }

    @When("l'utente erogatore richiede una operazione di listing delle finalità con offset {int}")
    public void requireListingOperationWithOffset(int offset) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getProducerPurposes(
                        offset, 50, null, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null, null)
        );

    }

    @When("l'utente erogatore richiede una operazione di listing delle finalità sui propri e-service")
    public void requireListingOperationPurposeOwned() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getProducerPurposes(
                        0, 50, null, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null, null)
        );

    }

    @When("l'utente erogatore richiede una operazione di listing delle finalità filtrata per fruitore {string}")
    public void requireFilteredListingOperationByConsumer(String consumer) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID consumerId = identityService.getOrganizationId(consumer);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getProducerPurposes(
                        0, 50, null, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), List.of(consumerId), null)
        );
    }

    @When("l'utente erogatore richiede una operazione di listing delle finalità filtrata per il secondo e-service")
    public void requireFilteredListingOperation() {
        requireListingOperationPurposeOwned();
    }

    @When("l'utente erogatore richiede una operazione di listing delle finalità in stato {string}")
    public void requireListingOperationForPurposeWithState(String state) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getProducerPurposes(
                        0, 50, null, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null,
                        List.of(PurposeVersionState.fromValue(state)))
        );
    }

    @When("l'utente erogatore richiede una operazione di listing delle finalità filtrando per la keyword {string}")
    public void requireListingOperationByKeyword(String keyword) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getProducerPurposes(
                        0, 50, keyword, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null, null)
        );
    }
}
