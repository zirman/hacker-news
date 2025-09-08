package com.monoid.hackernews

import com.monoid.hackernews.common.view.IosAppGraph
import dev.zacsweers.metro.createGraph

@Suppress("unused")
fun initKoin() {
    createGraph<IosAppGraph>()
}
