package com.monoid.hackernews.common.core.metro

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

/** A [dev.zacsweers.metro.MapKey] annotation for binding ViewModels in a multibinding map. */
@MapKey
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val value: KClass<out ViewModel>)
