package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.room.ItemDb

data class ItemUiWithThreadDepth(val threadDepth: Int, val itemUi: ItemUi?)

abstract class ItemUi {
    abstract val item: ItemDb
    abstract val kids: List<ItemId>
    abstract val isUpvote: Boolean
    abstract val isFavorite: Boolean
    abstract val isFlag: Boolean
    abstract val isExpanded: Boolean
    abstract val isFollowed: Boolean
    abstract suspend fun toggleUpvote(onNavigateLogin: (LoginAction) -> Unit)
    abstract suspend fun toggleFavorite(onNavigateLogin: (LoginAction) -> Unit)
    abstract suspend fun toggleFlag(onNavigateLogin: (LoginAction) -> Unit)
    abstract suspend fun toggleExpanded()
    abstract suspend fun toggleFollowed()

    override fun equals(other: Any?): Boolean {
        return other is ItemUi &&
            item == other.item &&
            kids == other.kids &&
            isUpvote == other.isUpvote &&
            isFavorite == other.isFavorite &&
            isFlag == other.isFlag &&
            isExpanded == other.isExpanded &&
            isFollowed == other.isFollowed
    }

    override fun hashCode(): Int {
        return item.hashCode() xor
            kids.hashCode() xor
            isUpvote.hashCode() xor
            isFavorite.hashCode() xor
            isFlag.hashCode() xor
            isExpanded.hashCode() xor
            isFollowed.hashCode()
    }
}
