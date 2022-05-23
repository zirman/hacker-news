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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val sharedFlows: MutableMap<ItemId, Flow<ItemUiInternal>> =
        mutableMapOf()

    @Immutable
    private inner class ItemRowInternal(
        override val itemId: ItemId,
    ) : ItemListRow(), () -> SharedFlow<ItemUiInternal> {
        override val itemUiFlow: Flow<ItemUi>
            get() = sharedFlows
                .getOrPut(itemId, this)
                .onEach { itemUpdatesSharedFlow.emit(it) }

        override fun invoke(): SharedFlow<ItemUiInternal> =
            sharedItemUiFlow(itemId)
    }

    @Immutable
    private inner class ItemThreadInternal(
        override val itemId: ItemId,
        private val threadDepth: Int,
    ) : ItemTreeRow(), () -> SharedFlow<ItemUiInternal> {
        override val itemUiFlow: Flow<ItemUiWithThreadDepth>
            get() = sharedFlows
                .getOrPut(itemId, this)
                .onEach { itemUpdatesSharedFlow.emit(it) }
                .map { ItemUiWithThreadDepth(threadDepth, it) }

        override fun invoke(): SharedFlow<ItemUiInternal> =
            sharedItemUiFlow(itemId)
    }

    @Immutable
    private inner class ItemUiInternal(
        override val item: ItemDb,
        override val kids: List<ItemId>,
        override val isUpvote: Boolean,
        override val isFavorite: Boolean,
        override val isExpanded: Boolean,
    ) : ItemUi() {
        override fun toggleUpvote() {
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

        override fun toggleFavorite() {
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

        override fun toggleExpanded() {
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
    private val itemUpdatesSharedFlow: MutableSharedFlow<ItemUiInternal> =
        MutableSharedFlow(extraBufferCapacity = 10)

    fun itemUiTreeFlow(rootItemId: ItemId): Flow<List<ItemTreeRow>> = flow {
        var itemTree: ItemTree = withContext(Dispatchers.IO) {
            suspend fun recur(itemId: ItemId): ItemTree {
                val itemWithKids = async { itemDao.itemByIdWithKidsById(itemId.long) }
                val isExpanded = async { expandedDao.isExpanded(itemId.long) }

                return ItemTree(
                    itemId = itemId,
                    kids = if (isExpanded.await()) {
                        itemWithKids.await()
                            ?.kids
                            ?.map { async { recur(ItemId(it.id)) } }
                            ?.awaitAll()
                    } else {
                        itemWithKids.await()
                            ?.kids
                            ?.map {
                                ItemTree(
                                    itemId = ItemId(it.id),
                                    isExpanded = false,
                                    kids = null,
                                )
                            }
                    },
                    isExpanded = isExpanded.await(),
                )
            }

            val rootItemWithKids = itemDao.itemByIdWithKidsById(rootItemId.long)

            ItemTree(
                itemId = rootItemId,
                kids = rootItemWithKids?.kids?.map { async { recur(ItemId(it.id)) } }?.awaitAll(),
                isExpanded = true,
            )
        }

        fun traverse(itemTree: ItemTree): List<ItemTreeRow> {
            val list = mutableListOf<ItemTreeRow>()

            fun recur(itemTree: ItemTree, threadDepth: Int = 0) {
                list.add(
                    ItemThreadInternal(
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

        itemUpdatesSharedFlow.collect { itemUi ->
            val itemId = ItemId(itemUi.item.id)
            val isExpanded = itemUi.isExpanded
            val kids = itemUi.kids

            suspend fun recur(itemTree: ItemTree): ItemTree {
                return if (itemTree.itemId == itemId) {
                    itemTree.copy(
                        isExpanded = isExpanded,
                        kids = coroutineScope {
                            kids.map { kidItemId ->
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

    fun itemUiList(itemIds: List<ItemId>): List<ItemListRow> {
        return itemIds.map { itemId -> ItemRowInternal(itemId) }
    }

    private fun sharedItemUiFlow(itemId: ItemId): SharedFlow<ItemUiInternal> = flow {
        val item = itemDao.itemById(itemId.long)

        if (
            item?.lastUpdate == null ||
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
                    .distinctUntilChanged(),
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
                ItemUiInternal(
                    item = itemWithKids.item,
                    kids = itemWithKids.kids.map { ItemId(it.id) },
                    isUpvote = isUpvote,
                    isFavorite = isFavorite,
                    isExpanded = isExpanded,
                )
            }
        )
    }
        .shareIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            replay = 1,
        )
}
