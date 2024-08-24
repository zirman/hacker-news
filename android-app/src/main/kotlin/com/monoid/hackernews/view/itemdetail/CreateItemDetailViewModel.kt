package com.monoid.hackernews.view.itemdetail

import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.api.ItemId
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun createItemDetailViewModel(itemId: ItemId): ItemDetailViewModel = koinViewModel(
    key = itemId.toString(),
    extras = itemId.toExtras(),
)
