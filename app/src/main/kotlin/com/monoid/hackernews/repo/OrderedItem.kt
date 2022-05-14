package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId

data class OrderedItem(
    val itemId: ItemId,
    val order: Int,
)
