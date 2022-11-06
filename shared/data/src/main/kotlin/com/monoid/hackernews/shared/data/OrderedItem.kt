package com.monoid.hackernews.shared.data

import com.monoid.hackernews.shared.api.ItemId

data class OrderedItem(
    val itemId: ItemId,
    val order: Int,
)
