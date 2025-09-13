package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.plus
import kotlin.reflect.KClass
import kotlin.reflect.cast

@Composable
actual inline fun <reified VM : ViewModel> metroViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    key: String?,
    extras: CreationExtras,
): VM = viewModel(
    viewModelStoreOwner = viewModelStoreOwner,
    key = key,
    factory = LocalJvmAppGraph.current.metroViewModelFactory,
    extras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    } + extras,
)

@Composable
actual inline fun <reified VM : ViewModel> metroViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    key: String?,
    extras: CreationExtras,
    crossinline factory: ViewModelGraph.() -> VM,
): VM {
    val metroViewModelProviderFactory = LocalJvmAppGraph.current
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                val viewModelGraph = metroViewModelProviderFactory.createViewModelGraph(extras)
                return checkNotNull(modelClass.cast(viewModelGraph.factory()))
            }
        },
        extras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
            viewModelStoreOwner.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        } + extras,
    )
}

val LocalJvmAppGraph = staticCompositionLocalOf<JvmAppGraph> {
    error("CompositionLocal LocalJvmAppGraph not present")
}
