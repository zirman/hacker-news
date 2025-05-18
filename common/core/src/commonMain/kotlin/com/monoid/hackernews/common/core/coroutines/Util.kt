package com.monoid.hackernews.common.core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

suspend inline fun <T> Result<T>.doOnErrorThenThrow(onError: (Throwable) -> Unit): T {
    val exception = exceptionOrNull()
    if (exception != null) {
        currentCoroutineContext().ensureActive()
        onError(exception)
        throw exception
    }
    return getOrThrow()
}

fun <T, R> StateFlow<T>.mapStateIn(
    scope: CoroutineScope,
    started: SharingStarted,
    transform: (T) -> R,
): StateFlow<R> =
    map(transform).stateIn(scope = scope, started = started, transform(value))
