package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId

data class ItemTree(
    val itemId: ItemId,
    val isExpanded: Boolean,
    val kids: List<ItemTree>?,
)
