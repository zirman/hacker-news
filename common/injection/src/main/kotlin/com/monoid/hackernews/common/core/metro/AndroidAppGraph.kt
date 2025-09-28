package com.monoid.hackernews.common.core.metro

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

interface AndroidAppGraph {
    val application: Application
    val activityGraphProviders: Map<KClass<out Activity>, Provider<ActivityGraph>>
    val serviceGraphProviders: Map<KClass<out Service>, Provider<ServiceGraph>>
    val broadcastReceiverGraphProviders: Map<KClass<out BroadcastReceiver>, Provider<BroadcastReceiverGraph>>
    val contentProviderGraphProviders: Map<KClass<out ContentProvider>, Provider<ContentProviderGraph>>
}
