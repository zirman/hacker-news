package com.monoid.hackernews.common.core.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
object AndroidLoggerBindings {
    @Provides
    fun providesLogger(): LoggerAdapter = LoggerAdapterImpl(FirebaseCrashlytics.getInstance())
}
