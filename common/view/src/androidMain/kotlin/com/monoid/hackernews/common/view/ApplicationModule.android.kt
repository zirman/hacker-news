package com.monoid.hackernews.common.view

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.core.DispatchersModule
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.core.LoggerModule
import com.monoid.hackernews.common.data.DataStoreModule
import com.monoid.hackernews.common.data.DatabaseModule
import com.monoid.hackernews.common.data.NetworkModule
import com.monoid.hackernews.common.data.model.SettingsRepository
import kotlinx.coroutines.channels.Channel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module(
    includes = [
        DispatchersModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        DataStoreModule::class,
        LoggerModule::class,
    ],
)
@ComponentScan("com.monoid.hackernews")
actual object ApplicationModule {
    @Single
    @Named(type = ProcessLifecycleOwner::class)
    fun processLifecycleOwner(): LifecycleOwner = ProcessLifecycleOwner.get()

    @Factory
    fun channel(): Channel<Intent> = Channel()

    @Single(createdAtStart = true)
    fun uiModeConfigurator(
        context: Context,
        @Named(type = ProcessLifecycleOwner::class)
        lifecycleOwner: LifecycleOwner,
        settings: SettingsRepository,
        logger: LoggerAdapter,
    ): UiModeConfigurator =
        UiModeConfigurator(context, lifecycleOwner, settings, logger)
}
