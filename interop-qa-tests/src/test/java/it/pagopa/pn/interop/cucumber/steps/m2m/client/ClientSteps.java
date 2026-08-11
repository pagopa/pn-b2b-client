package it.pagopa.pn.interop.cucumber.steps.m2m.client;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MClientsClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.client.utils.ClientResolver;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.*;
import java.util.function.Predicate;

import static org.apache.commons.lang3.ObjectUtils.allNull;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ClientSteps {
    public record ClientPurposesFilters(List<UUID> eserviceIds, List<PurposeVersionState> states) {
    }

    private final SharedStepsContext sharedStepsContext;
    private final IM2MClientsClient clientsApis;
    private final IHttpExecutor httpCallExecutor;
    private final ClientResolver clientResolver;
    private List<Purpose> retrievedClientPurposes = new ArrayList<>();

    public ClientSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        this.clientsApis = clientTokenConfigurator.getM2MClientsClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        clientsApis.setHttpCallExecutor(httpCallExecutor);
        clientResolver = new ClientResolver(sharedStepsContext);
    }

    @When("l'utente tenta di ottenere le finalità associate al client")
    public void getClientPurposes() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        httpCallExecutor.performCall(() -> clientsApis.getClientPurposes(clientId));
    }

    @When("l'utente tenta di recuperare il client")
    public void getClientByIdM2M() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        try {
            clientsApis.getClient(clientId);
        } catch (IllegalStateException e) {
            log.warn(httpCallExecutor.getErrorMessage());
        }
    }

    @When("l'utente tenta di ottenere le finalità associate ad un client inesistente")
    public void getNonExistentClientPurposes() {
        UUID clientId = UUID.randomUUID();
        httpCallExecutor.performCall(() -> clientsApis.getClientPurposes(clientId));
    }

    @When("l'utente recupera tutte le finalità associate al primo client creato")
    public void getAllPurposesForTrackedFirstClient() {
        UUID trackedClientId = sharedStepsContext.getClientCommonContext().getTrackedFirstClientId();
        assertThat(trackedClientId)
                .as("Il primo client tracciato non è disponibile nello scenario")
                .isNotNull();
        retrieveAllClientPurposes(trackedClientId);
    }

    private void retrieveAllClientPurposes(UUID clientId) {
        int offset = 0;
        int limit = 50;
        List<Purpose> allPurposes = new ArrayList<>();

        while (true) {
            Purposes page = clientsApis.getClientPurposes(clientId, offset, limit, null, null);
            Assertions.assertThat(page).as("La response non deve essere null").isNotNull();

            List<Purpose> currentPage = page.getResults();
            allPurposes.addAll(currentPage);

            if (currentPage.size() < limit) {
                break;
            }
            offset += limit;
        }

        this.retrievedClientPurposes = allPurposes;
    }

    @Then("le finalità associate al client sono state correttamente visualizzate")
    public void purposesVisualized() {
        List<PurposeSeed> createdPurposes = sharedStepsContext.getPurposeCommonContext()
                .getCreatedPurposes();
        List<Purpose> returnedPurposes = ((Purposes) httpCallExecutor.getResponse()).getResults();

        Predicate<Purpose> oneOfCreated = purpose -> createdPurposes.stream().anyMatch(created -> areConsistent(created, purpose));
        assertThat(returnedPurposes)
                .isNotEmpty()
                .allMatch(oneOfCreated, "each returned purpose match at least one created purpose");
    }

    @Then("vengono recuperate {int} finalità associate al client")
    public void verifyRetrievedPurposesSize(int expectedSize) {
        assertThat(retrievedClientPurposes)
                .as("Numero di finalità recuperate inatteso")
                .hasSize(expectedSize);
    }

    @Then("le finalità restituite sono tutte e sole le prime {int} finalità create")
    public void verifyRetrievedPurposesAreAllAndOnlyExpected(int expectedSize) {
        List<UUID> createdPurposeIds = sharedStepsContext.getPurposeCommonContext().getPurposesIdsAsUUID();
        assertThat(createdPurposeIds)
                .as("Le finalità create devono essere almeno %d", expectedSize)
                .hasSizeGreaterThanOrEqualTo(expectedSize);

        List<UUID> expectedPurposeIds = createdPurposeIds.subList(0, expectedSize);
        List<UUID> actualPurposeIds = retrievedClientPurposes.stream()
                .map(Purpose::getId)
                .toList();

        assertThat(actualPurposeIds)
                .as("Le finalità restituite devono essere tutte e sole le prime %d finalità create (senza considerare l'ordine)", expectedSize)
                .containsExactlyInAnyOrderElementsOf(expectedPurposeIds);
    }

    @When("vengono recuperate le finalità associate al client {string} con limit {string} e offset {string} e filtri eserviceIds {string}, states {string}")
    public void getClientPurposesWithPaginationAndFilters(String client, String limit, String offset, String eserviceIds, String states) {
        Integer limitValue = StepParser.nullableInteger(limit);
        Integer offsetValue = StepParser.nullableInteger(offset);

        UUID clientId = clientResolver.resolveClientId(client);
        List<UUID> eserviceIdFilter = clientResolver.resolveEserviceIds(eserviceIds);

        List<PurposeVersionState> stateFilter = StepParser.singletonListNullable(
                StepParser.nullOrValue(states),
                PurposeVersionState::fromValue
        );

        Purposes response;

        try {
            response = clientsApis.getClientPurposes(
                    clientId,
                    offsetValue,
                    limitValue,
                    eserviceIdFilter,
                    stateFilter
            );

            Assertions.assertThat(response)
                    .as("La response non deve essere null")
                    .isNotNull();

            ClientPurposesFilters appliedFilters = new ClientPurposesFilters(eserviceIdFilter, stateFilter);
            assertResultsMatchFilters(response, appliedFilters);

        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    private boolean areConsistent(PurposeSeed createdPurpose, Purpose returnedPurpose) {
        return allNull(createdPurpose, returnedPurpose) ||
                Objects.equals(createdPurpose.getConsumerId(), returnedPurpose.getConsumerId()) &&
                        Objects.equals(createdPurpose.getDescription(), returnedPurpose.getDescription()) &&
                        Objects.equals(createdPurpose.getTitle(), returnedPurpose.getTitle()) &&
                        Objects.equals(createdPurpose.getEserviceId(), returnedPurpose.getEserviceId()) &&
                        Objects.equals(createdPurpose.getIsFreeOfCharge(), returnedPurpose.getIsFreeOfCharge()) &&
                        Objects.equals(createdPurpose.getFreeOfChargeReason(), returnedPurpose.getFreeOfChargeReason());
    }

    private void assertResultsMatchFilters(Purposes response, ClientPurposesFilters filters) {
        List<Purpose> items = response.getResults();
        if (items.isEmpty()) return;

        boolean noEserviceFilter = (filters.eserviceIds() == null || filters.eserviceIds().isEmpty());
        boolean noStatesFilter = (filters.states() == null || filters.states().isEmpty());
        if (noEserviceFilter && noStatesFilter) return;

        for (Purpose item : items) {
            Assertions.assertThat(matchesAllFilters(item, filters))
                    .as("Risultato non coerente con filtri: item=%s, filters=%s", item, filters)
                    .isTrue();
        }
    }

    public static boolean matchesAllFilters(Purpose item, ClientPurposesFilters f) {
        if (item == null) return false;

        // eserviceIds filter
        if (f.eserviceIds() != null && !f.eserviceIds().isEmpty()) {
            UUID actual = item.getEserviceId();
            if (!f.eserviceIds().contains(actual)) return false;
        }

        // states filter
        if (f.states() != null && !f.states().isEmpty()) {
            PurposeVersionState actual = item.getCurrentVersion().getState();
            return f.states().contains(actual);
        }

        return true;
    }
}
