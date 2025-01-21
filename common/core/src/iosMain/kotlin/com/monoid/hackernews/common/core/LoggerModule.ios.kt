package com.monoid.hackernews.common.core

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class LoggerModule {

    @Single
    fun logger(): LoggerAdapter = LoggerAdapterImpl()
}
