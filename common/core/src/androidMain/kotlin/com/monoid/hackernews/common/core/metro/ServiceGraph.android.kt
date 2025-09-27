package com.monoid.hackernews.common.core.metro

import android.app.Service
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

interface ServiceGraph {
    val service: Service
}

/** A [MapKey] annotation for binding Services in a multibinding map. */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ServiceKey(val value: KClass<out Service>)

abstract class ServiceScope private constructor()
