package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingResolver;
import it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingUtils;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingUtils.*;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.*;

public class ProbingSteps {
    private final IHttpExecutor httpCallExecutor;
    private final ProbingClient probingClient;
    private final ProbingContext probingContext;
    private final ProbingResolver resolver;

    public ProbingSteps(ProbingClient probingClient, SharedStepsContext sharedStepsContext) {
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.probingClient = probingClient;
        probingClient.setHttpCallExecutor(httpCallExecutor);
        this.probingContext = new ProbingContext();
        this.resolver = new ProbingResolver(sharedStepsContext, probingClient, probingContext);
    }

    @And("il microservizio {string} risulta attivo")
    public void getStatus(String ms) {
        final int maxTry = 30;          // ~30 secondi
        final long sleepMs = 1_000L;    // 1 secondo

        switch (ms) {
            case "probing-api" -> PollingService.makePolling(
                    () -> {
                        probingClient.getProbingApiHealthStatus();
                        return httpCallExecutor.getResponseStatus();
                    },
                    HttpStatus::is2xxSuccessful,
                    "Il ms " + ms + " dovrebbe risultare attivo",
                    maxTry,
                    sleepMs
            );

            case "probing-statistics-api" -> PollingService.makePolling(
                    () -> {
                        probingClient.getStatisticsHealthStatus();
                        return httpCallExecutor.getResponseStatus();
                    },
                    HttpStatus::is2xxSuccessful,
                    "Il ms " + ms + " dovrebbe risultare attivo",
                    maxTry,
                    sleepMs
            );

            default -> throw new IllegalArgumentException("Il microservizio " + ms + " non esiste");
        }
    }

    @When("recupero la lista dei producers con limit {string} e offset {string} e producerName {string}")
    public void getProducersWithProducerName(String limit, String offset, String producerName) {
        Integer limitValue = nullableInteger(limit);
        Integer offsetValue = nullableInteger(offset);
        String producerTarget = StepParser.nullOrValue(producerName);

        List<SearchProducerNameResponse> producers =
                probingClient.getEservicesProducers(limitValue, offsetValue, producerTarget);

        Assertions.assertThat(producers)
                .as("La lista dei producer non deve essere null")
                .isNotNull();

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful() && producerTarget != null && !producers.isEmpty()) {

            Assertions.assertThat(producers)
                    .as("Tutti i risultati devono avere producerName='%s'", producerTarget)
                    .allSatisfy(p ->
                            Assertions.assertThat(p.getValue())
                                    .as("producerName del singolo elemento non deve essere null")
                                    .isNotNull()
                    );

            Assertions.assertThat(producers)
                    .as("Tutti i risultati devono matchare producerName='%s'", producerTarget)
                    .allMatch(p -> p.getValue().equals(producerTarget));
        }
    }

    @When("vengono recuperati dal catalogo gli e-service con limit {string} e offset {string} e filtri eserviceName {string}, producerName {string}, versionNumber {string}, state {string}")
    public void getEServiceCatalogWithPaginationAndFilters(String limit, String offset, String eserviceName, String producerName, String versionNumber, String state) {
        Integer limitValue = nullableInteger(limit);
        Integer offsetValue = nullableInteger(offset);

        String nameFilter = StepParser.nullOrValue(eserviceName);
        String producerFilter = StepParser.nullOrValue(producerName);
        Integer versionFilter = StepParser.nullableInteger(versionNumber);
        List<EserviceStateFE> stateFilter = StepParser.singletonListNullable(StepParser.nullOrValue(state), EserviceStateFE::fromValue);

        SearchEserviceResponse response = probingClient.searchEservices(
                limitValue,
                offsetValue,
                nameFilter,
                producerFilter,
                versionFilter,
                stateFilter
        );

        Assertions.assertThat(response).as("La response non deve essere null").isNotNull();

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            ProbingUtils.EserviceFilters appliedFilters = new ProbingUtils.EserviceFilters(nameFilter, producerFilter, versionFilter, stateFilter);
            assertResultsMatchFilters(response, appliedFilters);
        }
    }

    @When("viene modificato lo stato di probing dell'e-service con id {string} e id versione {string} in {string}")
    public void updateProbingState(String eserviceId, String versionId, String probingEnabled) {
        UUID eserviceUuid = resolver.resolveEserviceId(eserviceId);
        UUID versionUuid = resolver.resolveVersionId(versionId);
        Long eserviceRecordId = resolver.getEserviceRecordId();

        ChangeProbingStateRequest probingState = new ChangeProbingStateRequest()
                .probingEnabled(nullableBoolean(probingEnabled));

        probingClient.updateEserviceProbingState(eserviceUuid, versionUuid, probingState);

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            PollingService.makePolling(
                    () -> probingClient.getEserviceProbingData(eserviceRecordId),
                    resp -> resp.getProbingEnabled().equals(Boolean.valueOf(probingEnabled)),
                    "Errore durante il setting di probingEnabled per l'eservice con eserviceRecordId '" + eserviceRecordId + "'",
                    30,
                    1_000L
            );
        }
    }

    @When("viene modificato lo stato operativo dell'e-service con id {string} e id versione {string} in {string}")
    public void updateOperationalState(String eserviceId, String versionId, String eserviceState) {
        UUID eserviceUuid = resolver.resolveEserviceId(eserviceId);
        UUID versionUuid = resolver.resolveVersionId(versionId);
        Long eserviceRecordId = resolver.getEserviceRecordId();
        EserviceStateBE stateBE = EserviceStateBE.fromValue(eserviceState);

        ChangeEserviceStateRequest operationalState = new ChangeEserviceStateRequest()
                .eServiceState(parseNullableSafe(eserviceState, EserviceStateBE::fromValue));

        probingClient.updateEserviceState(eserviceUuid, versionUuid, operationalState);

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            PollingService.makePolling(
                    () -> probingClient.getEserviceProbingData(eserviceRecordId),
                    resp -> resp.getEserviceActive().equals(stateBE.equals(EserviceStateBE.ACTIVE)),
                    "Errore durante il setting dell'eserviceState per l'eservice con eserviceRecordId '" + eserviceRecordId + "'",
                    30,
                    1_000L
            );
        }
    }

    @And("vengono settati i parametri di probing di default per l'e-service")
    public void setDefaultProbingParamsForEservice() {
        UUID eserviceUuid = resolver.resolveEserviceId(ResolvableToken.ACTUAL.value());
        UUID versionUuid = resolver.resolveVersionId(ResolvableToken.ACTUAL.value());
        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // Valori di default riconoscibili per i test
        Integer defaultFrequency = 1;
        OffsetDateTime defaultStartDate = now.plusHours(1);
        OffsetDateTime defaultEndDate = now.plusHours(2);

        probingContext.setExpectedStartDate(defaultStartDate);
        probingContext.setExpectedEndDate(defaultEndDate);
        probingContext.setExpectedFrequency(defaultFrequency);

        probingClient.updateEserviceFrequency(eserviceUuid, versionUuid, defaultFrequency, defaultStartDate, defaultEndDate);
        assertProbingParams(204);
    }

    @When("aggiorno i parametri di probing dell'e-service con eserviceId {string} e versionId {string} impostando frequency {string}, startDate {string}, endDate {string}")
    public void updateEserviceFrequency(String eserviceId, String versionId, String frequency, String startDate, String endDate) {
        UUID eserviceUuid = resolver.resolveEserviceId(eserviceId);
        UUID versionUuid = resolver.resolveVersionId(versionId);

        Integer frequencyValue = resolver.resolveFrequency(frequency);
        OffsetDateTime startValue = resolver.resolveDateToken(startDate, probingContext.getExpectedStartDate());
        OffsetDateTime endValue = resolver.resolveDateToken(endDate, probingContext.getExpectedEndDate());

        probingClient.updateEserviceFrequency(eserviceUuid, versionUuid, frequencyValue, startValue, endValue);
        assertProbingParams(204);
    }

    private void assertProbingParams(int expectedStatusCode) {
        int actualStatusCode = httpCallExecutor.getResponseStatus().value();
        if (actualStatusCode != expectedStatusCode) return;

        Long eserviceRecordId = resolver.getEserviceRecordId();

        // 1) se la finestra attesa parte nel futuro, aspetta fino allo start (con cap)
        waitUntilExpectedWindowStarts(probingContext.getExpectedStartDate());

        // 2) calcola policy di polling in base a finestra/frequenza
        ProbingUtils.PollingPolicy policy = computePollingPolicy(
                probingContext.getExpectedStartDate(),
                probingContext.getExpectedEndDate(),
                probingContext.getExpectedFrequency()
        );

        PollingService.makePolling(
                () -> probingClient.getEserviceMainData(eserviceRecordId),
                resp -> {
                    if (!isProbingStateUpdated(resp)) return false;
                    probingContext.setActualFrequency(resp.getPollingFrequency());
                    return true;
                },
                "Errore durante il setting di parametri di probing l'e-service con eserviceRecordId '" + eserviceRecordId + "'",
                policy.maxTry(),
                policy.sleepMs()
        );
    }

    private boolean isProbingStateUpdated(MainDataEserviceResponse resp) {
        return resp != null
                && resp.getPollingFrequency() != null
                && isWithinExpectedWindow(OffsetDateTime.now(), probingContext.getExpectedStartDate(), probingContext.getExpectedEndDate())
                && resp.getPollingFrequency().equals(probingContext.getExpectedFrequency());
    }

    private void assertResultsMatchFilters(SearchEserviceResponse response, ProbingUtils.EserviceFilters filters) {
        List<SearchEserviceContent> items = response.getContent();
        if (items == null || items.isEmpty()) return; // niente da validare

        // Se tutti i filtri sono null, non serve validare
        if (filters.eserviceName() == null && filters.producerName() == null
                && filters.versionNumber() == null && filters.states() == null) {
            return;
        }

        for (SearchEserviceContent item : items) {
            Assertions.assertThat(matchesAllFilters(item, filters))
                    .as("Risultato non coerente con filtri: item=%s, filters=%s", item, filters)
                    .isTrue();
        }
    }
}
