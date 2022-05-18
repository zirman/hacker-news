package com.monoid.hackernews.repo

import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.room.ItemDb

data class ItemUiWithThreadDepth(val threadDepth: Int, val itemUi: ItemUi?)

abstract class ItemUi {
    abstract val item: ItemDb
    abstract val kids: List<ItemId>
    abstract val isUpvote: Boolean
    abstract val isFavorite: Boolean
    abstract val isExpanded: Boolean

    abstract fun toggleUpvote()
    abstract fun toggleFavorite()
    abstract fun toggleExpanded()

    override fun equals(other: Any?): Boolean {
        return other is ItemUi &&
            item == other.item &&
            kids == other.kids &&
            isUpvote == other.isUpvote &&
            isFavorite == other.isFavorite &&
            isExpanded == other.isExpanded
    }

    override fun hashCode(): Int {
        return item.hashCode() xor
            kids.hashCode() xor
            isUpvote.hashCode() xor
            isFavorite.hashCode() xor
            isExpanded.hashCode()
    }
}
