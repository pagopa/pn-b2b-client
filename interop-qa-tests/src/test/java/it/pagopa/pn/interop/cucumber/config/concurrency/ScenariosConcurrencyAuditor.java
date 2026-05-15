package it.pagopa.pn.interop.cucumber.config.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ScenariosConcurrencyAuditor {

    public record ExecutionRecord(String scenarioName, long start, long end) {}

    // Lista thread-safe per lo storico
    private final List<ExecutionRecord> history = Collections.synchronizedList(new ArrayList<>());

    // Mappa per tracciare gli scenari attualmente in volo
    private final Map<String, Long> activeScenarios = new ConcurrentHashMap<>();

    /**
     * Registra l'inizio di uno scenario.
     * Include un controllo di sicurezza per nomi duplicati in esecuzione simultanea.
     */
    public void recordStart(String scenarioName) {
        long startTime = System.currentTimeMillis();
        if (activeScenarios.putIfAbsent(scenarioName, startTime) != null) {
            log.error("Lo scenario '{}' è già in esecuzione! " +
                    "Assicurati che i nomi degli scenari siano univoci.", scenarioName);
        }
    }

    /**
     * Chiude il record di esecuzione per lo scenario indicato.
     */
    public void recordEnd(String scenarioName) {
        Long start = activeScenarios.remove(scenarioName);
        if (start != null) {
            history.add(new ExecutionRecord(scenarioName, start, System.currentTimeMillis()));
        } else {
            log.warn("Tentativo di chiudere lo scenario '{}' senza una registrazione di inizio.", scenarioName);
        }
    }

    /**
     * Restituisce una copia ordinata dello storico.
     * La sincronizzazione garantisce l'integrità durante l'iterazione dello stream.
     */
    public List<ExecutionRecord> getSortedHistory() {
        synchronized (history) {
            return history.stream()
                    .sorted(Comparator.comparingLong(ExecutionRecord::start))
                    .toList();
        }
    }
}