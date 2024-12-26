package com.monoid.hackernews

import com.monoid.hackernews.common.data.DataStoreModule
import com.monoid.hackernews.common.data.DatabaseModule
import com.monoid.hackernews.common.data.NetworkModule
import com.monoid.hackernews.common.injection.DispatcherModule
import com.monoid.hackernews.common.injection.LoggerModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        DispatcherModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        DataStoreModule::class,
        LoggerModule::class,
    ],
)
@ComponentScan("com.monoid.hackernews")
class ApplicationModule
