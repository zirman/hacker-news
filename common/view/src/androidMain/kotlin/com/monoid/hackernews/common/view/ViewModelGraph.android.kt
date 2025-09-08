package com.monoid.hackernews.common.view

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@GraphExtension(ViewModelScope::class)
actual interface ViewModelGraph {
    @Multibinds
    val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>

    @Provides
    fun providesApplication(creationExtras: CreationExtras): Application =
        checkNotNull(creationExtras[APPLICATION_KEY])

    @Provides
    fun providesSavedStateHandle(creationExtras: CreationExtras): SavedStateHandle =
        creationExtras.createSavedStateHandle()

    @GraphExtension.Factory
    actual fun interface Factory {
        actual fun createViewModelGraph(@Provides creationExtras: CreationExtras): ViewModelGraph
    }
}
