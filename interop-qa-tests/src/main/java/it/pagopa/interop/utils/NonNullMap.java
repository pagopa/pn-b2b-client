package it.pagopa.interop.utils;

import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** A {@link Map} that does not allow null values. Every put operation will be ignored if the value is null.
 * Methods like {@link #putAll(Map)} will put only the non-null values.
 */
public class NonNullMap<K, V> extends HashMap<K, V> {

    public NonNullMap(int initialCapacity) {
        super(initialCapacity);
    }

    public NonNullMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public NonNullMap() {
        super();
    }

    public NonNullMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m.entrySet().stream()
            .filter(e -> nonNull(e.getValue()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    }

    @Override
    public V put(K key, V value) {
        return nonNull(value) ? super.put(key, value) : null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return nonNull(value) ? super.putIfAbsent(key, value) : null;
    }
}
