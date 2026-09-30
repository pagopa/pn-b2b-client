package it.pagopa.pn.cucumber.steps.delayer.model;

import it.pagopa.pn.cucumber.steps.delayer.model.enums.ParallelScenarioPhase;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gate tra scenari Delayer paralleli (TC1–TC5).
 * Gli id si configurano una tantum da {@code DelayerParallelSuiteHooks}.
 */
@Component
public class DelayerSuiteContext {

    public static final Duration GATE_TIMEOUT = Duration.ofMinutes(45);

    /** Chiave {@code @ConfigurationParameter} sui runner (es. DelayerParallelTest). */
    public static final String SCENARIO_IDS_PROPERTY = "pn.delayer.suite.scenarioIds";

    private static final Pattern SCENARIO_ID_IN_TITLE = Pattern.compile("^\\[([^\\]]+)]");

    private static volatile List<String> configuredIds = List.of();

    private final Map<String, ParallelScenarioPhase> scenarioStates = new ConcurrentHashMap<>();

    public String batchExecutionArn;
    public String phase2ExecutionArn;

    public String extractScenarioId(String scenarioName) {
        Matcher matcher = SCENARIO_ID_IN_TITLE.matcher(scenarioName.trim());
        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Scenario Delayer parallelo senza id [..] all'inizio del titolo: " + scenarioName);
        }
        return matcher.group(1);
    }

    public static boolean isSuiteConfigured() {
        return !configuredIds.isEmpty();
    }

    /** Id attesi dal gate (uno o più). */
    public static void configure(String... scenarioIds) {
        configuredIds = (scenarioIds == null || scenarioIds.length == 0)
                ? List.of()
                : List.of(scenarioIds);
    }

    public void advance(String scenarioId, ParallelScenarioPhase phase) {
        if (scenarioId == null) {
            return;
        }
        if (isSuiteConfigured() && !configuredIds.contains(scenarioId)) {
            throw new IllegalStateException(
                    "Scenario parallelo non in suite: " + scenarioId + " (configurati=" + configuredIds + ")");
        }
        scenarioStates.put(scenarioId, phase);
        synchronized (this) {
            notifyAll();
        }
    }

    public void awaitAllAtLeast(ParallelScenarioPhase min, Duration timeout) throws InterruptedException {
        if (!isSuiteConfigured()) {
            return;
        }
        long deadlineNs = System.nanoTime() + timeout.toNanos();
        synchronized (this) {
            while (!allAtLeast(min)) {
                long remainingMs = (deadlineNs - System.nanoTime()) / 1_000_000L;
                if (remainingMs <= 0) {
                    throw new IllegalStateException(
                            "Timeout gate " + min + " — stati=" + scenarioStates + " configurati=" + configuredIds);
                }
                wait(Math.min(remainingMs, 1000L));
            }
        }
    }

    private boolean allAtLeast(ParallelScenarioPhase min) {
        for (String id : configuredIds) {
            ParallelScenarioPhase phase = scenarioStates.get(id);
            if (phase == null || phase.ordinal() < min.ordinal()) {
                return false;
            }
        }
        return true;
    }
}
