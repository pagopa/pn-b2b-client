package it.pagopa.pn.interop.cucumber.steps.pagination;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Scenario-local glue: use the existing Cucumber glue root and dependency injection. */
public class ReadModelPaginationSteps {
    private final ClientTokenConfigurator clients;
    private final SharedStepsContext context;
    private final Map<String, List<UUID>> baselines = new HashMap<>();
    private Object lastQueriedResponse;
    private String lastDomain;
    private Map<String, String> lastFilters;

    public ReadModelPaginationSteps(ClientTokenConfigurator clients, SharedStepsContext context) {
        this.clients = clients;
        this.context = context;
    }

    @Given("PIN-7134 il read model {string} espone {int} risultati con i filtri:")
    public void awaitReadModel(String domain, int expected, DataTable table) {
        Assertions.assertTrue(expected > 0 && expected < 50,
                "Readiness richiede una fixture positiva interamente contenuta nella pagina di 50 elementi");
        Map<String, String> filters = filters(domain, table);
        AtomicReference<List<UUID>> previous = new AtomicReference<>();
        AtomicInteger stable = new AtomicInteger();
        // Do not poll on totalCount: that is the regression assertion, not a readiness condition.
        context.getPollingService().makePolling(() -> {
            query(domain, 0, 50, filters);
            Snapshot snapshot = snapshot();
            List<UUID> ids = snapshot.ids();
            boolean ready = ids.size() == expected
                    && new HashSet<>(ids).size() == expected
                    && (!upgradeable(filters) || snapshot.upgradeFlags().stream().allMatch(Boolean.TRUE::equals));
            stable.set(ready ? (ids.equals(previous.get()) ? stable.get() + 1 : 1) : 0);
            previous.set(List.copyOf(ids));
            return stable.get() >= 2;
        }, Boolean.TRUE::equals, "PIN-7134: fixture non proiettata per " + domain + ", filtri " + filters);
        baselines.put(key(domain, filters), List.copyOf(snapshot().ids()));
        lastQueriedResponse = null;
    }

    @When("PIN-7134 l'utente interroga {string} con offset {int} e limit {int} e i filtri:")
    public void list(String domain, int offset, int limit, DataTable table) {
        Assertions.assertTrue(offset >= 0 && limit > 0);
        Map<String, String> filters = filters(domain, table);
        query(domain, offset, limit, filters);
        lastDomain = domain;
        lastFilters = filters;
        lastQueriedResponse = context.getHttpCallExecutor().getResponse();
    }

    @Then("PIN-7134 la risposta contiene {int} risultati distinti e totalCount {int} con offset {int} e limit {int}")
    public void assertPage(int count, int total, int offset, int limit) {
        Snapshot page = snapshot();
        Assertions.assertEquals(count, page.ids().size(), "results.size");
        Assertions.assertEquals(count, new HashSet<>(page.ids()).size(), "ID duplicati nella pagina");
        Assertions.assertNotNull(page.pagination(), "pagination assente");
        Assertions.assertEquals(Integer.valueOf(total), page.pagination().getTotalCount(), "pagination.totalCount");
        Assertions.assertEquals(Integer.valueOf(offset), page.pagination().getOffset(), "pagination.offset");
        Assertions.assertEquals(Integer.valueOf(limit), page.pagination().getLimit(), "pagination.limit");
        Assertions.assertTrue(count <= limit && count <= total, "Invariante di paginazione violato");

        // Existing listing steps keep working: compare ordered slices only for queries made by this glue.
        if (lastQueriedResponse == context.getHttpCallExecutor().getResponse()) {
            if (upgradeable(lastFilters)) {
                Assertions.assertTrue(page.upgradeFlags().stream().allMatch(Boolean.TRUE::equals),
                        "showOnlyUpgradeable=true contiene agreement non aggiornabili");
            }
            if (lastFilters.containsKey("states")) {
                Set<String> allowed = Set.copyOf(csv(lastFilters.get("states")));
                Assertions.assertTrue(page.states().stream().allMatch(allowed::contains), "Stati fuori filtro");
            }
            List<UUID> baseline = baselines.get(key(lastDomain, lastFilters));
            if (baseline != null) {
                int start = Math.min(offset, baseline.size());
                int end = (int) Math.min((long) offset + limit, baseline.size());
                Assertions.assertEquals(baseline.subList(start, end), page.ids(),
                        "La pagina deve essere la porzione ordinata della fixture sincronizzata");
            }
        }
    }

    private Map<String, String> filters(String domain, DataTable table) {
        Set<String> allowed = switch (domain) {
            case "agreement-fruitore" -> Set.of("states", "showOnlyUpgradeable", "producer");
            case "agreement-erogatore" -> Set.of("states", "showOnlyUpgradeable", "consumer");
            case "catalogo" -> Set.of("keyword", "producer");
            case "deleghe" -> Set.of("states");
            default -> throw new IllegalArgumentException("Dominio non supportato: " + domain);
        };
        Map<String, String> values = new HashMap<>(table.asMap(String.class, String.class));
        Assertions.assertTrue(allowed.containsAll(values.keySet()), "Filtro non supportato: " + values.keySet());
        if (values.containsKey("showOnlyUpgradeable")) {
            Assertions.assertTrue(Set.of("true", "false").contains(values.get("showOnlyUpgradeable")));
        }
        return Map.copyOf(values);
    }

    private void query(String domain, int offset, int limit, Map<String, String> filters) {
        clients.setBearerToken(context.getUserToken());
        List<UUID> eservices = context.getEServicesCommonContext().getPublishedEservicesIds()
                .stream().map(EServiceDescriptor::getEServiceId).toList();
        switch (domain) {
            case "agreement-fruitore", "agreement-erogatore" -> {
                Assertions.assertFalse(eservices.isEmpty(), "Manca lo scope degli e-service creati dal test");
                List<AgreementState> states = csv(filters.get("states")).stream()
                        .map(AgreementState::fromValue).toList();
                Boolean upgrade = filters.containsKey("showOnlyUpgradeable")
                        ? Boolean.valueOf(filters.get("showOnlyUpgradeable")) : null;
                if (domain.equals("agreement-fruitore")) {
                    context.getHttpCallExecutor().performCall(() -> clients.getAgreementClient()
                            .getConsumerAgreements(offset, limit, eservices, tenantIds(filters.get("producer")), states, upgrade));
                } else {
                    context.getHttpCallExecutor().performCall(() -> clients.getAgreementClient()
                            .getProducerAgreements(offset, limit, eservices, tenantIds(filters.get("consumer")), states, upgrade));
                }
            }
            case "catalogo" -> {
                String keyword = filters.get("keyword");
                String q = "corrente".equals(keyword) ? context.getEServicesCommonContext().getName()
                        : String.valueOf(context.getTestSeed()) + (keyword == null ? "" : "-" + keyword);
                Assertions.assertNotNull(q, "Nome dell'e-service corrente assente");
                context.getHttpCallExecutor().performCall(() -> clients.getEServiceClient().getEServicesCatalog(
                        offset, limit, q, tenantIds(filters.get("producer")), List.of(),
                        List.of(EServiceDescriptorState.PUBLISHED, EServiceDescriptorState.SUSPENDED), null, null, null));
            }
            case "deleghe" -> {
                UUID eserviceId = context.getEServicesCommonContext().getEserviceId();
                UUID delegatorId = context.getDelegationCommonContext().getDelegatorId();
                UUID delegateId = context.getDelegationCommonContext().getDelegateId();
                Assertions.assertNotNull(eserviceId);
                Assertions.assertNotNull(delegatorId);
                Assertions.assertNotNull(delegateId);
                List<DelegationState> states = csv(filters.get("states")).stream()
                        .map(DelegationState::fromValue).toList();
                context.getHttpCallExecutor().performCall(() -> clients.getDelegationApiClient().getDelegation(
                        offset, limit, states, List.of(delegatorId), List.of(delegateId),
                        DelegationKind.DELEGATED_PRODUCER, List.of(eserviceId)));
            }
            default -> throw new IllegalArgumentException("Dominio non supportato: " + domain);
        }
    }

    private Snapshot snapshot() {
        Assertions.assertEquals(200, context.getHttpCallExecutor().getResponseStatus().value(), "HTTP status");
        Object body = context.getHttpCallExecutor().getResponse();
        if (body instanceof ResponseEntity<?> response) {
            Assertions.assertEquals(200, response.getStatusCode().value());
            body = response.getBody();
        }
        Assertions.assertNotNull(body, "Body assente");
        if (body instanceof Agreements agreements) {
            Assertions.assertNotNull(agreements.getResults());
            return new Snapshot(agreements.getResults().stream().map(AgreementListEntry::getId).toList(),
                    agreements.getPagination(),
                    agreements.getResults().stream().map(AgreementListEntry::getCanBeUpgraded).toList(),
                    agreements.getResults().stream().map(row -> row.getState().getValue()).toList());
        }
        if (body instanceof CatalogEServices catalog) {
            Assertions.assertNotNull(catalog.getResults());
            return new Snapshot(catalog.getResults().stream().map(CatalogEService::getId).toList(),
                    catalog.getPagination(), List.of(), List.of());
        }
        if (body instanceof CompactDelegations delegations) {
            Assertions.assertNotNull(delegations.getResults());
            return new Snapshot(delegations.getResults().stream().map(CompactDelegation::getId).toList(),
                    delegations.getPagination(), List.of(),
                    delegations.getResults().stream().map(row -> row.getState().getValue()).toList());
        }
        throw new AssertionError("DTO di lista non supportato: " + body.getClass().getName());
    }

    private List<UUID> tenantIds(String tenant) {
        return tenant == null ? List.of() : List.of(context.getIdentityService().getOrganizationId(tenant));
    }

    private static List<String> csv(String value) {
        if (value == null || value.isBlank()) return List.of();
        return java.util.Arrays.stream(value.split(",")).map(String::trim).toList();
    }

    private static boolean upgradeable(Map<String, String> filters) {
        return filters != null && "true".equals(filters.get("showOnlyUpgradeable"));
    }

    private static String key(String domain, Map<String, String> filters) {
        return domain + ":" + new java.util.TreeMap<>(filters);
    }

    private record Snapshot(List<UUID> ids, Pagination pagination, List<Boolean> upgradeFlags, List<String> states) {}
}
