package com.monoid.hackernews.common.data

actual class WeakHashMap<K, V> : MutableMap<K, V> {
    private val map: java.util.WeakHashMap<K, V> = java.util.WeakHashMap()

    actual override fun put(key: K, value: V): V? {
        return map.put(key, value)
    }

    actual override fun remove(key: K): V? {
        return map.remove(key)
    }

    actual override fun putAll(from: Map<out K, V>) {
        return map.putAll(from)
    }

    actual override fun clear() {
         map.clear()
    }

    actual override val keys: MutableSet<K>
        get() {
            return map.keys
        }
    actual override val values: MutableCollection<V>
        get() {
            return map.values
        }
    actual override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            return map.entries
        }

    actual override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    actual override fun containsKey(key: K): Boolean {
        return map.containsKey(key)
    }

    actual override fun containsValue(value: V): Boolean {
        return map.containsValue(value)
    }

    actual override fun get(key: K): V? {
        return map.get(key)
    }

    actual override val size: Int
        get() {
            return map.size
        }
}
