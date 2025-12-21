package com.monoid.hackernews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.monoid.hackernews.common.core.metro.ViewModelGraph
import com.monoid.hackernews.common.core.metro.ViewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@GraphExtension(ViewModelScope::class)
interface IosViewModelGraph : ViewModelGraph {
    override val viewModelProviders get() = iosViewModelProviders

    @Multibinds
    val iosViewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>

    @Provides
    fun providesSavedStateHandle(creationExtras: CreationExtras): SavedStateHandle =
        creationExtras.createSavedStateHandle()

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createViewModelGraph(@Provides creationExtras: CreationExtras): IosViewModelGraph
    }
}
