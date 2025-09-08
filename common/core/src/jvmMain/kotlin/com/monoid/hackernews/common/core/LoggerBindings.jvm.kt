package com.monoid.hackernews.common.core

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
class JvmLoggerBindings() {

    @SingleIn(AppScope::class)
    @Provides
    fun providesLogger(): LoggerAdapter = LoggerAdapterImpl()
}
