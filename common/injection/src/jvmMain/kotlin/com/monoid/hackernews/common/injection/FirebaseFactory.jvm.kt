package com.monoid.hackernews.common.injection

actual fun loggerFactory(): LoggerAdapter {
    return LoggerAdapterStub()
}
