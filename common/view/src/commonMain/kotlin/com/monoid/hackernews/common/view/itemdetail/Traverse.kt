package com.monoid.hackernews.common.view.itemdetail

import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.makeItem

/**
 * Traverses an Item and it's descendants and builds a list with depth and descendant count.
 */
internal fun Map<ItemId, Item>.traverse(
    itemId: ItemId,
): List<ItemDetailViewModel.ThreadItemUiState> = buildList {
    fun recurDescendants(itemId: ItemId): Int = 1 + this@traverse[itemId]?.kids.orEmpty().sumOf {
        recurDescendants(it)
    }

    fun recur(itemId: ItemId, depth: Int): Int {
        val item = this@traverse[itemId] ?: makeItem(id = itemId)
        val kids = item.kids.orEmpty()
        return 1 + if (item.expanded) {
            var decendents = 0
            for (i in kids.size - 1 downTo 0) {
                decendents += recur(kids[i], depth = depth + 1)
            }
            add(
                ItemDetailViewModel.ThreadItemUiState(
                    item = item,
                    depth = depth,
                    decendents = decendents,
                )
            )
            decendents
        } else {
            var decendents = 0
            for (i in kids.size - 1 downTo 0) {
                decendents += recurDescendants(kids[i])
            }
            add(
                ItemDetailViewModel.ThreadItemUiState(
                    item = item,
                    depth = depth,
                    decendents = decendents,
                )
            )
            decendents
        }
    }
    recur(itemId, depth = 0)
}.asReversed()
