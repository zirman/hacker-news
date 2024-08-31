package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.data.api.ItemId
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun createItemDetailViewModel(itemId: ItemId): ItemDetailViewModel = koinViewModel(
    key = itemId.toString(),
    extras = itemId.toExtras(),
)
