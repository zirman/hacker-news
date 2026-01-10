package com.monoid.hackernews.common.view

import androidx.compose.runtime.snapshots.SnapshotStateList

fun <T> SnapshotStateList<T>.navigateTo(route: T) {
    add(route)
}

fun <T> SnapshotStateList<T>.navigateUp() {
    removeLastOrNull()
}

inline fun <reified R : T, T> SnapshotStateList<T>.currentBottomNav(): R? {
    for (i in indices.reversed()) {
        (this[i] as? R)?.run {
            return this
        }
    }
    return null
}

inline fun <reified R : T, T> SnapshotStateList<T>.currentStack(bottomNav: R): IntRange {
    var last = indices.last
    for (i in indices.reversed()) {
        if (this[i] == bottomNav) {
            return i..last
        }
        if (this[i] is R) {
            last = i - 1
        }
    }
    add(bottomNav)
    val i = indices.last
    return i..i
}
