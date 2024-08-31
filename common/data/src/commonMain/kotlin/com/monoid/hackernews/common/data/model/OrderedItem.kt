package com.monoid.hackernews.common.data.model

import com.monoid.hackernews.common.data.api.ItemId

data class OrderedItem(
    val itemId: ItemId,
    val order: Int,
)
