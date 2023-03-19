package com.monoid.hackernews.common.data

import androidx.compose.runtime.Immutable
import com.monoid.hackernews.common.api.ItemId

@Immutable
data class ItemTree(
    val itemId: ItemId,
    val isExpanded: Boolean,
    val kids: List<ItemTree>?,
)
