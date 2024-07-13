package ru.job4j.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCache<K, V> {

    private final Map<K, SoftReference<V>> cache = new HashMap<>();

    public final void put(K key, V value) {
        SoftReference<V> softValue = new SoftReference<>(value);
        cache.put(key, softValue);
    }

    public final V get(K key) {
        V result = cache.get(key).get();
        if (result == null) {
            load(key);
            result = cache.get(key).get();
        }
        return result;
    }

    protected abstract V load(K key);
}