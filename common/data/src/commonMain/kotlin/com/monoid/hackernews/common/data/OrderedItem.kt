package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId

data class OrderedItem(
    val itemId: ItemId,
    val order: Int,
)
