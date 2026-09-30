package it.pagopa.pn.client.b2b.pa.cache;

import lombok.Getter;

/**
 * Wrapper generico per un'entry in cache con supporto a TTL
 * @param <T> Tipo del valore cachato
 */
@Getter
public class CacheEntry<T> {
    private final String key;
    private final T value;
    private final long createdAt;
    private final long ttlMillis;

    public CacheEntry(String key, T value, long ttlMillis) {
        this.key = key;
        this.value = value;
        this.ttlMillis = ttlMillis;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Verifica se l'entry è scaduta
     * Se ttlMillis è -1 (INFINITE_TTL), non scade mai
     */
    public boolean isExpired() {
        // TTL infinito (-1) significa che non scade mai
        if (ttlMillis == CacheManager.INFINITE_TTL) {
            return false;
        }
        return (System.currentTimeMillis() - createdAt) > ttlMillis;
    }

    /**
     * Restituisce il tempo rimanente in millisecondi
     * Se TTL è infinito, ritorna Long.MAX_VALUE
     */
    public long getTimeRemainingMillis() {
        if (ttlMillis == CacheManager.INFINITE_TTL) {
            return Long.MAX_VALUE;
        }
        long remaining = ttlMillis - (System.currentTimeMillis() - createdAt);
        return Math.max(0, remaining);
    }

    /**
     * Verifica se l'entry è ancora valida
     */
    public boolean isValid() {
        return !isExpired();
    }
}
