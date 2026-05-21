package it.pagopa.pn.client.b2b.pa.cache;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Generica cache manager thread-safe con supporto a TTL
 * Può essere utilizzata per cachare qualsiasi tipo di dato
 *
 * Uso:
 * CacheManager<String, String> cache = new CacheManager<>("paCache", 300_000); // 5 min TTL
 * String value = cache.getOrCompute("key", () -> expensiveOperation());
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
     * Recupera un valore dalla cache con TTL personalizzato
     *
     * @param key Chiave per identificare il valore
     * @param valueSupplier Supplier che fornisce il valore se non in cache
     * @param ttlMillis TTL personalizzato in millisecondi
     * @return Valore dalla cache o computato
     */
    public V getOrCompute(K key, Supplier<V> valueSupplier, long ttlMillis) {
        CacheEntry<V> entry = cache.get(key);

        // Cache hit se entry esiste e non è scaduta
        if (entry != null && entry.isValid()) {
            log.debug("[{}] Cache HIT for key: {} (remaining: {}ms)",
                    cacheName, key, entry.getTimeRemainingMillis());
            return entry.getValue();
        }

        // Cache miss: calcola il valore
        log.debug("[{}] Cache MISS for key: {} - computing value", cacheName, key);

        V value = valueSupplier.get();
        CacheEntry<V> newEntry = new CacheEntry<>(key.toString(), value, ttlMillis);
        cache.put(key, newEntry);

        log.debug("[{}] Cached new value for key: {}", cacheName, key);
        return value;
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
     * Get diretto (restituisce null se non trovato o scaduto)
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && entry.isValid()) {
            return entry.getValue();
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
