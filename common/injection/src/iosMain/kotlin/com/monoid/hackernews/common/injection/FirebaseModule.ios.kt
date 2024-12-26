package com.monoid.hackernews.common.injection

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class LoggerModule {

    @Single
    actual fun logger(): LoggerAdapter = LoggerAdapterImpl()
}
