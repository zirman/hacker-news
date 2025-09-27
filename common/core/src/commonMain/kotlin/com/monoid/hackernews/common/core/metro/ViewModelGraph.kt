package com.monoid.hackernews.common.core.metro

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

interface ViewModelGraph {
    val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>
}
