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
 * I partecipanti sono quelli passati a {@link #configureSuiteScenarios} / {@link #configureParallelSuite}
 * nello static initializer del runner (una sola volta).
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
        if (suiteConfiguredScenarioIds.isEmpty()) {
            return true;
        }
        for (String id : suiteConfiguredScenarioIds) {
            ParallelScenarioPhase phase = scenarioStates.get(id);
            if (phase == null || phase.ordinal() < min.ordinal()) {
                return false;
            }
        }
        return true;
    }

    private void requireKnown(String scenarioId) {
        if (!suiteConfiguredScenarioIds.contains(scenarioId)) {
            throw new IllegalStateException(
                    "Scenario parallelo non in suite: " + scenarioId
                            + " (configurati=" + suiteConfiguredScenarioIds + ")");
        }
    }
}
