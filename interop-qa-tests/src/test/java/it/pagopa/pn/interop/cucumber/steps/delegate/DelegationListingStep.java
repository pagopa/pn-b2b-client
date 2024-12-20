package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDelegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDelegations;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.generated.openapi.clients.bff.model.Pagination;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DelegationListingStep {
    private final SharedStepsContext sharedStepsContext;
    private final IDelegationApiClient delegationApiClient;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;
    private final List<CompactDelegations> delegationList;

    public DelegationListingStep(SharedStepsContext sharedStepsContext,
                                 IDelegationApiClient delegationApiClient) {
        this.sharedStepsContext = sharedStepsContext;
        this.delegationApiClient = delegationApiClient;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.delegationList = new ArrayList<>();
    }

    @And("l'utente recupera le prime {int} pagine con la lista delle deleghe")
    public void retrieveDelegationsList(int pageNumber) {
        for (int i = 0; i < 5; i++) {
            AtomicInteger offset = new AtomicInteger(i);
            httpCallExecutor.performCall(
                    () -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(), offset.get(), 50, List.of(), List.of(), List.of(), null, List.of())
            );
            delegationList.add((CompactDelegations) httpCallExecutor.getResponse());
        }
    }

    @And("l'utente recupera la lista delle deleghe in stato ACTIVE e WAITING_FOR_APPROVAL")
    public void retrieveDelegationsListByStatus() {
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() ->  delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(), 0, 50, List.of(DelegationState.ACTIVE, DelegationState.WAITING_FOR_APPROVAL),
                        List.of(), List.of(), null, List.of())),
                res -> res.is2xxSuccessful(),
                "There was an error while retrieving the delegations!"
        );
        delegationList.add((CompactDelegations) httpCallExecutor.getResponse());
    }

    @Then("viene verificato che sono state ritornate le prime {int} pagine")
    public void verifyPaginationReturned(int pageNumber) {
        Assertions.assertTrue(delegationList.stream()
                .map(CompactDelegations::getPagination)
                .map(Pagination::getOffset)
                .collect(Collectors.toSet())
                .size() == pageNumber);
    }

    @And("viene verificato che le deleghe ritornate sono soltanto quelle in stato ACTIVE e WAITING_FOR_APPROVAL")
    public void verifyStatusDelegationsReturned() {
        delegationList.stream()
                .map(CompactDelegations::getResults)
                .findFirst()
                .orElse(List.of())
                .stream()
                .map(CompactDelegation::getState)
                .noneMatch(state -> (state != DelegationState.ACTIVE) || (state != DelegationState.WAITING_FOR_APPROVAL));
        }
}
