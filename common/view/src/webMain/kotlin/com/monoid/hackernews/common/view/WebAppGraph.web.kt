package com.monoid.hackernews.common.view

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface WebAppGraph : WebViewModelGraph.Factory {
    val webViewModelFactory: WebViewModelProviderFactoryImpl
}
