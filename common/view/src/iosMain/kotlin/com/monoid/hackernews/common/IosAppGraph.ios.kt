package com.monoid.hackernews.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface IosAppGraph : IosViewModelGraph.Factory {
    val iosViewModelFactory: IosViewModelProviderFactoryImpl
}
