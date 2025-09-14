package com.monoid.hackernews.common.core.metro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.plus

@Composable
actual inline fun <reified VM : ViewModel> metroViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    key: String?,
    extras: CreationExtras,
): VM = viewModel(
    viewModelStoreOwner = viewModelStoreOwner,
    key = key,
    factory = LocalIosViewModelProviderFactory.current,
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
    crossinline factory: ViewModelGraph.() -> VM
): VM {
    val metroViewModelProviderFactory = LocalIosViewModelProviderFactory.current
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = metroViewModelProviderFactory,
        extras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
            viewModelStoreOwner.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        } + extras,
    )
}

val LocalIosViewModelProviderFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("CompositionLocal LocalIosViewModelProviderFactory not present")
}
