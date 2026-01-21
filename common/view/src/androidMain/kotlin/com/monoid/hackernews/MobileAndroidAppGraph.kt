package com.monoid.hackernews

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import com.monoid.hackernews.common.core.metro.ActivityGraph
import com.monoid.hackernews.common.core.metro.AndroidAppGraph
import com.monoid.hackernews.common.core.metro.BroadcastReceiverGraph
import com.monoid.hackernews.common.core.metro.ContentProviderGraph
import com.monoid.hackernews.common.core.metro.ServiceGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface MobileAndroidAppGraph : AndroidAppGraph {
    @Multibinds
    val _activityGraphProviders: Map<KClass<out Activity>, Provider<ActivityGraph>>
    override val activityGraphProviders: Map<KClass<out Activity>, Provider<ActivityGraph>>
        get() = _activityGraphProviders

    @Multibinds(allowEmpty = true)
    val _serviceGraphProviders: Map<KClass<out Service>, Provider<ServiceGraph>>
    override val serviceGraphProviders get() = _serviceGraphProviders

    @Multibinds(allowEmpty = true)
    val _broadcastReceiverGraphProviders: Map<KClass<out BroadcastReceiver>, Provider<BroadcastReceiverGraph>>
    override val broadcastReceiverGraphProviders get() = _broadcastReceiverGraphProviders

    @Multibinds(allowEmpty = true)
    val _contentProviderGraphProviders: Map<KClass<out ContentProvider>, Provider<ContentProviderGraph>>
    override val contentProviderGraphProviders get() = _contentProviderGraphProviders
}
