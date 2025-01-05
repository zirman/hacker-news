package com.monoid.hackernews

import com.monoid.hackernews.common.data.DataStoreModule
import com.monoid.hackernews.common.data.DatabaseModule
import com.monoid.hackernews.common.data.NetworkModule
import com.monoid.hackernews.common.core.DispatchersModule
import com.monoid.hackernews.common.core.LoggerModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

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
class ApplicationModule
