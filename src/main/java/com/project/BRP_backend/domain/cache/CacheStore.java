package com.project.BRP_backend.domain.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class CacheStore<K, V> {
    private final Cache<K, V> cache;

    public CacheStore(int expiryDuration, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryDuration,timeUnit)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    public V get(K key) {
        return cache.getIfPresent(key);
    }
    public void put(K key, V value) {
        cache.put(key, value);
    }
    public void evict(K key) {
        cache.invalidate(key);
    }
}
