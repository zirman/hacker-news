package com.monoid.hackernews.common.core

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
expect class LoggerModule {

    @Single
    fun logger(): LoggerAdapter
}
