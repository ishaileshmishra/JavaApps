package com.shaileshmishra.app.practice;

import java.util.LinkedHashMap;
import java.util.Map;

public class LeastRecentUsedCacheExample<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LeastRecentUsedCacheExample(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    public static void main(String[] args) {
        LeastRecentUsedCacheExample<Integer, String> cache = new LeastRecentUsedCacheExample<>(3);
        // since capacity is 3, the cache can hold a maximum of 3 entries. When a new entry is added and the cache is full, the least recently used entry will be removed.
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        System.out.println(cache);
        // After addding 4 as a key, the least recently used key (2) will be removed from the cache
        cache.put(4, "Four");
        System.out.println(cache);
    }

}
