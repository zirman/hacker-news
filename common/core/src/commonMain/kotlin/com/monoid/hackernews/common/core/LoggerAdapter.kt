package com.monoid.hackernews.common.core

interface LoggerAdapter {
    fun recordException(messageString: String, throwable: Throwable, tag: String? = null)
}
