package com.monoid.hackernews.view.itemdetail

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.monoid.hackernews.common.api.ItemId

@Composable
internal fun ItemId.toExtras(): CreationExtras = MutableCreationExtras().apply {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    set(
        DEFAULT_ARGS_KEY,
        bundleOf(
            ItemDetailViewModel.ITEM_ID to this@toExtras,
        ),
    )
    set(VIEW_MODEL_STORE_OWNER_KEY, viewModelStoreOwner)
    set(SAVED_STATE_REGISTRY_OWNER_KEY, viewModelStoreOwner as SavedStateRegistryOwner)
}
