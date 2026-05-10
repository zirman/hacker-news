@file:OptIn(ExperimentalWasmJsInterop::class)

package com.monoid.hackernews.common.data

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsBoolean
import kotlin.js.js
import kotlin.js.toBoolean
import kotlin.js.toJsReference

private fun newWeakMap(): JsAny = js("new WeakMap()")
private fun set(wm: JsAny, key: JsAny, value: JsAny): JsAny = js("wm.set(key, value)")
private fun get(wm: JsAny, key: JsAny): JsAny = js("wm.get(key)")
private fun delete(wm: JsAny, key: JsAny): JsAny = js("wm.delete(key, value)")
private fun containsKey(wm: JsAny, key: JsAny): JsBoolean = js("wm.has(key)")

actual class WeakHashMap<K, V> actual constructor() : MutableMap<K, V> {
    private var wm: JsAny = newWeakMap()
    actual override fun put(key: K, value: V): V? = set(wm, key!!.toJsReference(), value!!.toJsReference()) as V?
    actual override fun remove(key: K): V? = delete(wm, key!!.toJsReference()) as V?

    actual override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) ->
            put(key, value)
        }
    }

    actual override fun clear() {
        wm = newWeakMap()
    }

    actual override val keys: MutableSet<K>
        get() = mutableSetOf()
    actual override val values: MutableCollection<V>
        get() = mutableListOf()
    actual override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = mutableSetOf()

    actual override fun isEmpty(): Boolean {
        return true
    }

    actual override fun containsKey(key: K): Boolean = containsKey(wm, key!!.toJsReference()).toBoolean()

    actual override fun containsValue(value: V): Boolean {
        return false
    }

    actual override operator fun get(key: K): V? = get(wm, key!!.toJsReference()) as V?

    actual override val size: Int get() = 0
}
