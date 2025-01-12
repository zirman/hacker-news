package com.monoid.hackernews.wear

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.core.DispatchersModule
import com.monoid.hackernews.common.core.LoggerModule
import com.monoid.hackernews.common.data.DataStoreModule
import com.monoid.hackernews.common.data.DatabaseModule
import com.monoid.hackernews.common.data.NetworkModule
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
@ComponentScan("com.monoid.hackernews.wear")
class WearApplicationModule {

    @Single
    @Named(type = ProcessLifecycleOwner::class)
    fun processLifecycleOwner(): LifecycleOwner = ProcessLifecycleOwner.get()

    @Factory
    fun channel(): Channel<Intent> = Channel()
}
