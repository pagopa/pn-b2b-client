package it.pagopa.pn.interop.cucumber.config.concurrency;

import io.cucumber.java.Scenario;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Gestore centralizzato per la concorrenza degli scenari.
 * Permette di definire vincoli di esecuzione basati su predicati applicati agli scenari.
 */
@EqualsAndHashCode
public class ScenariosConcurrencyManager {

    // Mappa dei semafori: ID -> Semaforo (gestisce il numero di posti disponibili)
    private final Map<String, Semaphore> locks = new ConcurrentHashMap<>();

    // Mappa dei criteri: ID -> Predicato (definisce a quali scenari si applica il lock)
    private final Map<String, Predicate<Scenario>> criteria = new ConcurrentHashMap<>();

    private static final int MINUMUM_CONCURRENCY_LEVEL = 1;

    public void register(String id, String tag, int concurrencyLevel) {
        this.register(id, sc -> sc.getSourceTagNames().contains(tag), concurrencyLevel);
    }

    /**
     * Registra un nuovo meccanismo di locking.
     * @param id Identificativo univoco del lock.
     * @param scenariosCriteria Predicato per identificare gli scenari soggetti a questo lock.
     * @param concurrencyLevel Numero massimo di esecuzioni contemporanee ammesse (> 0).
     */
    public void register(String id, Predicate<Scenario> scenariosCriteria, int concurrencyLevel) {
        if (concurrencyLevel < MINUMUM_CONCURRENCY_LEVEL) {
            throw new IllegalArgumentException("Il livello di concorrenza deve essere >= " + MINUMUM_CONCURRENCY_LEVEL);
        }
        if (locks.containsKey(id)) {
            throw new IllegalArgumentException("L'id '%s' è già gestito, indicane uno diverso oppure verificane la correttezza".formatted(id));
        }

        criteria.put(id, scenariosCriteria);
        locks.put(id, new Semaphore(concurrencyLevel));
    }

    /**
     * Rimuove un meccanismo di locking registrato.
     */
    public void unregister(String id) {
        criteria.remove(id);
        locks.remove(id);
    }

    /**
     * Identifica e acquisisce tutti i lock necessari per lo scenario fornito.
     * I lock vengono acquisiti in ordine alfabetico per prevenire Deadlock.
     *
     * @param scenario Lo scenario Cucumber corrente.
     * @return Una lista di ID dei lock effettivamente acquisiti.
     */
    public List<String> acquireLocksFor(Scenario scenario) {
        // 1. Trova tutti gli ID i cui predicati corrispondono allo scenario
        List<String> matchingIds = criteria.entrySet().stream()
                .filter(entry -> entry.getValue().test(scenario))
                .map(Map.Entry::getKey)
                .sorted() // FONDAMENTALE: Previene Deadlock ordinando le risorse
                .collect(Collectors.toList());

        // 2. Tenta l'acquisizione seriale di ogni lock identificato
        for (String id : matchingIds) {
            Semaphore semaphore = locks.get(id);
            if (semaphore != null) {
                // Resta bloccato finché non si libera un posto nel semaforo
                semaphore.acquireUninterruptibly();
            }
        }

        return matchingIds;
    }

    /**
     * Rilascia i lock precedentemente acquisiti.
     * @param acquiredIds Lista degli ID restituiti da acquireLocksFor.
     */
    public void releaseLocks(List<String> acquiredIds) {
        if (acquiredIds == null || acquiredIds.isEmpty()) return;

        // Rilasciamo i lock in ordine inverso rispetto all'acquisizione
        for (int i = acquiredIds.size() - 1; i >= 0; i--) {
            String id = acquiredIds.get(i);
            Semaphore semaphore = locks.get(id);
            if (semaphore != null) {
                semaphore.release();
            }
        }
    }
}