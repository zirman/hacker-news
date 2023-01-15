package com.monoid.hackernews.common.data

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.favoriteRequest
import com.monoid.hackernews.common.api.flagRequest
import com.monoid.hackernews.common.api.getItem
import com.monoid.hackernews.common.api.upvoteItem
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.room.ExpandedDao
import com.monoid.hackernews.common.room.ExpandedDb
import com.monoid.hackernews.common.room.FavoriteDao
import com.monoid.hackernews.common.room.FavoriteDb
import com.monoid.hackernews.common.room.FlagDao
import com.monoid.hackernews.common.room.FlagDb
import com.monoid.hackernews.common.room.ItemDao
import com.monoid.hackernews.common.room.ItemDb
import com.monoid.hackernews.common.room.UpvoteDao
import com.monoid.hackernews.common.room.UpvoteDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemTreeRepository @Inject constructor(
    private val authentication: DataStore<Authentication>,
    private val httpClient: HttpClient,
    private val itemDao: ItemDao,
    private val upvoteDao: UpvoteDao,
    private val favoriteDao: FavoriteDao,
    private val flagDao: FlagDao,
    private val expandedDao: ExpandedDao,
) {
    private val sharedFlows: MutableMap<ItemId, WeakReference<Flow<ItemUiInternal>>> =
        mutableMapOf()

    private val itemUpdatesSharedFlow: MutableSharedFlow<ItemUiInternal> =
        MutableSharedFlow(extraBufferCapacity = 10)

    @Immutable
    private inner class ItemRowInternal(
        override val itemId: ItemId,
    ) : ItemListRow() {
        override val itemUiFlow: Flow<ItemUi>
            get() {
                return sharedFlows[itemId]?.get()
                    ?: sharedItemUiFlow(itemId).also {
                        sharedFlows[itemId] = WeakReference(it)
                    }
            }
    }

    @Immutable
    private inner class ItemThreadInternal(
        override val itemId: ItemId,
        private val threadDepth: Int,
    ) : ItemTreeRow() {
        override val itemUiFlow: Flow<ItemUiWithThreadDepth>
            get() {
                return (
                        sharedFlows[itemId]?.get()
                            ?: sharedItemUiFlow(itemId).also {
                                sharedFlows[itemId] = WeakReference(it)
                            }
                        )
                    .onEach { itemUpdatesSharedFlow.emit(it) }
                    .map { ItemUiWithThreadDepth(threadDepth, it) }
            }
    }

    fun cleanup() {
        sharedFlows.toList()
            .forEach { (itemId) ->
                if (sharedFlows[itemId]?.get() == null) {
                    sharedFlows.remove(itemId)
                }
            }
    }

    suspend fun upvoteItemJob(
        authentication: Authentication,
        itemId: ItemId,
        isUpvote: Boolean = true
    ) {
        try {
            httpClient.upvoteItem(
                authentication = authentication,
                itemId = itemId,
                flag = isUpvote,
            )

            if (isUpvote) {
                upvoteDao.upvoteInsert(UpvoteDb(authentication.username, itemId.long))
            } else {
                upvoteDao.upvoteDelete(UpvoteDb(authentication.username, itemId.long))
            }
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
        }
    }

    suspend fun favoriteItemJob(
        authentication: Authentication,
        itemId: ItemId,
        isFavorite: Boolean = true
    ) {
        try {
            httpClient.favoriteRequest(
                authentication = authentication,
                itemId = itemId,
                flag = isFavorite,
            )

            if (isFavorite) {
                favoriteDao.favoriteInsert(FavoriteDb(authentication.username, itemId.long))
            } else {
                favoriteDao.favoriteDelete(FavoriteDb(authentication.username, itemId.long))
            }
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
        }
    }

    suspend fun flagItemJob(
        authentication: Authentication,
        itemId: ItemId,
        isFlag: Boolean = true
    ) {
        try {
            httpClient.flagRequest(
                authentication = authentication,
                itemId = ItemId(itemId.long),
            )

            if (isFlag) {
                flagDao.flagInsert(FlagDb(authentication.username, itemId.long))
            } else {
                flagDao.flagDelete(FlagDb(authentication.username, itemId.long))
            }
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
        }
    }

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
                        null
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
            return buildList {
                fun recur(itemTree: ItemTree, threadDepth: Int = 0) {
                    add(
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
            }
        }

        emit(traverse(itemTree))

        emitAll(
            itemUpdatesSharedFlow.map { itemUi ->
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
                traverse(itemTree)
            }
        )
    }
        .distinctUntilChanged()

    fun itemUiList(itemIds: List<ItemId>): List<ItemListRow> {
        return itemIds.map { itemId -> ItemRowInternal(itemId) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sharedItemUiFlow(
        itemId: ItemId,
    ): SharedFlow<ItemUiInternal> = flow {
        coroutineScope {
            launch {
                val item = withContext(Dispatchers.IO) {
                    itemDao.itemById(itemId.long)
                }

                if (
                    item?.lastUpdate == null ||
                    (Clock.System.now() - Instant.fromEpochSeconds(item.lastUpdate))
                        .inWholeMinutes > 5
//                    HNApplication.instance.resources
//                        .getInteger(R.integer.item_stale_minutes)
//                        .toLong()
                ) {
                    try {
                        itemDao.itemApiInsert(httpClient.getItem(itemId))
                    } catch (error: Throwable) {
                        if (error is CancellationException) throw error
                    }
                }
            }

            emitAll(
                combine(
                    combine(
                        itemDao.itemByIdWithKidsByIdFlow(itemId.long)
                            .filterNotNull()
                            .distinctUntilChanged(),
                        expandedDao.isExpandedFlow(itemId.long)
                            .distinctUntilChanged(),
                        ::Pair,
                    ).distinctUntilChanged(),
                    authentication.data
                        .map { it.username }
                        .distinctUntilChanged()
                        .flatMapLatest { username ->
                            combine(
                                upvoteDao
                                    .isUpvoteFlow(itemId.long, username)
                                    .distinctUntilChanged(),
                                favoriteDao
                                    .isFavoriteFlow(itemId.long, username)
                                    .distinctUntilChanged(),
                                flagDao
                                    .isFlagFlow(itemId.long, username)
                                    .distinctUntilChanged(),
                                ::Triple
                            )
                        },
                ) { (itemWithKids, isExpanded), (isUpvote, isFavorite, isFlag) ->
                    ItemUiInternal(
                        item = itemWithKids.item,
                        kids = itemWithKids.kids.map { ItemId(it.id) },
                        isUpvote = isUpvote,
                        isFavorite = isFavorite,
                        isFlag = isFlag,
                        isExpanded = isExpanded,
                    )
                }
            )
        }
    }
        .shareIn(
            scope = GlobalScope, // shared flows are in global scope
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            replay = 1,
        )

    @Immutable
    private inner class ItemUiInternal(
        override val item: ItemDb,
        override val kids: List<ItemId>,
        override val isUpvote: Boolean,
        override val isFavorite: Boolean,
        override val isFlag: Boolean,
        override val isExpanded: Boolean,
    ) : ItemUi() {
        override suspend fun toggleUpvote(onNavigateLogin: (LoginAction) -> Unit) {
            val authentication = authentication.data.first()

            if (authentication.password.isNotEmpty()) {
                upvoteItemJob(authentication, ItemId(item.id), isUpvote.not())
            } else {
                withContext(Dispatchers.Main.immediate) {
                    onNavigateLogin(LoginAction.Upvote(itemId = item.id))
                }
            }
        }

        override suspend fun toggleFavorite(onNavigateLogin: (LoginAction) -> Unit) {
            val authentication = authentication.data.first()

            if (authentication.password.isNotEmpty()) {
                favoriteItemJob(authentication, ItemId(item.id), isFavorite.not())
            } else {
                withContext(Dispatchers.Main.immediate) {
                    onNavigateLogin(LoginAction.Favorite(itemId = item.id))
                }
            }
        }

        override suspend fun toggleFlag(
            onNavigateLogin: (LoginAction) -> Unit
        ) {
            val authentication = authentication.data.first()

            if (authentication.password.isNotEmpty()) {
                flagItemJob(authentication, ItemId(item.id), isFlag.not())
            } else {
                withContext(Dispatchers.Main.immediate) {
                    onNavigateLogin(LoginAction.Flag(itemId = item.id))
                }
            }
        }

        override suspend fun toggleExpanded() {
            if (isExpanded) {
                expandedDao.expandedDelete(ExpandedDb(item.id))
            } else {
                expandedDao.expandedInsert(ExpandedDb(item.id))
            }
        }
    }
}
