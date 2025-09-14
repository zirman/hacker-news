package com.monoid.hackernews.common.core.metro

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

interface AndroidViewModelFactory : ViewModelProvider.Factory {
    fun viewModelGraph(extras: CreationExtras): ViewModelGraph
}
