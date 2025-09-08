package com.monoid.hackernews.common.view

import androidx.lifecycle.viewmodel.CreationExtras

expect interface ViewModelGraph {
    fun interface Factory {
        fun createViewModelGraph(creationExtras: CreationExtras): ViewModelGraph
    }
}
