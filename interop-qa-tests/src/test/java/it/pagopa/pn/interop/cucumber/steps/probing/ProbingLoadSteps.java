package it.pagopa.pn.interop.cucumber.steps.probing;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.probing.model.ChangeProbingStateRequest;
import it.pagopa.interop.generated.openapi.clients.probing.model.ProbingDataEserviceResponse;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.EserviceRow;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.dateTimeOrNull;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.durationOrNull;

@Slf4j
public class ProbingLoadSteps {
    private final ProbingClient probingClient;

    private int totalEservices;
    private int workers;

    private int frequencyMinutes;
    private String startDateExpr;
    private String endDateExpr;

    private int waitPeriods;          // quanti periodi aspettare
    private Duration extraWait;       // buffer oltre ai periodi
    private Duration recentTolerance; // tolleranza

    // calcolati
    private Duration waitDuration;

    // Timestamp riferimento: quando abilito
    private Instant enableTimeUtc;

    public ProbingLoadSteps(ProbingClient probingClient, SharedStepsContext sharedStepsContext) {
        this.probingClient = probingClient;
        this.probingClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @Given("preparo il load test probing con:")
    public void setup(DataTable table) {
        Map<String, String> row = table.asMaps().get(0);

        totalEservices = Integer.parseInt(row.get("totalEservices"));
        workers = Integer.parseInt(row.get("workers"));

        frequencyMinutes = Integer.parseInt(row.get("frequency"));
        startDateExpr = row.get("startDate");
        endDateExpr = row.get("endDate");

        waitPeriods = Integer.parseInt(row.get("waitPeriods"));
        extraWait = durationOrNull(row.get("extraWait"));
        recentTolerance = durationOrNull(row.get("recentTolerance"));

        Assertions.assertThat(totalEservices).as("totalEservices").isGreaterThan(0);
        Assertions.assertThat(workers).as("workers").isGreaterThan(0);

        Assertions.assertThat(frequencyMinutes).as("frequency").isGreaterThanOrEqualTo(1);
        Assertions.assertThat(waitPeriods).as("waitPeriods").isGreaterThanOrEqualTo(1);
        Assertions.assertThat(extraWait).as("extraWait").isNotNull();
        Assertions.assertThat(recentTolerance).as("recentTolerance").isNotNull();

        // Attesa = N * frequency + buffer
        waitDuration = Duration.ofMinutes((long) frequencyMinutes * (long) waitPeriods).plus(extraWait);

        log.info("LOAD setup: total={}, workers={}, frequency={}m, window=[{}, {}], waitPeriods={}, extraWait={}, waitDuration={}, recentTolerance={}",
                totalEservices, workers, frequencyMinutes, startDateExpr, endDateExpr, waitPeriods, extraWait, waitDuration, recentTolerance
        );
    }

    @When("aggiorno scheduling in parallelo per tutti gli eservice")
    public void updateSchedulingParallel() {
        OffsetDateTime startUtc = dateTimeOrNull(startDateExpr);
        OffsetDateTime endUtc = dateTimeOrNull(endDateExpr);

        Assertions.assertThat(endUtc).as("endDate deve essere dopo startDate").isAfter(startUtc);

        runParallelRange(totalEservices, idx -> {
            EserviceRow row = EserviceRow.atIndex(idx);

            probingClient.updateEserviceFrequency(row.getEserviceId(), row.getVersionId(), frequencyMinutes, startUtc.toOffsetTime(), endUtc.toOffsetTime());
        }, "updateScheduling");
    }

    @And("abilito probing in parallelo per tutti gli eservice")
    public void enableParallel() {
        // momento a partire dal quale mi aspetto aggiornamenti
        enableTimeUtc = Instant.now();

        runParallelRange(totalEservices, idx -> {
            EserviceRow row = EserviceRow.atIndex(idx);

            ChangeProbingStateRequest req = new ChangeProbingStateRequest().probingEnabled(true);
            probingClient.updateEserviceProbingState(row.getEserviceId(), row.getVersionId(), req);
        }, "enableProbing");

        log.info("Enable completato. enableTimeUtc={}", enableTimeUtc);
    }

    @And("attendo N periodi di frequency più extraWait")
    public void waitNPeriodsPlusExtra() {
        log.info("Attendo {}", waitDuration);
        sleep(waitDuration);
    }

    @Then("verifico in parallelo che responseReceived sia valorizzata e aggiornata dopo l'enable per tutti gli eservice")
    public void verifyAllUpdatedAfterEnable() {
        Assertions.assertThat(enableTimeUtc).as("enableTimeUtc").isNotNull();

        Instant minAllowed = enableTimeUtc.minus(recentTolerance);
        log.info("Verifica: responseReceived deve essere >= {} (enableTimeUtc={} minus tolerance={})",
                minAllowed, enableTimeUtc, recentTolerance
        );

        ConcurrentLinkedQueue<String> notOk = new ConcurrentLinkedQueue<>();

        // Una sola GET per e-service
        runParallelRange(totalEservices, idx -> {
            Instant last = readLastResponseInstant(idx);

            if (last == null) {
                notOk.add("idx=" + idx + " responseReceived=null");
                return;
            }

            if (last.isBefore(minAllowed)) {
                notOk.add("idx=" + idx + " responseReceived=" + last + " < minAllowed=" + minAllowed);
            }
        }, "verifyAllOnce");

        if (!notOk.isEmpty()) {
            List<String> top = notOk.stream().limit(30).toList();
            throw new AssertionError(
                    "Verifica fallita: " + notOk.size() + "/" + totalEservices + " eservice non aggiornati dopo l'enable.\n" +
                            "Parametri: frequency=" + frequencyMinutes + "m, waitPeriods=" + waitPeriods + ", extraWait=" + extraWait +
                            ", waitDuration=" + waitDuration + ", tolerance=" + recentTolerance + "\n" +
                            "Esempi (max 30):\n" + String.join("\n", top)
            );
        }

        log.info("OK: tutti gli eservice risultano aggiornati dopo l'enable.");
    }

    @And("disabilito probing in parallelo per tutti gli eservice")
    public void disableParallel() {
        runParallelRange(totalEservices, idx -> {
            EserviceRow row = EserviceRow.atIndex(idx);

            UUID eserviceId = row.getEserviceId();
            UUID versionId = row.getVersionId();

            ChangeProbingStateRequest req = new ChangeProbingStateRequest().probingEnabled(false);
            probingClient.updateEserviceProbingState(eserviceId, versionId, req);
        }, "disableProbing");
    }

    private Instant readLastResponseInstant(int idx) {
        EserviceRow row = EserviceRow.atIndex(idx);

        Long recordId = row.getId();
        ProbingDataEserviceResponse resp = probingClient.getEserviceProbingData(recordId);
        Assertions.assertThat(resp).as("ProbingDataEserviceResponse").isNotNull();

        String rr = resp.getResponseReceived();
        if (rr == null || rr.isBlank()) return null;

        // 1) UTC standard
        try {
            return Instant.parse(rr);
        } catch (Exception ignored) {
        }
        try {
            return OffsetDateTime.parse(rr).toInstant();
        } catch (Exception ignored) {
        }

        // 2) fallback legacy (solo orario)
        try {
            OffsetTime ot = OffsetTime.parse(rr);
            return ot.atDate(LocalDate.now()).toInstant();
        } catch (Exception e) {
            throw new IllegalArgumentException("responseReceived non parsabile: '" + rr + "'", e);
        }
    }

    private void runParallelRange(int total, ThrowingIntConsumer fn, String phase) {
        ExecutorService pool = Executors.newFixedThreadPool(workers);
        try {
            LongAdder ok = new LongAdder();
            ConcurrentLinkedQueue<String> errors = new ConcurrentLinkedQueue<>();
            List<Future<?>> futures = new ArrayList<>(total);

            long start = System.nanoTime();

            for (int i = 0; i < total; i++) {
                final int idx = i;
                futures.add(pool.submit(() -> {
                    try {
                        fn.accept(idx);
                        ok.increment();
                    } catch (Exception e) {
                        errors.add("idx=" + idx + " -> " + e.getMessage());
                    }
                }));
            }

            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (Exception e) {
                    errors.add("future -> " + e.getMessage());
                }
            }

            long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            log.info("Phase '{}' completata: {} ok / {} tot in {}ms (workers={})",
                    phase, ok.sum(), total, ms, workers
            );

            if (!errors.isEmpty()) {
                List<String> top = errors.stream().limit(30).toList();
                throw new AssertionError(
                        "Phase '" + phase + "' fallita: errors=" + errors.size() + ", ok=" + ok.sum() + "/" + total + "\n" +
                                "Esempi (max 30):\n" + String.join("\n", top)
                );
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @FunctionalInterface
    private interface ThrowingIntConsumer {
        void accept(int value) throws Exception;
    }

    private static void sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException ignored) {
        }
    }
}
