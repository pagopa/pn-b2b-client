package it.pagopa.pn.interop.cucumber.config.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ScenariosConcurrencyAuditor {

    public record ExecutionRecord(String scenarioName, long start, long end) {}

    private final List<ExecutionRecord> history = Collections.synchronizedList(new ArrayList<>());

    // Usiamo il nome dello scenario come chiave per tracciare l'inizio
    private final Map<String, Long> activeScenarios = new ConcurrentHashMap<>();

    public void recordStart(String scenarioName) {
        // Registriamo l'istante iniziale associato a quel nome specifico
        activeScenarios.put(scenarioName, System.currentTimeMillis());
    }

    public void recordEnd(String scenarioName) {
        Long start = activeScenarios.remove(scenarioName);
        if (start != null) {
            history.add(new ExecutionRecord(scenarioName, start, System.currentTimeMillis()));
        } else {
            log.warn("recordEnd chiamato per scenario mai iniziato: {}", scenarioName);
        }
    }

    public List<ExecutionRecord> getSortedHistory() {
        synchronized (history) {
            return history.stream()
                    .sorted(Comparator.comparingLong(ExecutionRecord::start))
                    .toList();
        }
    }
}