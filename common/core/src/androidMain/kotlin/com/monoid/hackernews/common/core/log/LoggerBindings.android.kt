package com.monoid.hackernews.common.core.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
class AndroidLoggerBindings() {

    @Provides
    fun providesLogger(): LoggerAdapter = LoggerAdapterImpl(FirebaseCrashlytics.getInstance())
}
