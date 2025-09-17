package com.monoid.hackernews

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(scope = AppScope::class)
interface DesktopAppGraph : DesktopViewModelGraph.Factory {
    val desktopViewModelFactory: DesktopViewModelFactory
}
