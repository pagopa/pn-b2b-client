package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.generated.openapi.clients.probingStatistics.model.TelemetryDataEserviceResponse;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.EserviceRow;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingResolver;
import it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingUtils;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingUtils.matchesAllFilters;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.*;

@Slf4j
public class ProbingSteps {
    private static final Duration NOT_ADVANCING_TOLERANCE = Duration.ofSeconds(1);

    private final IHttpExecutor httpCallExecutor;
    private final ProbingClient probingClient;
    private final ProbingContext probingContext;
    private final ProbingResolver resolver;

    public ProbingSteps(ProbingClient probingClient, SharedStepsContext sharedStepsContext) {
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.probingClient = probingClient;
        this.probingClient.setHttpCallExecutor(httpCallExecutor);
        this.probingContext = new ProbingContext();
        this.resolver = new ProbingResolver(this.probingClient, probingContext);
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

        try {
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
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("vengono recuperati dal catalogo gli e-service con limit {string} e offset {string} e filtri eserviceName {string}, producerName {string}, versionNumber {string}, state {string}")
    public void getEServiceCatalogWithPaginationAndFilters(String limit, String offset, String eserviceName, String producerName, String versionNumber, String state) {
        Integer limitValue = nullableInteger(limit);
        Integer offsetValue = nullableInteger(offset);

        String nameFilter = resolver.resolveEserviceName(eserviceName);
        String producerFilter = resolver.resolveProducer(producerName);
        Integer versionFilter = StepParser.nullableInteger(versionNumber);
        List<EserviceStateFE> stateFilter = StepParser.singletonListNullable(StepParser.nullOrValue(state), EserviceStateFE::fromValue);

        SearchEserviceResponse response;

        try {
            response = probingClient.searchEservices(
                    limitValue,
                    offsetValue,
                    nameFilter,
                    producerFilter,
                    versionFilter,
                    stateFilter
            );

            Assertions.assertThat(response).as("La response non deve essere null").isNotNull();
            ProbingUtils.EserviceFilters appliedFilters = new ProbingUtils.EserviceFilters(nameFilter, producerFilter, versionFilter, stateFilter);
            assertResultsMatchFilters(response, appliedFilters);

        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("viene modificato lo stato di probing dell'e-service con id {string} e id versione {string} in {string}")
    @When("viene modificato lo stato di probing dell'e-service con id {string} e id versione {string} in {string} e si verifica che coincida con quanto atteso")
    public void setProbingState(String eserviceId, String versionId, String probingEnabled) {
        UUID eserviceUuid = resolver.resolveEserviceId(eserviceId);
        UUID versionUuid = resolver.resolveVersionId(versionId);
        Long eserviceRecordId = resolver.getEserviceRecordId();

        ChangeProbingStateRequest probingState = new ChangeProbingStateRequest()
                .probingEnabled(nullableBoolean(probingEnabled));

        probingClient.updateEserviceProbingState(eserviceUuid, versionUuid, probingState);

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            httpCallExecutor.snapshot();

            EserviceRow expected = probingContext.getExpectedEserviceRow();
            expected.setProbingEnabled(Boolean.valueOf(probingEnabled));

            PollingService.makePolling(
                    () -> probingClient.getEserviceProbingData(eserviceRecordId),
                    resp -> resp.getProbingEnabled().equals(Boolean.valueOf(probingEnabled)),
                    "Errore durante il setting di probingEnabled per l'eservice con eserviceRecordId '" + eserviceRecordId + "'",
                    30,
                    1_000L
            );

            httpCallExecutor.resetFormSnapshot();
        }
    }

    @When("viene modificato lo stato operativo dell'e-service con id {string} e id versione {string} in {string}")
    @When("viene modificato lo stato operativo dell'e-service con id {string} e id versione {string} in {string} e si verifica che coincida con quanto atteso")
    public void setOperationalState(String eserviceId, String versionId, String eserviceState) {
        UUID eserviceUuid = resolver.resolveEserviceId(eserviceId);
        UUID versionUuid = resolver.resolveVersionId(versionId);
        Long eserviceRecordId = resolver.getEserviceRecordId();
        EserviceStateBE stateBE = EserviceStateBE.fromValue(eserviceState);

        ChangeEserviceStateRequest operationalState = new ChangeEserviceStateRequest()
                .eServiceState(parseNullableSafe(eserviceState, EserviceStateBE::fromValue));

        probingClient.updateEserviceState(eserviceUuid, versionUuid, operationalState);

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            EserviceRow expected = probingContext.getExpectedEserviceRow();
            expected.setState(stateBE.getValue());

            httpCallExecutor.snapshot();

            PollingService.makePolling(
                    () -> probingClient.getEserviceProbingData(eserviceRecordId),
                    resp -> resp.getEserviceActive().equals(stateBE.equals(EserviceStateBE.ACTIVE)),
                    "Errore durante il setting dell'eserviceState per l'eservice con eserviceRecordId '" + eserviceRecordId + "'",
                    30,
                    1_000L
            );

            httpCallExecutor.resetFormSnapshot();
        }
    }

    @When("aggiorno i parametri di probing dell'e-service con eserviceId {string} e versionId {string} impostando frequency {string}, startDate {string}, endDate {string}")
    @When("vengono aggiornati i parametri di probing dell'e-service con eserviceId {string} e versionId {string} impostando frequency {string}, startDate {string}, endDate {string} e si verifica che coincidano con quanto atteso")
    public void setEserviceFrequency(String eserviceId, String versionId, String frequency, String startDate, String endDate) {
        UUID eserviceUuid = resolver.resolveEserviceId(eserviceId);
        UUID versionUuid = resolver.resolveVersionId(versionId);
        Integer frequencyValue = resolver.resolveFrequency(frequency);
        OffsetTime startValue = resolver.resolvePollingStartTime(startDate);
        OffsetTime endValue = resolver.resolvePollingEndTime(endDate);

        try {
            probingClient.updateEserviceFrequency(eserviceUuid, versionUuid, frequencyValue, startValue, endValue);

            Long eserviceRecordId = resolver.getEserviceRecordId();
            EserviceRow expected = probingContext.getExpectedEserviceRow();
            expected.setPollingFrequency(frequencyValue);
            httpCallExecutor.snapshot();

            PollingService.makePolling(
                    () -> probingClient.getEserviceMainData(eserviceRecordId),
                    resp -> resp.getPollingFrequency() == expected.getPollingFrequency(),
                    "Errore durante il setting di probingEnabled per l'eservice con eserviceRecordId '" + eserviceRecordId + "'",
                    30,
                    1_000L
            );

            httpCallExecutor.resetFormSnapshot();

        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("vengono recuperati i main data dell'e-service con eserviceRecordId {string}")
    public void getEserviceMainData(String eserviceRecordId) {
        Long recordId = resolver.resolveEserviceRecordId(eserviceRecordId);

        try {
            MainDataEserviceResponse response = probingClient.getEserviceMainData(recordId);
            Assertions.assertThat(response).as("La response contenente i metadati anagrafici dell'e-service non deve essere null").isNotNull();

            EserviceRow actual = probingContext.getActualEserviceRow();
            actual.setPollingFrequency(response.getPollingFrequency());

        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("vengono recuperati i dati di probing dell'e-service con eserviceRecordId {string}")
    public void getEserviceProbingData(String eserviceRecordId) {
        Long recordId = resolver.resolveEserviceRecordId(eserviceRecordId);

        try {
            ProbingDataEserviceResponse response = probingClient.getEserviceProbingData(recordId);
            Assertions.assertThat(response).as("La response contenente i dati di probing dell'e-service non deve essere null").isNotNull();

            EserviceRow actual = probingContext.getActualEserviceRow();
            actual.setProbingEnabled(response.getProbingEnabled());
            actual.setState(response.getState().getValue());

            probingContext.setLastResponseTime(response.getResponseReceived() != null ? LocalDateTime.parse(response.getResponseReceived()) : null);

        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("viene recuperata la telemetria pubblica dell'e-service con eserviceRecordId {string} e pollingFrequency {string}")
    public void getEservicePublicTelemetry(String eserviceRecordId, String pollingFrequency) {
        Long recordIdValue = resolver.resolveEserviceRecordId(eserviceRecordId);
        Integer poolingFrequencyValue = resolver.resolveFrequency(pollingFrequency);

        try {
            TelemetryDataEserviceResponse response = probingClient.statisticsEservices(recordIdValue, poolingFrequencyValue);
            Assertions.assertThat(response).as("La response contenente la telemetria pubblica dell'e-service non deve essere null").isNotNull();

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                probingContext.getActualTelemetry().add(response);
            }
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("viene recuperata la telemetria dell'e-service con eserviceRecordId {string} e impostando pollingFrequency {string} , startDate {string} , endDate {string}")
    public void getEserviceTelemetry(String eserviceRecordId, String pollingFrequency, String startDate, String endDate) {
        Long recordIdValue = resolver.resolveEserviceRecordId(eserviceRecordId);
        Integer poolingFrequencyValue = resolver.resolveFrequency(pollingFrequency);
        String startDateValue = resolver.resolveDateToken(startDate).toString();
        String endDateValue = resolver.resolveDateToken(endDate).toString();

        TelemetryDataEserviceResponse response = probingClient.filteredStatisticsEservices(recordIdValue, poolingFrequencyValue, startDateValue, endDateValue);
        Assertions.assertThat(response).as("La response contenente la telemetria dell'e-service non deve essere null").isNotNull();

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            probingContext.getActualTelemetry().add(response);
        }
    }

    @Given("vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo")
    public void getEserviceRow() {
        EserviceRow eserviceRow = EserviceRow.atIndex(probingContext.getThreadNumber());
        probingContext.setActualEserviceRow(eserviceRow);
        probingContext.setExpectedEserviceRow(eserviceRow);
    }

    @Then("verifica che la responseReceived sia aggiornata coerentemente rispetto la frequency {string}, startDate {string}, endDate {string}")
    public void assertScheduler(String pollingFrequency, String startDate, String endDate) throws Exception {

        OffsetDateTime start = resolver.resolveDateToken(startDate);
        OffsetDateTime end = resolver.resolveDateToken(endDate);

        Assertions.assertThat(start).as("startDate non deve essere null").isNotNull();
        Assertions.assertThat(end).as("endDate non deve essere null").isNotNull();
        Assertions.assertThat(end).as("endDate deve essere dopo startDate").isAfter(start);

        Instant startI = start.toInstant();
        Instant endI = end.toInstant();
        Instant now = Instant.now();

        // CONTRATTO: frequency è in minuti (minimum: 1)
        int freqMinutes = resolver.resolveFrequency(pollingFrequency);
        Assertions.assertThat(freqMinutes)
                .as("pollingFrequency (minutes) deve essere >= 1")
                .isGreaterThanOrEqualTo(1);

        Duration period = Duration.ofMinutes(freqMinutes);

        // Policy su tolleranze
        Duration notAdvancingTolerance = Duration.ofSeconds(2); // piccoli delta (jitter/rounding/clock skew)
        Duration observeOutside = Duration.ofSeconds(20); // abbastanza per capire se sta aggiornando "a sorpresa"
        Duration observeInsideMax = Duration.ofSeconds(90); // se freq=1m possiamo ragionevolmente vedere 1 update

        // Semantica finestra: [start, end)
        boolean before = now.isBefore(startI);
        boolean after = !now.isBefore(endI); // now >= end

        if (before) {
            // 1) Prima della finestra: non deve avanzare
            assertNotAdvancing(observeOutside, notAdvancingTolerance, "Il probing sta avanzando PRIMA della finestra attesa");

            // 2) Se la finestra inizia a breve e finisce a breve, facciamo anche start&stop in un solo test (utile per casi tipo now+1m / now+2m)
            Duration untilStart = Duration.between(now, startI);
            if (!untilStart.isNegative() && untilStart.compareTo(Duration.ofSeconds(30)) <= 0) {
                waitUntil(startI, Duration.ofSeconds(35)); // cap

                // Dentro finestra: osserviamo, ma senza imporre "deve avanzare" perché non sappiamo se first-run è immediato.
                observeAndValidateInside(endI, observeInsideMax, notAdvancingTolerance, "Durante la finestra (appena iniziata) il probing si comporta in modo incoerente");
            }

            return;
        }

        if (after) {
            // Dopo la finestra: non deve avanzare
            assertNotAdvancing(observeOutside, notAdvancingTolerance, "Il probing sta avanzando DOPO la finestra attesa");
            return;
        }

        // Siamo dentro finestra
        // Se la finestra termina a breve (short window), facciamo start&stop nello stesso test:
        Duration untilEnd = Duration.between(now, endI);

        if (!untilEnd.isNegative() && untilEnd.compareTo(Duration.ofSeconds(45)) <= 0) {
            // Fase "durante finestra": se avanza, deve essere <= end (+ tolleranza)
            observeAndValidateInside(endI, untilEnd.plusSeconds(2), notAdvancingTolerance, "Durante la finestra corta il probing si comporta in modo incoerente");

            // Aspetta fino a end, poi verifica che non avanzi più
            waitUntil(endI, Duration.ofSeconds(60));
            assertNotAdvancing(observeOutside, notAdvancingTolerance, "Il probing non si è fermato dopo endDate (finestra corta)");
            return;
        }

        // Finestra “normale”: qui possiamo essere più ambiziosi SOLO se è ragionevole osservare un update.
        // Se period è 1 minuto, in 90s in genere lo vediamo. Se period è 10 minuti, no.
        Duration observeInside = min(observeInsideMax, period.plusSeconds(20)); // per freq=1m => 80s circa
        boolean canReasonablySeeAtLeastOneTick = period.compareTo(observeInsideMax) <= 0;

        if (canReasonablySeeAtLeastOneTick) {
            // Qui ci aspettiamo di vedere almeno un avanzamento entro observeInside
            assertAdvancingWithin(observeInside,
                    "Il probing non sta avanzando durante la finestra (atteso almeno 1 update dato che period=" + period + ")");
        } else {
            // Periodo troppo grande per essere osservabile
            // non imponiamo l’avanzamento, ma se avanza deve comunque rispettare end.
            observeAndValidateInside(endI, observeInside, notAdvancingTolerance, "Durante la finestra il probing avanza in modo incoerente rispetto alla endDate");
        }
    }

    @Then("verifica che la responseReceived NON sia aggiornata quando probing è disabilitato")
    public void assertSchedulerWhenProbingDisabled() throws Exception {
        Duration observe = Duration.ofSeconds(30);
        Duration tolerance = Duration.ofSeconds(2);

        assertNotAdvancing(observe, tolerance, "Il probing sta aggiornando anche se probingEnabled=false");
    }

    private void assertNotAdvancing(Duration observe, Duration tolerance, String message) throws Exception {
        Instant t1 = readLastResponseTime();
        TimeUnit.MILLISECONDS.sleep(observe.toMillis());
        Instant t2 = readLastResponseTime();

        long deltaMillis = Math.abs(t2.toEpochMilli() - t1.toEpochMilli());

        Assertions.assertThat(deltaMillis)
                .as(message + " (delta=" + deltaMillis + "ms, tolerance=" + tolerance.toMillis() + "ms)")
                .isLessThanOrEqualTo(tolerance.toMillis());
    }

    private void assertAdvancingWithin(Duration observe, String message) throws Exception {
        Instant t1 = readLastResponseTime();

        long deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(observe.toMillis());
        long stepMillis = 1000L; // 1s step: frequency è in minuti, non serve più fitto

        while (System.nanoTime() < deadlineNanos) {
            TimeUnit.MILLISECONDS.sleep(stepMillis);
            Instant t2 = readLastResponseTime();
            if (t2.isAfter(t1)) {
                return;
            }
        }

        Instant tFinal = readLastResponseTime();
        long deltaMillis = tFinal.toEpochMilli() - t1.toEpochMilli();
        Assertions.fail(message + " (delta=" + deltaMillis + "ms, observe=" + observe.toMillis() + "ms)");
    }

    private void observeAndValidateInside(Instant endI, Duration observe, Duration tolerance, String message) throws Exception {
        Instant baseline = readLastResponseTime();

        long deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(observe.toMillis());
        long stepMillis = 1000L;

        while (System.nanoTime() < deadlineNanos) {
            TimeUnit.MILLISECONDS.sleep(stepMillis);
            Instant current = readLastResponseTime();

            if (current.isAfter(baseline)) {
                // Se è avanzato, deve comunque non superare end (+ tolleranza)
                Instant endPlusTol = endI.plusMillis(tolerance.toMillis());
                Assertions.assertThat(current)
                        .as(message + " (lastResponseTime avanzato ma oltre endDate)")
                        .isBeforeOrEqualTo(endPlusTol);

                baseline = current; // aggiorno baseline e continuo a osservare
            }
        }
    }

    private void waitUntil(Instant target, Duration maxWait) throws Exception {
        Instant now = Instant.now();
        if (!now.isBefore(target)) return;

        long msToWait = target.toEpochMilli() - now.toEpochMilli();
        long capped = Math.min(msToWait, maxWait.toMillis());
        if (capped > 0) TimeUnit.MILLISECONDS.sleep(capped);
    }

    private Duration min(Duration a, Duration b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    private Instant readLastResponseTime() {
        Long eserviceRecordId = resolver.getEserviceRecordId();
        this.getEserviceMainData(String.valueOf(eserviceRecordId));
        this.getEserviceProbingData(String.valueOf(eserviceRecordId));
        return probingContext.getLastResponseTime().toInstant(ZoneOffset.UTC);
    }

    private void assertResultsMatchFilters(SearchEserviceResponse response, ProbingUtils.EserviceFilters filters) {
        List<SearchEserviceContent> items = response.getContent();
        if (items == null || items.isEmpty()) return; // niente da validare

        // Se tutti i filtri sono null, non serve validare
        if (filters.eserviceName() == null && isNullOrBlank(filters.producerName())
                && filters.versionNumber() == null && filters.states() == null) {
            return;
        }

        for (SearchEserviceContent item : items) {
            Assertions.assertThat(matchesAllFilters(item, filters))
                    .as("Risultato non coerente con filtri: item=%s, filters=%s", item, filters)
                    .isTrue();
        }
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
