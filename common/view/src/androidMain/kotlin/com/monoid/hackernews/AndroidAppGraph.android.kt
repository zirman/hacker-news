package com.monoid.hackernews

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.core.AndroidLoggerBindings
import com.monoid.hackernews.common.core.DispatcherBindings
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.AndroidDataStoreBindings
import com.monoid.hackernews.common.data.AndroidDatabaseBindings
import com.monoid.hackernews.common.data.AndroidNetworkBindings
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.view.UiModeConfigurator
import com.monoid.hackernews.common.view.ViewModelGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import kotlin.reflect.KClass

@DependencyGraph(
    AppScope::class,
    bindingContainers = [
        DispatcherBindings::class,
        AndroidNetworkBindings::class,
        AndroidDatabaseBindings::class,
        AndroidDataStoreBindings::class,
        AndroidLoggerBindings::class,
    ]
)
interface AndroidAppGraph : ViewModelGraph.Factory {
    val db: HNDatabase
    val httpClient: HttpClient

    @Provides
    fun providesApplicationContext(application: Application): Context = application

    @Multibinds
    val activityProviders: Map<KClass<out Activity>, Provider<Activity>>

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AndroidAppGraph
    }

    val application: Application

    @SingleIn(AppScope::class)
    @Named("ProcessLifecycleOwner")
    @Provides
    fun providesProcessLifecycleOwner(): LifecycleOwner = ProcessLifecycleOwner.get()

    // @Factory
    // fun channel(): Channel<Intent> = Channel()

    @SingleIn(AppScope::class)
    @Provides
    fun providesUiModeConfigurator(
        context: Context,
        @Named("ProcessLifecycleOwner")
        lifecycleOwner: LifecycleOwner,
        settings: SettingsRepository,
        logger: LoggerAdapter,
    ): UiModeConfigurator =
        UiModeConfigurator(context, lifecycleOwner, settings, logger)
}
