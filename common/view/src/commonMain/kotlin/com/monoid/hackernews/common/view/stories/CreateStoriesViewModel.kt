package com.monoid.hackernews.common.view.stories

import androidx.compose.runtime.Composable
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.savedState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun createStoriesViewModel(storyOrdering: StoryOrdering): StoriesViewModel {
    return koinViewModel(
        key = storyOrdering.toString(),
        extras = storyOrdering.toExtras(),
    )
}

@Composable
private fun StoryOrdering.toExtras(): CreationExtras = MutableCreationExtras().apply {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    set(
        DEFAULT_ARGS_KEY,
        savedState {
            putString(StoriesViewModel.STORY_ORDERING, this@toExtras.name)
        },
    )
    set(VIEW_MODEL_STORE_OWNER_KEY, viewModelStoreOwner)
    set(SAVED_STATE_REGISTRY_OWNER_KEY, viewModelStoreOwner as SavedStateRegistryOwner)
}
