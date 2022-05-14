package com.monoid.hackernews

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

// Perform transformation on every element in an an iterable concurrently.
suspend inline fun <T, R> Iterable<T>.mapAsync(crossinline transform: suspend (T) -> R): List<R> {
    return coroutineScope { map { x -> async { transform(x) } } }.map { it.await() }
}
