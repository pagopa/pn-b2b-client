package it.pagopa.pn.cucumber.steps.delayer.model;

import it.pagopa.pn.cucumber.steps.delayer.model.enums.ParallelScenarioPhase;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.cucumber.junit.platform.engine.Constants.PARALLEL_CONFIG_FIXED_PARALLELISM_PROPERTY_NAME;

/**
 * Contesto di suite per gate tra scenari {@code @delayerParallel}.
 * Traccia fasi per scenarioId e ARN condivisi di Batch / Phase2.
 * La pulizia tabelle e la registrazione partecipanti avvengono una tantum nel {@code @BeforeAll}.
 */
@Component
public class DelayerSuiteContext {

    public static final Duration GATE_TIMEOUT = Duration.ofMinutes(45);

    private static final Pattern SCENARIO_ID_IN_TITLE = Pattern.compile("^\\[([^\\]]+)]");

    private static volatile List<String> suiteConfiguredScenarioIds = List.of();

    private final Map<String, ParallelScenarioPhase> scenarioStates = new ConcurrentHashMap<>();

    public String batchExecutionArn;
    public String phase2ExecutionArn;

    public String extractScenarioId(String scenarioName) {
        Matcher matcher = SCENARIO_ID_IN_TITLE.matcher(scenarioName.trim());
        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Scenario @delayerParallel senza id [..] all'inizio del titolo: " + scenarioName);
        }
        return matcher.group(1);
    }

    public static boolean isSuiteConfigured() {
        return !suiteConfiguredScenarioIds.isEmpty();
    }

    public static void configureSuiteScenarios(String... scenarioIds) {
        suiteConfiguredScenarioIds = List.of(scenarioIds);
    }

    public static void configureParallelSuite(String... scenarioIds) {
        configureSuiteScenarios(scenarioIds);
        System.setProperty(PARALLEL_CONFIG_FIXED_PARALLELISM_PROPERTY_NAME, Integer.toString(scenarioIds.length));
    }

    public synchronized void registerScenariosFromSuite() {
        if (!scenarioStates.isEmpty()) {
            return;
        }
        if (suiteConfiguredScenarioIds.isEmpty()) {
            throw new IllegalStateException(
                    "Nessun scenarioId configurato: chiamare DelayerSuiteContext.configureSuiteScenarios(...) "
                            + "nello static initializer del runner (es. DelayerParallelTest / Delayer1Test)");
        }
        batchExecutionArn = null;
        phase2ExecutionArn = null;
        for (String id : suiteConfiguredScenarioIds) {
            if (id == null || id.isBlank()) {
                continue;
            }
            scenarioStates.put(id.trim(), ParallelScenarioPhase.REGISTERED);
        }
    }

    public void advance(String scenarioId, ParallelScenarioPhase phase) {
        if (scenarioId == null) {
            return;
        }
        requireKnown(scenarioId);
        scenarioStates.put(scenarioId, phase);
        synchronized (this) {
            notifyAll();
        }
    }

    public void awaitAllAtLeast(ParallelScenarioPhase min, Duration timeout) throws InterruptedException {
        long deadlineNs = System.nanoTime() + timeout.toNanos();
        synchronized (this) {
            while (!allAtLeast(min)) {
                long remainingMs = (deadlineNs - System.nanoTime()) / 1_000_000L;
                if (remainingMs <= 0) {
                    throw new IllegalStateException(
                            "Timeout gate " + min + " — stati=" + scenarioStates);
                }
                wait(Math.min(remainingMs, 1000L));
            }
        }
    }

    private boolean allAtLeast(ParallelScenarioPhase min) {
        if (scenarioStates.isEmpty()) {
            return true;
        }
        for (ParallelScenarioPhase phase : scenarioStates.values()) {
            if (phase == null || phase.ordinal() < min.ordinal()) {
                return false;
            }
        }
        return true;
    }

    private void requireKnown(String scenarioId) {
        if (!scenarioStates.containsKey(scenarioId)) {
            throw new IllegalStateException(
                    "Scenario parallelo non registrato: " + scenarioId + " (stati=" + scenarioStates + ")");
        }
    }
}
