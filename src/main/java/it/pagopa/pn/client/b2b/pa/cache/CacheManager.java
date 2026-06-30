package it.pagopa.pn.client.b2b.pa.cache;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * CacheManager è una semplice implementazione di cache in-memory con TTL (Time To Live)
 * Thread-safe e adatta per caching di risultati di chiamate a servizi esterni o computazioni costose
 * Uso:
 * CacheManager<String, String> cache = new CacheManager<>("MyCache", 300); // TTL di 5 minuti
 * String value = cache.getOrCompute("key1", () -> expensiveComputation());
 *
 * @param <K> Tipo della chiave
 * @param <V> Tipo del valore
 */
@Slf4j
public class CacheManager<K, V> {

    /**
     * Costante per indicare TTL infinito (non scade mai)
     * Usare questa costante quando vuoi che la cache non scada mai durante l'esecuzione
     */
    public static final long INFINITE_TTL = -1L;

    private final String cacheName;
    private final long defaultTtlMillis;
    private final Map<K, CacheEntry<V>> cache;

    /**
     * Costruttore con nome e TTL di default
     * @param cacheName Nome della cache per logging/monitoring
     * @param ttlSeconds TTL di default in secondi. Usa CacheManager.INFINITE_TTL per non scadere mai
     */
    public CacheManager(String cacheName, long ttlSeconds) {
        this.cacheName = cacheName;

        // Se ttlSeconds è INFINITE_TTL (-1), non convertire a millisecondi
        // Altrimenti converti secondi in millisecondi
        this.defaultTtlMillis = (ttlSeconds == INFINITE_TTL) ? INFINITE_TTL : ttlSeconds * 1000;

        this.cache = new ConcurrentHashMap<>();

        String ttlInfo = (ttlSeconds == INFINITE_TTL) ? "INFINITE" : (ttlSeconds + "s");
        log.info("CacheManager initialized: name='{}', ttl={}", cacheName, ttlInfo);
    }

    /**
     * Recupera un valore dalla cache, oppure lo computa tramite supplier
     * Thread-safe: due thread che richiedono lo stesso key non competeranno inutilmente
     *
     * @param key Chiave per identificare il valore
     * @param valueSupplier Supplier che fornisce il valore se non in cache
     * @return Valore dalla cache o computato
     */
    public V getOrCompute(K key, Supplier<V> valueSupplier) {
        return getOrCompute(key, valueSupplier, defaultTtlMillis);
    }

    /**
     * Versione con TTL personalizzato per questa specifica computazione
     * Utile quando vuoi un TTL diverso dal default per alcune chiavi
     *
     * @param key Chiave per identificare il valore
     * @param valueSupplier Supplier che fornisce il valore se non in cache
     * @param ttlMillis TTL in millisecondi per questa entry specifica (usa INFINITE_TTL per non scadere)
     * @return Valore dalla cache o computato
     */
    public V getOrCompute(K key, Supplier<V> valueSupplier, long ttlMillis) {
        CacheEntry<V> entry = cache.compute(key, (k, existingEntry) -> {
            if (existingEntry != null && existingEntry.isValid()) {
                // Cache hit, se l'entry esiste e non è scaduta, restituisci l'entry esistente
                log.debug("[{}] Cache HIT for key: {} (remaining: {}ms)",
                        cacheName, k, existingEntry.getTimeRemainingMillis());
                return existingEntry;
            }
            // Cache miss o scaduta, calcola il nuovo valore
            log.info("[{}] Cache MISS for key: {} - computing value", cacheName, k);
            V newValue = valueSupplier.get();
            if (newValue == null) {
                log.warn("[{}] Computed value for key: {} is null, not caching", cacheName, k);
                return null; // Non cacheare valori null, ma restituisci comunque null
            }
            log.debug("[{}] Cached new value for key: {}", cacheName, k);
            return new CacheEntry<>(k.toString(), newValue, ttlMillis);
        });
        return (entry != null) ? entry.getValue() : null;
    }

    /**
     * Recupera un valore trasformando la chiave con una funzione
     * Utile per chiavi composite
     */
    public V getOrCompute(K key, Function<K, V> computeFunction) {
        return getOrCompute(key, () -> computeFunction.apply(key));
    }

    /**
     * Put diretto in cache (utile per precaching)
     */
    public void put(K key, V value) {
        put(key, value, defaultTtlMillis);
    }

    /**
     * Put con TTL personalizzato
     */
    public void put(K key, V value, long ttlMillis) {
        cache.put(key, new CacheEntry<>(key.toString(), value, ttlMillis));
        log.debug("[{}] Put value for key: {}", cacheName, key);
    }

    /**
     * Recupera un valore dalla cache senza computare
     * Restituisce null se non presente o scaduto
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if(entry != null) {
            if(entry.isValid()) {
                return entry.getValue();
            } else {
                // Rimuovi l'entry scaduta per evitare accumulo di chiavi scadute
                cache.remove(key, entry);
                log.debug("[{}] Removed expired entry for key: {}", cacheName, key);
            }
        }
        return null;
    }

    /**
     * Invalida una entry specifica
     */
    public void invalidate(K key) {
        cache.remove(key);
        log.debug("[{}] Invalidated cache entry for key: {}", cacheName, key);
    }

    /**
     * Invalida tutte le entry scadute
     */
    public void cleanupExpired() {
        int removedCount = (int) cache.entrySet().stream()
                .filter(e -> e.getValue().isExpired())
                .map(Map.Entry::getKey)
                .peek(key -> cache.remove(key))
                .count();

        if (removedCount > 0) {
            log.debug("[{}] Cleaned up {} expired entries", cacheName, removedCount);
        }
    }

    /**
     * Invalida tutta la cache
     */
    public void invalidateAll() {
        int size = cache.size();
        cache.clear();
        log.info("[{}] Invalidated all {} cache entries", cacheName, size);
    }

    /**
     * Restituisce il numero di entry valide (non scadute)
     */
    public int getValidEntryCount() {
        cleanupExpired();
        return cache.size();
    }

    /**
     * Restituisce il numero totale di entry (incluse scadute)
     */
    public int getTotalEntryCount() {
        return cache.size();
    }

    /**
     * Restituisce il numero di entry scadute
     */
    public int getExpiredEntryCount() {
        return (int) cache.values().stream()
                .filter(CacheEntry::isExpired)
                .count();
    }
}
