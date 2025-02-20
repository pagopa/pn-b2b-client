package it.pagopa.pn.interop.cucumber.steps.delegate;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDelegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDelegations;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServices;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationTenants;
import it.pagopa.interop.generated.openapi.clients.bff.model.Pagination;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DelegationListingStep {
    private final SharedStepsContext sharedStepsContext;
    private final IDelegationApiClient delegationApiClient;
    private final PollingService pollingService;
    private final HttpCallExecutor httpCallExecutor;
    private final List<CompactDelegations> delegationList;
    private final IConsumerDelegationsApiClient consumerDelegationsApiClient;

    private DelegationTenants delegationTenants;
    private CompactEServices compactEServices;

    public DelegationListingStep(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.delegationApiClient = clientTokenConfigurator.getDelegationApiClient();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.delegationList = new ArrayList<>();
        this.consumerDelegationsApiClient = clientTokenConfigurator.getConsumerDelegationsApiClient();
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

    @And("l'ente {delegationRole} visualizza l'elenco delle deleghe ricevute")
    public void retrieveDelegateDelegationsList(DelegationRole delegationRole) {
        UUID tenantId = sharedStepsContext.getDelegationCommonContext().getIdBy(delegationRole);
        httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(), 0, 2, List.of(), List.of(), List.of(tenantId), null, List.of()));
    }

    @And("l'ente {delegationRole} visualizza l'elenco delle deleghe conferite")
    public void retrieveDelegatorDelegationsList(DelegationRole delegationRole) {
        UUID tenantId = sharedStepsContext.getDelegationCommonContext().getIdBy(delegationRole);
        httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(), 0, 2, List.of(), List.of(tenantId), List.of(), null, List.of()));
    }

    @And("l'utente visualizza il dettaglio della delega creata")
    public void retrieveDelegationDetails() {
        String xCorrelationId = sharedStepsContext.getXCorrelationId();
        String delegationId = String.valueOf(sharedStepsContext.getDelegationCommonContext().getDelegationId());
        httpCallExecutor.performCall(() -> delegationApiClient.getDelegation(xCorrelationId, delegationId));
    }

    @And("l'utente recupera la lista delle deleghe in stato ACTIVE e WAITING_FOR_APPROVAL")
    public void retrieveDelegationsListByStatus() {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() ->  delegationApiClient.getDelegation(sharedStepsContext.getXCorrelationId(), 0, 50, List.of(DelegationState.ACTIVE, DelegationState.WAITING_FOR_APPROVAL),
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
        delegationList.stream()
                .map(CompactDelegations::getResults)
                .findFirst()
                .orElse(List.of())
                .stream()
                .map(CompactDelegation::getState)
                .noneMatch(state -> (state != DelegationState.ACTIVE) || (state != DelegationState.WAITING_FOR_APPROVAL));
        }

    @And("si recupera la lista dei delegatori e si verifica che non sia vuota")
    public void retrieveDelegators() {
        try {
            delegationTenants = consumerDelegationsApiClient.getConsumerDelegators(sharedStepsContext.getXCorrelationId(), 0, 50, null,
                    List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId()));
        } catch (RestClientException e) {
            throw new RuntimeException("There was an error while retrieving the delegators list: ", e);
        }
        assertResultsNotEmpty(delegationTenants, DelegationTenants::getResults, "deleganti");
    }

    @And("si recupera la lista dei delegatori con deleghe ATTIVE e si verifica che non sia vuota")
    public void retrieveDelegatorsWithActiveAgreement() {
        try {
            delegationTenants = consumerDelegationsApiClient.getConsumerDelegatorsWithAgreements(sharedStepsContext.getXCorrelationId(), 0, 50, null);
        } catch (RestClientException e) {
            throw new RuntimeException("There was an error while retrieving the delegators with an active agreement: ", e);
        }
        assertResultsNotEmpty(delegationTenants, DelegationTenants::getResults, "deleganti");
    }

    @And("viene recuperata la lista degli e-service delegati e si verifica che non sia vuota")
    public void retrieveDelegatedEServices() {
        try {
            compactEServices = consumerDelegationsApiClient.getConsumerDelegatedEservices(sharedStepsContext.getXCorrelationId(),
                    sharedStepsContext.getDelegationCommonContext().getDelegatorId(), 0, 50, null);
        } catch (RestClientException e) {
            throw new RuntimeException("There was an error while retrieving the delegated e-service list: ", e);
        }
        assertResultsNotEmpty(compactEServices, CompactEServices::getResults, "e-service delegati");
    }

    private static <T, U> void assertResultsNotEmpty(T resultsContainer, Function<T, U> resultsExtractor, String resultsName) {
        assertThat(resultsContainer)
            .withFailMessage("L'oggetto di risposta contenente la lista di '%s' non dovrebbe essere NULL", resultsName)
            .isNotNull()
            .extracting(resultsExtractor)
            .withFailMessage("La lista di '%s' non dovrebbe essere NULL", resultsName)
            .asList()
            .withFailMessage("La lista di '%s' non dovrebbe essere vuota", resultsName)
            .isNotEmpty()
        ;
    }
}
