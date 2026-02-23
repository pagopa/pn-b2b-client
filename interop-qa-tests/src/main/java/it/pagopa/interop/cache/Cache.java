package it.pagopa.interop.cache;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public interface Cache<K, V> extends Iterable<Map.Entry<K, V>> {
    Optional<V> get(K key);

    void put(K key, V value);

    default void putAll(Map<K, V> map) {
        map.forEach(this::put);
    }

    default Optional<V> find(Predicate<V> searchPredicate) {
        return StreamSupport.stream(this.spliterator(), false)
            .map(Map.Entry::getValue)
            .filter(searchPredicate)
            .findFirst();
    }
}
