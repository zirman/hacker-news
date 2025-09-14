package com.monoid.hackernews

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.monoid.hackernews.common.core.metro.ViewModelGraph
import com.monoid.hackernews.common.core.metro.ViewModelScope
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@GraphExtension(ViewModelScope::class)
interface AndroidViewModelGraph : ViewModelGraph {
    override val viewModelProviders get() = androidViewModelProviders

    @Multibinds
    val androidViewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>

    @Provides
    fun providesApplication(creationExtras: CreationExtras): Application =
        checkNotNull(creationExtras[APPLICATION_KEY])

    @Provides
    fun providesSavedStateHandle(creationExtras: CreationExtras): SavedStateHandle =
        creationExtras.createSavedStateHandle()

    @GraphExtension.Factory
    fun interface Factory {
        fun createViewModelGraph(@Provides creationExtras: CreationExtras): AndroidViewModelGraph
    }
}
