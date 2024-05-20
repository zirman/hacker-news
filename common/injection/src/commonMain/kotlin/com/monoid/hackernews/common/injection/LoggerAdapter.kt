package com.monoid.hackernews.common.injection

interface LoggerAdapter {
    fun recordException(messageString: String, throwable: Throwable, tag: String? = null)
}
