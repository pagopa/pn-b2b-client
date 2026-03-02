package it.pagopa.interop.cache;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CacheConcurrentMapImpl<K,V> implements Cache<K, V> {
    protected final ConcurrentMap<K,V> map;

    public CacheConcurrentMapImpl() {
        this(100);
    }

    public CacheConcurrentMapImpl(int initialCapacity) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(this.map.get(key));
    }

    @Override
    public void put(K key, V value) {
        this.map.put(key, value);
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return this.map.entrySet().iterator();
    }
}