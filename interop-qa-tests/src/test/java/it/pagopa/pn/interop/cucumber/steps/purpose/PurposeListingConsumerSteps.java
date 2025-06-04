package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;

public class PurposeListingConsumerSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public PurposeListingConsumerSteps(ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente fruitore richiede una operazione di listing delle finalità limitata ai primi {int} risultati")
    public void consumerRequireListingOperationWithLimit(int limit) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getConsumerPurposes(
                        0, limit, null, null, null, null
                )
        );
    }

    @When("l'utente fruitore richiede una operazione di listing delle finalità con offset {int}")
    public void consumerRequireListingOperationWithOffset(int offset) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getConsumerPurposes(
                        offset, 50, null, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null, null
                )
        );

    }

    @When("l'utente fruitore richiede una operazione di listing delle sue finalità sugli e-services a cui è sottoscritto")
    public void consumerRequireSubscribedPurpose() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getConsumerPurposes(
                        0, 50, null, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null, null
                )
        );
    }

    @When("l'utente fruitore richiede una operazione di listing delle finalità filtrata per il secondo e-service")
    public void consumerRequireFilteredPurpose() {
        consumerRequireSubscribedPurpose();
    }

    @When("l'utente fruitore richiede una operazione di listing delle finalità in stato {string}")
    public void consumerRequireListingOperationForPurposeWithState(String state) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getConsumerPurposes(
                        0, 50, null, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null,
                        List.of(PurposeVersionState.fromValue(state))
                )
        );
    }

    @When("l'utente fruitore richiede una operazione di listing delle finalità filtrando per la keyword {string}")
    public void consumerRequireListingOperationPurporposeWithKeyword(String keyword) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().getConsumerPurposes(
                        0, 50, keyword, List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()), null, null
                )
        );

    }

}
