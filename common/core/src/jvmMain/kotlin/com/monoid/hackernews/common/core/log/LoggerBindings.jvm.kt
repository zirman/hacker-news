package com.monoid.hackernews.common.core.log

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
object JvmLoggerBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesLogger(): LoggerAdapter = LoggerAdapterImpl()
}
