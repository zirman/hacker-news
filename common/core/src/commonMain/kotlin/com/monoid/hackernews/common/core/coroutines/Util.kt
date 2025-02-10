package com.monoid.hackernews.common.core.coroutines

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <T> Result<T>.doOnErrorThenThrow(onError: (Throwable) -> Unit): T {
    val exception = exceptionOrNull()
    if (exception != null) {
        currentCoroutineContext().ensureActive()
        onError(exception)
        throw exception
    }
    return getOrThrow()
}
