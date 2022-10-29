package com.monoid.hackernews.shared.data

import com.monoid.hackernews.shared.api.ItemId

data class ItemTree(
    val itemId: ItemId,
    val isExpanded: Boolean,
    val kids: List<ItemTree>?,
)
