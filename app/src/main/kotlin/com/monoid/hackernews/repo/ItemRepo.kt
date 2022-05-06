package com.monoid.hackernews.repo

import com.monoid.hackernews.HNApplication
import com.monoid.hackernews.R
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getItem
import com.monoid.hackernews.room.ExpandedDao
import com.monoid.hackernews.room.ExpandedDb
import com.monoid.hackernews.room.ItemDao
import com.monoid.hackernews.room.ItemDb
import com.monoid.hackernews.room.ItemTree
import com.monoid.hackernews.room.ItemUi
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class ItemRepo(
    private val httpClient: HttpClient,
    private val itemDao: ItemDao,
    private val expandedDao: ExpandedDao,
) {
    fun itemTreeFactory(rootItemId: ItemId): ItemTreeFactory {
        return ItemTreeFactory(rootItemId)
    }

    private sealed class Event {
        data class TryItemUpdate(val item: ItemId) : Event()
        data class SetExpanded(val item: ItemId, val expanded: Boolean) : Event()
    }

    inner class ItemTreeFactory(private val rootItemId: ItemId) {
        private val itemTreeEventChannel: Channel<Event> =
            Channel()

        fun itemUiListFlow(): Flow<List<ItemUi>> =
            flow {
                fun traverse(itemTree: ItemTree): List<ItemUi> {
                    val list = mutableListOf<ItemUi>()

                    fun recur(itemTree: ItemTree, depth: Int) {
                        list.add(
                            ItemUi(
                                item = itemTree.item,
                                expanded = itemTree.expanded,
                                kidCount = itemTree.kids?.size ?: 0,
                                depth = depth,
                            )
                        )

                        if (itemTree.expanded) {
                            itemTree.kids?.forEach { recur(it, depth + 1) }
                        }
                    }

                    recur(itemTree = itemTree, depth = 0)
                    return list.toList()
                }

                var itemTree: ItemTree =
                    withContext(Dispatchers.IO) {
                        suspend fun recur(itemId: ItemId): ItemTree {
                            return (itemDao.itemByIdWithKidsById(itemId.long)
                                ?: run {
                                    try {
                                        itemDao.itemApiInsert(httpClient.getItem(rootItemId))
                                    } catch (error: Throwable) {
                                        if (error is CancellationException) throw error
                                    }

                                    itemDao.itemByIdWithKidsById(rootItemId.long)
                                })
                                .let { itemWithKids ->
                                    val expanded = expandedDao.isExpanded(itemId.long)

                                    ItemTree(
                                        item = itemWithKids?.item ?: ItemDb(id = itemId.long),
                                        kids = if (expanded) {
                                            itemWithKids?.kids
                                                ?.map { async { recur(ItemId(it.id)) } }
                                                ?.map { it.await() }
                                        } else {
                                            itemWithKids?.kids
                                                ?.map {
                                                    async {
                                                        ItemTree(
                                                            item = it,
                                                            kids = null,
                                                            expanded = expandedDao.isExpanded(it.id),
                                                        )
                                                    }
                                                }
                                                ?.map { it.await() }
                                        },
                                        expanded = expanded,
                                    )
                                }
                        }

                        recur(rootItemId)
                    }

                for (event in itemTreeEventChannel) {
                    when (event) {
                        is Event.SetExpanded -> {
                            withContext(Dispatchers.IO) {
                                if (event.expanded) {
                                    expandedDao.expandedInsert(ExpandedDb(event.item.long))
                                } else {
                                    expandedDao.expandedDelete(ExpandedDb(event.item.long))
                                }
                            }

                            fun recur(itemTree: ItemTree): ItemTree {
                                return if (itemTree.item.id == event.item.long) {
                                    itemTree.copy(expanded = event.expanded)
                                } else {
                                    itemTree.copy(kids = itemTree.kids?.map { recur(it) })
                                }
                            }

                            itemTree = recur(itemTree)
                        }
                        is Event.TryItemUpdate -> {
                            itemTree = withContext(Dispatchers.IO) {
                                suspend fun recur(itemTree: ItemTree): ItemTree {
                                    return if (itemTree.item.id == event.item.long) {
                                        if (itemTree.item.lastUpdate == null ||
                                            (Clock.System.now() - Instant.fromEpochSeconds(itemTree.item.lastUpdate))
                                                .inWholeMinutes >
                                            HNApplication.instance.resources
                                                .getInteger(R.integer.item_stale_minutes)
                                                .toLong()
                                        ) {
                                            try {
                                                itemDao.itemApiInsert(httpClient.getItem(event.item))
                                            } catch (error: Throwable) {
                                                if (error is CancellationException) throw error
                                            }
                                        }

                                        val itemWithKids =
                                            itemDao.itemByIdWithKidsById(event.item.long)

                                        itemTree.copy(
                                            item = itemWithKids?.item ?: ItemDb(id = event.item.long),
                                            kids = itemWithKids?.kids?.map { itemDb ->
                                                itemTree.kids?.find { it.item.id == itemDb.id }
                                                    ?: ItemTree(
                                                        item = itemDb,
                                                        kids = null,
                                                        expanded = expandedDao.isExpanded(itemDb.id),
                                                    )
                                            },
                                        )
                                    } else {
                                        itemTree.copy(kids = itemTree.kids?.map { recur(it) })
                                    }
                                }

                                recur(itemTree)
                            }
                        }
                    }

                    emit(traverse(itemTree))
                }
            }

        suspend fun itemUpdate(itemId: ItemId) {
            itemTreeEventChannel.send(Event.TryItemUpdate(itemId))
        }

        suspend fun setItemExpanded(itemId: ItemId, expanded: Boolean) {
            itemTreeEventChannel.send(Event.SetExpanded(itemId, expanded))
        }
    }

    suspend fun itemUpdate(itemId: ItemId) {
        withContext(Dispatchers.IO) {
            val item = itemDao.itemById(itemId.long)

            if (item?.lastUpdate == null ||
                (Clock.System.now() - Instant.fromEpochSeconds(item.lastUpdate))
                    .inWholeMinutes >
                HNApplication.instance.resources
                    .getInteger(R.integer.item_stale_minutes)
                    .toLong()
            ) {
                try {
                    itemDao.itemApiInsert(httpClient.getItem(itemId))
                } catch (error: Throwable) {
                    if (error is CancellationException) throw error
                }
            }
        }
    }
}
