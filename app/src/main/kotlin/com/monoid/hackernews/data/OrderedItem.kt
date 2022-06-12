package com.monoid.hackernews.data

import com.monoid.hackernews.api.ItemId

data class OrderedItem(
    val itemId: ItemId,
    val order: Int,
)
