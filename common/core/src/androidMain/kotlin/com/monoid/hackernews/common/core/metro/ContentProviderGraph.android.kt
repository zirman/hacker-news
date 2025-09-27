package com.monoid.hackernews.common.core.metro

import android.content.ContentProvider
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

interface ContentProviderGraph {
    val contentProvider: ContentProvider
}

/** A [MapKey] annotation for binding ContentProviders in a multibinding map. */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ContentProviderKey(val value: KClass<out ContentProvider>)

abstract class ContentProviderScope private constructor()
