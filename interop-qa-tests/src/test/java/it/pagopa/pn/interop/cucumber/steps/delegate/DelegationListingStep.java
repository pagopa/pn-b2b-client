package it.pagopa.pn.interop.cucumber.steps.delegate;

import static it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState.ACTIVE;
import static it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState.WAITING_FOR_APPROVAL;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDelegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDelegations;
import it.pagopa.interop.generated.openapi.clients.bff.model.Pagination;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DelegationListingStep {
    private final SharedStepsContext sharedStepsContext;
    private final IDelegationApiClient delegationApiClient;
    private final PollingService pollingService;
    private final HttpCallExecutor httpCallExecutor;
    private final List<CompactDelegations> delegationList;

    public DelegationListingStep(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.delegationApiClient = clientTokenConfigurator.getDelegationApiClient();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.delegationList = new ArrayList<>();
    }

    @And("l'utente recupera le prime {int} pagine con la lista delle deleghe")
    public void retrieveDelegationsList(int pageNumber) {
        for (int i = 0; i < pageNumber; i++) {
            AtomicInteger offset = new AtomicInteger(i);
            httpCallExecutor.performCall(
                    () -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(), offset.get(), 50, List.of(), List.of(), List.of(), null, List.of())
            );
            delegationList.add((CompactDelegations) httpCallExecutor.getResponse());
        }
    }

    @And("l'utente recupera la lista delle deleghe in stato ACTIVE e WAITING_FOR_APPROVAL")
    public void retrieveDelegationsListByStatus() {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() ->  delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(), 0, 50, List.of(
                        ACTIVE, WAITING_FOR_APPROVAL),
                        List.of(), List.of(), null, List.of())),
            HttpStatus::is2xxSuccessful,
                "There was an error while retrieving the delegations!"
        );
        delegationList.add((CompactDelegations) httpCallExecutor.getResponse());
    }

    @Then("viene verificato che sono state ritornate le prime {int} pagine")
    public void verifyPaginationReturned(int pageNumber) {
        Assertions.assertEquals(
            delegationList.stream()
                .map(CompactDelegations::getPagination)
                .map(Pagination::getOffset)
                .collect(Collectors.toSet())
                .size(),
            pageNumber);
    }

    @And("viene verificato che le deleghe ritornate sono soltanto quelle in stato ACTIVE e WAITING_FOR_APPROVAL")
    public void verifyStatusDelegationsReturned() {
        List<CompactDelegation> delegations = delegationList.stream()
            .map(CompactDelegations::getResults)
            .findFirst()
            .orElse(emptyList());

        Condition<CompactDelegation> ofExpectedStates = new Condition<>(
            delegation -> delegation.getState().equals(ACTIVE) || delegation.getState().equals(WAITING_FOR_APPROVAL),
            "ACTIVE or WAITING_FOR_APPROVAL"
        );

        /* NOTE 13/01/2025: assertion checked for the following borderline cases:
         * - empty list: success
         * - null list (currently impossible): failure
         */
        assertThat(delegations).are(ofExpectedStates);
    }

}
