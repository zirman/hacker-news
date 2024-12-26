package com.monoid.hackernews.common.data

// TODO
actual class WeakHashMap<K, V> : MutableMap<K, V> {
    actual override fun put(key: K, value: V): V? {
        TODO()
    }

    actual override fun remove(key: K): V? {
        TODO()
    }

    actual override fun putAll(from: Map<out K, V>) {
        TODO()
    }

    actual override fun clear() {
        TODO()
    }

    actual override val keys: MutableSet<K>
        get() {
            TODO()
        }
    actual override val values: MutableCollection<V>
        get() {
            TODO()
        }
    actual override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            TODO()
        }

    actual override fun isEmpty(): Boolean {
        TODO()
    }

    actual override fun containsKey(key: K): Boolean {
        TODO()
    }

    actual override fun containsValue(value: V): Boolean {
        TODO()
    }

    actual override fun get(key: K): V? {
        TODO()
    }

    actual override val size: Int
        get() {
            TODO()
        }
}
