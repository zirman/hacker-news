package com.monoid.hackernews.common.view

import com.monoid.hackernews.common.core.DispatcherBindings
import com.monoid.hackernews.common.core.log.IosLoggerBindings
import com.monoid.hackernews.common.data.IosDataStoreBindings
import com.monoid.hackernews.common.data.IosDatabaseBindings
import com.monoid.hackernews.common.data.IosNetworkBindings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(
    scope = AppScope::class,
    bindingContainers = [
        DispatcherBindings::class,
        IosNetworkBindings::class,
        IosDatabaseBindings::class,
        IosDataStoreBindings::class,
        IosLoggerBindings::class,
    ],
)
interface IosAppGraph : ViewModelGraph.Factory {
    val metroViewModelFactory: MetroViewModelFactory
}
