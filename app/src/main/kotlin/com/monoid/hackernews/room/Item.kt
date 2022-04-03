package com.monoid.hackernews.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.monoid.hackernews.api.ItemId
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Item(
    @PrimaryKey val id: Long,
    // last time item was retrieved from api
    val lastUpdate: Long? = null,
    val type: String? = null,
    val time: Long? = null,
    val deleted: Boolean? = null,
    val by: String? = null,
    val descendants: Int? = null,
    val score: Int? = null,
    val title: String? = null,
    val text: String? = null,
    val url: String? = null,

    val parent: Long? = null,
)

data class ItemWithKids(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent",
    )
    val kids: List<Item>,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId",
    )
    val favorites: List<Favorite>,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId",
    )
    val upvotes: List<Upvote>,
)

@Serializable
data class ItemTree(
    val item: Item,
    val kids: List<ItemTree>?,
    val expanded: Boolean = false,
)

data class ItemRow(
    val item: Item,
    val kidCount: Int,
    val expanded: Boolean,
    val depth: Int,
)

fun ItemTree.traverse(): List<ItemRow> {
    val list = mutableListOf<ItemRow>()

    fun recur(itemTree: ItemTree, depth: Int) {
        list.add(
            ItemRow(
                item = itemTree.item,
                kidCount = itemTree.kids?.size ?: 0,
                expanded = itemTree.expanded,
                depth = depth,
            )
        )

        if (itemTree.expanded) {
            itemTree.kids?.forEach { recur(it, depth + 1) }
        }
    }

    recur(this, 0)
    return list.toList()
}

fun ItemTree.update(itemWithKids: ItemWithKids): ItemTree =
    if (item.id == itemWithKids.item.id) {
        copy(
            item = itemWithKids.item,
            kids = itemWithKids.kids.map { item ->
                kids?.firstOrNull { it.item.id == item.id }
                    ?.copy(item = item)
                    ?: ItemTree(item, null)
            },
        )
    } else {
        copy(kids = kids?.map { it.update(itemWithKids) })
    }

fun ItemTree.setExpanded(itemId: ItemId, expanded: Boolean): ItemTree =
    if (ItemId(item.id) == itemId) {
        copy(expanded = expanded)
    } else {
        copy(kids = kids?.map { it.setExpanded(itemId, expanded) })
    }
