package com.monoid.hackernews.common.core.log

interface LoggerAdapter {
    fun recordException(messageString: String, throwable: Throwable, tag: String? = null)
}
