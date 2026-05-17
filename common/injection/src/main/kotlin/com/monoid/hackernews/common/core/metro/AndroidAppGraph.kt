package com.monoid.hackernews.common.core.metro

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import kotlin.reflect.KClass

interface AndroidAppGraph {
    val application: Application
    val activityGraphProviders: Map<KClass<out Activity>, () -> ActivityGraph>
    val serviceGraphProviders: Map<KClass<out Service>, () -> ServiceGraph>
    val broadcastReceiverGraphProviders: Map<KClass<out BroadcastReceiver>, () -> BroadcastReceiverGraph>
    val contentProviderGraphProviders: Map<KClass<out ContentProvider>, () -> ContentProviderGraph>
}
