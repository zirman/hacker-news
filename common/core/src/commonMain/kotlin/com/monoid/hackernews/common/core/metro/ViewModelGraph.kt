package com.monoid.hackernews.common.core.metro

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

interface ViewModelGraph {
    val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>
}
