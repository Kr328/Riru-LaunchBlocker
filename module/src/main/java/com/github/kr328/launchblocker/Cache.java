package com.github.kr328.launchblocker;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

final class Cache<K, V> {
    private Hashtable<K, Element<V>> caches = new Hashtable<>();

    V put(K key, V value, long expires) {
        Element<V> e = new Element<>();

        e.expires = expires;
        e.value = value;

        caches.put(key, e);

        this.clearExpired();

        return value;
    }

    V get(K key) {
        Element<V> e = caches.get(key);
        if (e == null)
            return null;

        if (System.currentTimeMillis() > e.expires) {
            caches.remove(key);
            return null;
        }

        return e.value;
    }

    V remove(K key) {
        Element<V> e = caches.remove(key);

        if (e == null)
            return null;

        return e.value;
    }

    private void clearExpired() {
        new ArrayList<K>(caches.keySet()).forEach(this::get);
    }

    private final static class Element<V> {
        long expires;
        V value;
    }
}
