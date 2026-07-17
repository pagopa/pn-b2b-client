package it.pagopa.pn.cucumber.steps.delayer.model;

import it.pagopa.pn.cucumber.steps.delayer.model.enums.ParallelScenarioPhase;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.cucumber.junit.platform.engine.Constants.PARALLEL_CONFIG_FIXED_PARALLELISM_PROPERTY_NAME;

/**
 * Contesto di suite per gate tra scenari {@code @delayerParallel}.
 * <p>
 * Gli id partecipanti stanno in una {@link System#getProperty system property}
 * (visibile in tutta la JVM): un campo {@code static} sul bean non basta perché
 * il classloader della suite JUnit e quello del glue Cucumber possono essere diversi.
 */
@Component
public class DelayerSuiteContext {

    public static final Duration GATE_TIMEOUT = Duration.ofMinutes(45);

    /** Property JVM con gli id della suite, separati da virgola. */
    static final String SCENARIO_IDS_PROPERTY = "pn.delayer.suite.scenarioIds";

    private static final Pattern SCENARIO_ID_IN_TITLE = Pattern.compile("^\\[([^\\]]+)]");

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
        return !configuredScenarioIds().isEmpty();
    }

    public static void configureSuiteScenarios(String... scenarioIds) {
        if (scenarioIds == null || scenarioIds.length == 0) {
            System.clearProperty(SCENARIO_IDS_PROPERTY);
            return;
        }
        System.setProperty(SCENARIO_IDS_PROPERTY, String.join(",", scenarioIds));
    }

    public static void configureParallelSuite(String... scenarioIds) {
        configureSuiteScenarios(scenarioIds);
        System.setProperty(PARALLEL_CONFIG_FIXED_PARALLELISM_PROPERTY_NAME, Integer.toString(scenarioIds.length));
    }

    static List<String> configuredScenarioIds() {
        String raw = System.getProperty(SCENARIO_IDS_PROPERTY);
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
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
                            "Timeout gate " + min + " — stati=" + scenarioStates
                                    + " configurati=" + configuredScenarioIds());
                }
                wait(Math.min(remainingMs, 1000L));
            }
        }
    }

    private boolean allAtLeast(ParallelScenarioPhase min) {
        List<String> configured = configuredScenarioIds();
        if (configured.isEmpty()) {
            return true;
        }
        for (String id : configured) {
            ParallelScenarioPhase phase = scenarioStates.get(id);
            if (phase == null || phase.ordinal() < min.ordinal()) {
                return false;
            }
        }
        return true;
    }

    private void requireKnown(String scenarioId) {
        List<String> configured = configuredScenarioIds();
        if (!configured.contains(scenarioId)) {
            throw new IllegalStateException(
                    "Scenario parallelo non in suite: " + scenarioId
                            + " (configurati=" + configured + ")");
        }
    }
}
