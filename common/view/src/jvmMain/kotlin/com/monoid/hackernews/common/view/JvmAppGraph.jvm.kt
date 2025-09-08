package com.monoid.hackernews.common.view

import com.monoid.hackernews.common.core.DispatcherBindings
import com.monoid.hackernews.common.core.JvmLoggerBindings
import com.monoid.hackernews.common.data.JvmDataStoreBindings
import com.monoid.hackernews.common.data.JvmDatabaseBindings
import com.monoid.hackernews.common.data.JvmNetworkBindings
import dev.zacsweers.metro.BindingContainer

@BindingContainer(
    includes = [
        DispatcherBindings::class,
        JvmNetworkBindings::class,
        JvmDatabaseBindings::class,
        JvmDataStoreBindings::class,
        JvmLoggerBindings::class,
    ],
)
interface JvmAppGraph : ViewModelGraph.Factory
