package com.monoid.hackernews

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ProcessLifecycleOwnerQualifier
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.view.UiModeConfigurator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object AppBindings {
    @SingleIn(AppScope::class)
    @ProcessLifecycleOwnerQualifier
    @Provides
    fun providesProcessLifecycleOwner(): LifecycleOwner = ProcessLifecycleOwner.get()

    @SingleIn(AppScope::class)
    @Provides
    fun providesUiModeConfigurator(
        context: Application,
        @ProcessLifecycleOwnerQualifier
        lifecycleOwner: LifecycleOwner,
        settings: SettingsRepository,
        logger: LoggerAdapter,
    ): UiModeConfigurator = UiModeConfigurator(context, lifecycleOwner, settings, logger)
}
