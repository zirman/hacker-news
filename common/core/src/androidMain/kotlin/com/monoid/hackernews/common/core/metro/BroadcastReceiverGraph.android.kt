package com.monoid.hackernews.common.core.metro

import android.content.BroadcastReceiver
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

interface BroadcastReceiverGraph {
    val broadcastReceiver: BroadcastReceiver
}

/** A [MapKey] annotation for binding BroadcastReceivers in a multibinding map. */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class BroadcastReceiverKey(val value: KClass<out BroadcastReceiver>)

abstract class BroadcastReceiverScope private constructor()
