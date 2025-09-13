package com.monoid.hackernews.common.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * A [ViewModelProvider.Factory] that uses an injected map of [KClass] to [Provider] of [ViewModel]
 * to create ViewModels.
 */
@ContributesBinding(AppScope::class)
@Inject
class MetroViewModelFactory(val appGraph: JvmAppGraph) : ViewModelProvider.Factory {

    fun viewModelGraph(extras: CreationExtras): ViewModelGraph {
        return appGraph.createViewModelGraph(extras)
    }

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val viewModelGraph = viewModelGraph(extras)
        println(viewModelGraph.viewModelProviders)
        val provider = viewModelGraph.viewModelProviders[modelClass.java.kotlin]
            ?: throw IllegalArgumentException("Unknown model class $modelClass")
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return modelClass.cast(provider())
    }
}
