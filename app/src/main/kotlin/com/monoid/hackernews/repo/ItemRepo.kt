package com.monoid.hackernews.repo

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import com.monoid.hackernews.HNApplication
import com.monoid.hackernews.R
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.favoriteRequest
import com.monoid.hackernews.api.getItem
import com.monoid.hackernews.api.upvoteRequest
import com.monoid.hackernews.datastore.Authentication
import com.monoid.hackernews.util.mapAsync
import com.monoid.hackernews.room.ExpandedDao
import com.monoid.hackernews.room.ExpandedDb
import com.monoid.hackernews.room.FavoriteDao
import com.monoid.hackernews.room.FavoriteDb
import com.monoid.hackernews.room.ItemDao
import com.monoid.hackernews.room.ItemDb
import com.monoid.hackernews.room.UpvoteDao
import com.monoid.hackernews.room.UpvoteDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class ItemRepo(
    private val authenticationDataStore: DataStore<Authentication>,
    private val httpClient: HttpClient,
    private val itemDao: ItemDao,
    private val upvoteDao: UpvoteDao,
    private val favoriteDao: FavoriteDao,
    private val expandedDao: ExpandedDao,
) {
    private val sharedFlows: MutableMap<ItemId, Flow<ItemUi>> = mutableMapOf()

    inner class ItemRow(val itemId: ItemId, private val threadDepth: Int) {
        override fun equals(other: Any?): Boolean = other is ItemRow && itemId == other.itemId
        override fun hashCode(): Int = itemId.long.toInt()

        val itemUiFlow: Flow<ItemUi> by lazy {
            sharedFlows.getOrPut(itemId) {
                flow {
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

                    val username = authenticationDataStore.data.first().username

                    expandedDao.isExpandedFlow(itemId.long)
                        .distinctUntilChanged()

                    var foo: Triple<ItemId, Boolean, List<ItemId>>? = null

                    emitAll(
                        combine(
                            combine(
                                itemDao.itemByIdWithKidsByIdFlow(itemId.long)
                                    .filterNotNull()
                                    .distinctUntilChanged(),
                                expandedDao.isExpandedFlow(itemId.long)
                                    .distinctUntilChanged(),
                                ::Pair,
                            )
                                .onEach { (itemWithKids, isExpanded) ->
                                    val bar = Triple(
                                        ItemId(itemWithKids.item.id),
                                        isExpanded,
                                        itemWithKids.kids.map { ItemId(it.id) }
                                    )

                                    if (foo != bar) {
                                        foo = bar
                                        itemUpdatesChannel.tryEmit(bar)
                                    }
                                },
                            combine(
                                upvoteDao
                                    .isUpvoteFlow(itemId.long, username)
                                    .distinctUntilChanged(),
                                favoriteDao
                                    .isFavoriteFlow(itemId.long, username)
                                    .distinctUntilChanged(),
                                ::Pair,
                            )
                        ) { (itemWithKids, isExpanded), (isUpvote, isFavorite) ->
                            ItemUi(
                                item = itemWithKids.item,
                                kidCount = itemWithKids.kids.size,
                                isUpvote = isUpvote,
                                isFavorite = isFavorite,
                                isExpanded = isExpanded,
                                threadDepth = 0, // changes based root
                            )
                        }
                    )
                }
                    .shareIn(
                        scope = coroutineScope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                        replay = 1,
                    )
                    .map { it.setThreadDepth(threadDepth) }
            }
        }
    }

    @Immutable
    inner class ItemUi(
        val item: ItemDb,
        val kidCount: Int,
        val isUpvote: Boolean,
        val isFavorite: Boolean,
        val isExpanded: Boolean,
        val threadDepth: Int,
    ) {
        override fun equals(other: Any?): Boolean {
            return other is ItemUi &&
                item == other.item &&
                kidCount == other.kidCount &&
                isUpvote == other.isUpvote &&
                isFavorite == other.isFavorite &&
                isExpanded == other.isExpanded &&
                threadDepth == other.threadDepth
        }

        override fun hashCode(): Int {
            return item.hashCode() xor
                kidCount.hashCode() xor
                isUpvote.hashCode() xor
                isFavorite.hashCode() xor
                isExpanded.hashCode() xor
                threadDepth.hashCode()
        }

        // used to set depth after update from db
        fun setThreadDepth(threadDepth: Int): ItemUi {
            return ItemUi(item, kidCount, isUpvote, isFavorite, isExpanded, threadDepth)
        }

        fun toggleUpvote() {
            coroutineScope.launch {
                try {
                    val authentication = authenticationDataStore.data.first()

                    httpClient.upvoteRequest(
                        authentication = authentication,
                        itemId = ItemId(item.id),
                    )

                    if (isUpvote) {
                        upvoteDao.upvoteDelete(UpvoteDb(authentication.username, item.id))
                    } else {
                        upvoteDao.upvoteInsert(UpvoteDb(authentication.username, item.id))
                    }
                } catch (error: Throwable) {
                    if (error is CancellationException) throw error
                }
            }
        }

        fun toggleFavorite() {
            coroutineScope.launch {
                try {
                    val authentication = authenticationDataStore.data.first()

                    httpClient.favoriteRequest(
                        authentication = authentication,
                        itemId = ItemId(item.id),
                    )

                    if (isFavorite) {
                        favoriteDao.favoriteDelete(FavoriteDb(authentication.username, item.id))
                    } else {
                        favoriteDao.favoriteInsert(FavoriteDb(authentication.username, item.id))
                    }
                } catch (error: Throwable) {
                    if (error is CancellationException) throw error
                }
            }
        }

        fun toggleExpanded() {
            coroutineScope.launch {
                if (isExpanded) {
                    expandedDao.expandedDelete(ExpandedDb(item.id))
                } else {
                    expandedDao.expandedInsert(ExpandedDb(item.id))
                }
            }
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val itemUpdatesChannel: MutableSharedFlow<Triple<ItemId, Boolean, List<ItemId>>> =
        MutableSharedFlow(extraBufferCapacity = 10)

    fun itemUiTreeFlow(rootItemId: ItemId): Flow<List<ItemRow>> = flow {
        var itemTree: ItemTree = withContext(Dispatchers.IO) {
            val rootItemWithKids = itemDao.itemByIdWithKidsById(rootItemId.long)

            ItemTree(
                itemId = rootItemId,
                kids = rootItemWithKids?.kids?.map {
                    ItemTree(
                        itemId = ItemId(it.id),
                        isExpanded = expandedDao.isExpanded(it.id),
                        kids = null,
                    )
                },
                isExpanded = rootItemWithKids?.kids != null,
            )
        }

        fun traverse(itemTree: ItemTree): List<ItemRow> {
            val list = mutableListOf<ItemRow>()

            fun recur(itemTree: ItemTree, threadDepth: Int = 0) {
                list.add(
                    ItemRow(
                        itemId = itemTree.itemId,
                        threadDepth = threadDepth,
                    )
                )

                if (itemTree.itemId == rootItemId || itemTree.isExpanded) {
                    itemTree.kids?.forEach { recur(it, threadDepth + 1) }
                }
            }

            recur(itemTree)
            return list.toList()
        }

        emit(traverse(itemTree))

        itemUpdatesChannel.collect { (itemId, isExpanded, kids) ->
            suspend fun recur(itemTree: ItemTree): ItemTree {
                return if (itemTree.itemId == itemId) {
                    itemTree.copy(
                        isExpanded = isExpanded,
                        kids = coroutineScope {
                            kids.mapAsync { kidItemId ->
                                itemTree.kids?.find { it.itemId == kidItemId }
                                    ?: ItemTree(
                                        itemId = kidItemId,
                                        kids = null,
                                        isExpanded = false,
                                    )
                            }
                        },
                    )
                } else {
                    itemTree.copy(kids = itemTree.kids?.map { recur(it) })
                }
            }

            itemTree = recur(itemTree)
            emit(traverse(itemTree))
        }
    }
        .distinctUntilChanged()

    fun itemUiList(itemIds: List<ItemId>): List<ItemRow> {
        return itemIds.map { itemId -> ItemRow(itemId, 0) }
    }
}
