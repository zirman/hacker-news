package com.monoid.hackernews.wear

import androidx.annotation.Keep
import androidx.core.app.AppComponentFactory
import com.monoid.hackernews.AndroidAppComponentFactory
import com.monoid.hackernews.common.core.metro.AndroidAppGraph
import dev.zacsweers.metro.createGraph

/**
 * An [AppComponentFactory] that uses Metro for constructor injection of Activities.
 *
 * If you have minSdk < 28, you can fall back to using member injection on Activities or (better)
 * use an architecture that abstracts the Android framework components away.
 */
@Keep
class WearAndroidAppComponentFactory : AndroidAppComponentFactory() {
    override fun createAndroidAppGraph(): AndroidAppGraph = createGraph<WearAndroidAppGraph>()
}
