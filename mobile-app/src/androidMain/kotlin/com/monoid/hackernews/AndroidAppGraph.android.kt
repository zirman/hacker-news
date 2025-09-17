package com.monoid.hackernews

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ProcessLifecycleOwnerQualifier
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.view.UiModeConfigurator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AndroidViewModelGraph.Factory {
    val db: HNDatabase
    val httpClient: HttpClient
    val uiModeConfigurator: UiModeConfigurator

    @Provides
    fun providesApplicationContext(application: Application): Context = application

    @Multibinds
    val activityProviders: Map<KClass<out Activity>, Provider<out Activity>>

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AndroidAppGraph
    }

    @SingleIn(AppScope::class)
    @ProcessLifecycleOwnerQualifier
    @Provides
    fun providesProcessLifecycleOwner(): LifecycleOwner = ProcessLifecycleOwner.get()

    @SingleIn(AppScope::class)
    @Provides
    fun providesUiModeConfigurator(
        context: Context,
        @ProcessLifecycleOwnerQualifier
        lifecycleOwner: LifecycleOwner,
        settings: SettingsRepository,
        logger: LoggerAdapter,
    ): UiModeConfigurator =
        UiModeConfigurator(context, lifecycleOwner, settings, logger)
}
