package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
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
    factory = metroViewModelProviderFactory(),
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
    TODO("Not yet implemented")
}

@Composable
fun metroViewModelProviderFactory(): MetroViewModelFactory = TODO()
//    (LocalContext.current as HasDefaultViewModelProviderFactory)
//        .defaultViewModelProviderFactory as MetroViewModelFactory
