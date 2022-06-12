package com.monoid.hackernews.data

import com.monoid.hackernews.api.ItemId

data class ItemTree(
    val itemId: ItemId,
    val isExpanded: Boolean,
    val kids: List<ItemTree>?,
)
