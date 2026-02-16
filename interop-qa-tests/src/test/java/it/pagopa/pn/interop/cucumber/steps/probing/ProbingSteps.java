package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.probing.model.*;
import it.pagopa.interop.generated.openapi.clients.probingStatistics.model.EserviceStatus;
import it.pagopa.interop.generated.openapi.clients.probingStatistics.model.PercentageContent;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingUtils.matchesAllFilters;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.*;

@Slf4j
public class ProbingSteps {

    private final IHttpExecutor httpCallExecutor;
    private final ProbingClient probingClient;
    private final ProbingContext probingContext;
    private final ProbingResolver resolver;

    public ProbingSteps(ProbingClient probingClient, SharedStepsContext sharedStepsContext) {
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.probingClient = probingClient;
        this.probingClient.setHttpCallExecutor(httpCallExecutor);
        this.probingContext = new ProbingContext();
        this.resolver = new ProbingResolver(probingContext);
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

        try {
            probingClient.updateEserviceProbingState(eserviceUuid, versionUuid, probingState);
            httpCallExecutor.snapshot();

            EserviceRow expected = probingContext.getExpectedEserviceRow();
            expected.setProbingEnabled(Boolean.parseBoolean(probingEnabled));

            PollingService.makePolling(
                    () -> probingClient.getEserviceProbingData(eserviceRecordId),
                    resp -> resp.getProbingEnabled().equals(Boolean.valueOf(probingEnabled)),
                    "Errore durante il setting di probingEnabled per l'eservice con eserviceRecordId '" + eserviceRecordId + "'",
                    30,
                    1_000L
            );

            httpCallExecutor.resetFormSnapshot();
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("viene modificato lo stato operativo dell'e-service con id {string} e id versione {string} in {string}")
    @When("viene modificato lo stato operativo dell'e-service con id {string} e id versione {string} in {string} e si verifica che coincida con quanto atteso")
    public void setOperationalState(String eserviceId, String versionId, String eserviceState) {
        UUID eserviceUuid = resolver.resolveEserviceId(eserviceId);
        UUID versionUuid = resolver.resolveVersionId(versionId);
        Long eserviceRecordId = resolver.getEserviceRecordId();
        EserviceStateBE stateBE = resolver.resolveEserviceStateBE(eserviceState);

        ChangeEserviceStateRequest operationalState = new ChangeEserviceStateRequest()
                .eServiceState(parseNullableSafe(eserviceState, EserviceStateBE::fromValue));

        try {
            probingClient.updateEserviceState(eserviceUuid, versionUuid, operationalState);
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

        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
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

            probingContext.setLastResponseTime(response.getResponseReceived() != null ? OffsetDateTime.parse(response.getResponseReceived()) : null);

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

            probingContext.getActualTelemetry().add(response);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("viene recuperata la telemetria dell'e-service con eserviceRecordId {string} e impostando pollingFrequency {string} , startDate {string} , endDate {string}")
    public void getEserviceTelemetry(String eserviceRecordId, String pollingFrequency, String startDate, String endDate) {
        Long recordIdValue = resolver.resolveEserviceRecordId(eserviceRecordId);
        Integer poolingFrequencyValue = resolver.resolveFrequency(pollingFrequency);
        OffsetDateTime startDateValue = dateTimeOrNull(startDate);
        OffsetDateTime endDateValue = dateTimeOrNull(endDate);

        try {
            TelemetryDataEserviceResponse response = probingClient.filteredStatisticsEservices(recordIdValue, poolingFrequencyValue, startDateValue, endDateValue);
            Assertions.assertThat(response).as("La response contenente la telemetria dell'e-service non deve essere null").isNotNull();

            probingContext.getActualTelemetry().add(response);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @Given("vengono calcolate le informazioni di probing relative ad un e-service presente a catalogo")
    public void getEserviceRow() {
        EserviceRow eserviceRow = EserviceRow.atIndex(probingContext.getThreadNumber());
        probingContext.setActualEserviceRow(eserviceRow);
        probingContext.setExpectedEserviceRow(eserviceRow);
    }

    @Given("vengono calcolate le informazioni di probing relative ad un e-service con health check {word} presente a catalogo")
    public void getEserviceRowWithOutcome(String outcomeStr) {
        EserviceRow.Outcome outcome = EserviceRow.Outcome.valueOf(outcomeStr.toUpperCase());

        int okCount = ProbingContext.ESERVICE_OK_COUNT;
        int errorCount = ProbingContext.ESERVICE_KO_COUNT;
        int randomCount = ProbingContext.ESERVICE_RANDOM_COUNT;

        EserviceRow row = EserviceRow.pickByOutcome(outcome, okCount, errorCount, randomCount);

        probingContext.setActualEserviceRow(row);
        probingContext.setExpectedEserviceRow(row);
    }

    @Then("verifica che la responseReceived sia aggiornata coerentemente rispetto la frequency {string}, clockScheduler {string}, startDate {string}, endDate {string}")
    public void assertScheduler(String pollingFrequency, String clockScheduler, String startDate, String endDate) throws Exception {

        // --- Inputs
        Duration tick = resolver.resolveSchedulerInterval(clockScheduler);
        Assertions.assertThat(tick)
                .as("clockScheduler deve essere valorizzato e > 0")
                .isNotNull()
                .isGreaterThan(Duration.ZERO);

        int freqMinutes = resolver.resolveFrequency(pollingFrequency);
        Assertions.assertThat(freqMinutes)
                .as("pollingFrequency (minutes) deve essere >= 1")
                .isGreaterThanOrEqualTo(1);

        OffsetTime start = resolver.resolvePollingStartTime(startDate);
        OffsetTime end = resolver.resolvePollingEndTime(endDate);

        Assertions.assertThat(start).as("startDate non deve essere null").isNotNull();
        Assertions.assertThat(end).as("endDate non deve essere null").isNotNull();
        Assertions.assertThat(end).as("endDate deve essere dopo startDate").isAfter(start);

        // --- Instants
        LocalDate today = LocalDate.now();
        Instant startI = start.atDate(today).toInstant();
        Instant endI = end.atDate(today).toInstant();

        // --- Policy (solo tick + jitter)
        Duration jitter = Duration.ofSeconds(20);
        Duration unit = tick.plus(jitter);
        Duration notAdvancingTolerance = Duration.ofSeconds(2);
        Duration boundaryBuffer = Duration.ofSeconds(1);

        Instant now = Instant.now();

        // --- AFTER: fuori finestra
        if (!now.isBefore(endI)) {
            assertNotAdvancing(unit, notAdvancingTolerance,
                    "Il probing sta avanzando DOPO la finestra attesa");
            return;
        }

        // --- BEFORE: fuori finestra (non deve avanzare fino allo start)
        if (now.isBefore(startI)) {
            Duration untilStart = Duration.between(now, startI);
            Duration observe = min(unit, untilStart.minus(boundaryBuffer));

            if (!observe.isNegative() && !observe.isZero()) {
                assertNotAdvancing(observe, notAdvancingTolerance,
                        "Il probing sta avanzando PRIMA della finestra attesa");
            }

            // best effort: prova a entrare in finestra al massimo entro 1 unit
            waitUntil(startI, unit);

            now = Instant.now();
            if (!now.isBefore(endI)) {
                // finestra scaduta durante l'attesa
                return;
            }
            if (now.isBefore(startI)) {
                // non siamo entrati (attesa cappata): stop qui
                return;
            }
        }

        // --- INSIDE: siamo in finestra
        now = Instant.now();
        if (now.isBefore(endI)) {
            verifyInsideWindow(endI, now, unit, notAdvancingTolerance, tick, jitter);

            // --- STOP: aspetta end (max 1 unit) e poi non deve avanzare per 1 unit
            waitUntil(endI, unit);
            // assorbe eventuale update tardivo, poi verifica stabilità
            assertStopsAfterEnd(unit, notAdvancingTolerance,
                    "Il probing non si è fermato stabilmente dopo endDate");
        }
    }

    private void verifyInsideWindow(
            Instant endI,
            Instant now,
            Duration unit,
            Duration notAdvancingTolerance,
            Duration tick,
            Duration jitter
    ) throws Exception {

        Duration untilEnd = Duration.between(now, endI);
        if (untilEnd.isNegative() || untilEnd.isZero()) return;

        // Se manca meno di 1 unit alla fine finestra, non ha senso pretendere advancing: best effort
        if (untilEnd.compareTo(unit) < 0) {
            observeAndValidateInside(endI, untilEnd.plusSeconds(2), notAdvancingTolerance,
                    "Dentro finestra ma troppo vicini alla endDate per pretendere un update (unit=" + unit + ")");
            return;
        }

        // Altrimenti: mi aspetto almeno 1 update entro 1 unit (tick + jitter)
        assertAdvancingWithin(unit,
                "Il probing non sta avanzando durante la finestra (atteso >=1 update entro 1 unit=" + unit +
                        ", tick=" + tick + ", jitter=" + jitter + ")");
    }


    @Then("verifica che la responseReceived NON sia aggiornata quando probing è disabilitato")
    public void assertSchedulerWhenProbingDisabled() throws Exception {
        Duration observe = Duration.ofSeconds(30);
        Duration tolerance = Duration.ofSeconds(2);

        assertNotAdvancing(observe, tolerance, "Il probing sta aggiornando anche se probingEnabled=false");
    }

    @And("la telemetria dell'e-service risulta aggiornata con successo")
    public void assertTelemetry() {
        List<TelemetryDataEserviceResponse> actualTelemetry = probingContext.getActualTelemetry();
        EserviceRow actual = probingContext.getActualEserviceRow();
        boolean probingEnabled = actual.isProbingEnabled();

        // 1) La telemetria deve esistere come risposta (anche se potrebbe essere "scarica")
        Assertions.assertThat(actualTelemetry)
                .as("La telemetria dell'e-service non deve essere null")
                .isNotNull();

        // probing disabled: ok anche lista vuota
        if (!probingEnabled && actualTelemetry.isEmpty()) {
            return;
        }

        // Se probing è abilitato mi aspetto almeno un elemento
        if (probingEnabled) {
            Assertions.assertThat(actualTelemetry)
                    .as("Con probing abilitato mi aspetto telemetria non vuota")
                    .isNotEmpty();

            TelemetryDataEserviceResponse lastTelemetry = actualTelemetry.get(actualTelemetry.size() - 1);
            Assertions.assertThat(lastTelemetry)
                    .as("Ultimo elemento telemetry non deve essere null")
                    .isNotNull();

            List<PercentageContent> lastPercentages = lastTelemetry.getPercentages();
            Assertions.assertThat(lastPercentages)
                    .as("percentages non deve essere null")
                    .isNotNull();

            // status -> value, default 0 se assente (evita .get() su Optional)
            Map<EserviceStatus, Float> pctByStatus = lastPercentages.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            PercentageContent::getStatus,
                            PercentageContent::getValue,
                            (a, b) -> b
                    ));

            float koPercentage = pctByStatus.getOrDefault(EserviceStatus.KO, 0f);
            float okPercentage = pctByStatus.getOrDefault(EserviceStatus.OK, 0f);
            float ndPercentage = pctByStatus.getOrDefault(EserviceStatus.N_D, 0f);

            // sanity minima
            Assertions.assertThat(koPercentage).as("KO% deve essere >= 0").isGreaterThanOrEqualTo(0f);
            Assertions.assertThat(okPercentage).as("OK% deve essere >= 0").isGreaterThanOrEqualTo(0f);
            Assertions.assertThat(ndPercentage).as("N_D% deve essere >= 0").isGreaterThanOrEqualTo(0f);

            // assert minimali per outcome
            if (actual.isOk()) {
                Assertions.assertThat(koPercentage)
                        .as("Outcome OK: KO% deve essere 0")
                        .isEqualTo(0f);

                Assertions.assertThat(okPercentage)
                        .as("Outcome OK: OK% deve essere > 0")
                        .isGreaterThan(0f);

            } else if (actual.isKo()) {
                Assertions.assertThat(okPercentage)
                        .as("Outcome KO: OK% deve essere 0")
                        .isEqualTo(0f);

                Assertions.assertThat(koPercentage)
                        .as("Outcome KO: KO% deve essere > 0")
                        .isGreaterThan(0f);

            } else {
                // RANDOM: assert minimo (E2E-safe): almeno una percentuale positiva
                boolean anyPositive = okPercentage > 0f || koPercentage > 0f;

                Assertions.assertThat(anyPositive)
                        .as("Outcome RANDOM: mi aspetto almeno una percentuale > 0 tra OK/KO")
                        .isTrue();
            }
        }

        // 2) Invarianti strutturali e sanity (sempre valide se ci sono elementi)
        for (TelemetryDataEserviceResponse t : actualTelemetry) {
            Assertions.assertThat(t)
                    .as("Elemento telemetry non deve essere null")
                    .isNotNull();

            Assertions.assertThat(t.getPerformances())
                    .as("performances non deve essere null")
                    .isNotNull();

            Assertions.assertThat(t.getFailures())
                    .as("failures non deve essere null")
                    .isNotNull();

            Assertions.assertThat(t.getPercentages())
                    .as("percentages non deve essere null")
                    .isNotNull();

            boolean hasNonNegativeResponseTime = t.getPerformances().stream()
                    .filter(Objects::nonNull)
                    .allMatch(p -> p.getResponseTime() >= 0);

            Assertions.assertThat(hasNonNegativeResponseTime)
                    .as("responseTime deve essere >= 0 quando presente")
                    .isTrue();

            // 3) Contenuto minimo: solo se probing enabled
            if (probingEnabled) {
                boolean hasAnySignal =
                        !t.getPerformances().isEmpty()
                                || !t.getFailures().isEmpty()
                                || !t.getPercentages().isEmpty();

                Assertions.assertThat(hasAnySignal)
                        .as("Con probing abilitato mi aspetto almeno un contenuto tra performances/failures/percentages")
                        .isTrue();
            }
        }
    }

    private void assertNotAdvancing(Duration observe, Duration tolerance, String message) throws Exception {
        Instant t1 = readLastResponseTime();
        TimeUnit.MILLISECONDS.sleep(observe.toMillis());
        Instant t2 = readLastResponseTime();

        long deltaMillis = t2.toEpochMilli() - t1.toEpochMilli(); // niente abs

        Assertions.assertThat(deltaMillis)
                .as(message + " (delta=" + deltaMillis + "ms, tolerance=" + tolerance.toMillis() + "ms)")
                .isBetween(0L, tolerance.toMillis()); // non deve diminuire, né avanzare troppo
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

    private void assertStopsAfterEnd(Duration unit, Duration tolerance, String message) throws Exception {
        // Fase 1: assorbi un eventuale update tardivo (1 unit)
        TimeUnit.MILLISECONDS.sleep(unit.toMillis());

        // Fase 2: ora deve essere stabile per 1 unit
        assertNotAdvancing(unit, tolerance, message);
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

        OffsetDateTime last = probingContext.getLastResponseTime();
        if (last == null) {
            // Nessuna response ancora ricevuta: trattiamo come "istant molto vecchio"
            return Instant.EPOCH;
        }

        return last.toInstant();
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
