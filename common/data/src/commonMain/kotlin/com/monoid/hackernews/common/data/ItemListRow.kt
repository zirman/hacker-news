package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class ItemListRow {
    abstract val itemId: ItemId
    abstract fun itemUiFlow(scope: CoroutineScope): StateFlow<ItemUi?>

    override fun equals(other: Any?): Boolean =
        other is ItemListRow && itemId == other.itemId

    override fun hashCode(): Int =
        itemId.long.toInt()
}

abstract class ItemTreeRow {
    abstract val itemId: ItemId
    abstract fun itemUiFlow(scope: CoroutineScope): Flow<ItemUiWithThreadDepth>

    override fun equals(other: Any?): Boolean =
        other is ItemTreeRow && itemId == other.itemId

    override fun hashCode(): Int =
        itemId.long.toInt()
}
