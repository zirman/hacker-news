package com.monoid.hackernews.common.view

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface DesktopAppGraph : DesktopViewModelGraph.Factory {
    val desktopViewModelFactory: DesktopViewModelProviderFactoryImpl
}
