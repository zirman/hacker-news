package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.data.api.ItemId

@Composable
fun createItemDetailViewModel(itemId: ItemId): ItemDetailViewModel = metroViewModel(
    key = itemId.toString(),
    extras = itemId.toItemDetailViewModelExtras(),
)
