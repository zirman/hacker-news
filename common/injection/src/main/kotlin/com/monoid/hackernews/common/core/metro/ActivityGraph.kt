package com.monoid.hackernews.common.core.metro

import android.app.Activity
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

interface ActivityGraph {
    val activity: Activity
}

/** A [MapKey] annotation for binding Activities in a multibinding map. */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ActivityKey(val value: KClass<out Activity>)

abstract class ActivityScope private constructor()
