package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.favoriteRequest
import com.monoid.hackernews.common.api.flagRequest
import com.monoid.hackernews.common.api.getItem
import com.monoid.hackernews.common.api.upvoteItem
import com.monoid.hackernews.common.injection.LoggerAdapter
import com.monoid.hackernews.common.room.ExpandedDao
import com.monoid.hackernews.common.room.ExpandedDb
import com.monoid.hackernews.common.room.FavoriteDao
import com.monoid.hackernews.common.room.FavoriteDb
import com.monoid.hackernews.common.room.FlagDao
import com.monoid.hackernews.common.room.FlagDb
import com.monoid.hackernews.common.room.FollowedDao
import com.monoid.hackernews.common.room.FollowedDb
import com.monoid.hackernews.common.room.ItemDao
import com.monoid.hackernews.common.room.ItemDb
import com.monoid.hackernews.common.room.UpvoteDao
import com.monoid.hackernews.common.room.UpvoteDb
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.max

class ItemTreeRepository(
    private val preferences: DataStore<Preferences>,
    private val remoteDataSource: HttpClient,
    private val logger: LoggerAdapter,
    private val itemLocalDataSource: ItemDao,
    private val upvoteLocalDataSource: UpvoteDao,
    private val favoriteLocalDataSource: FavoriteDao,
    private val flagLocalDataSource: FlagDao,
    private val expandedLocalDataSource: ExpandedDao,
    private val followedLocalDataSource: FollowedDao,
    private val mainDispatcher: MainCoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val itemCache: MutableMap<ItemId, ItemUiInternal> = mutableMapOf()

    private val coroutineScope =
        CoroutineScope(SupervisorJob() + ioDispatcher + CoroutineExceptionHandler { _, throwable ->
            logger.recordException(
                messageString = "CoroutineScope",
                throwable = throwable,
                tag = TAG,
            )
        })

    private val itemUpdatesSharedFlow: MutableSharedFlow<ItemUiInternal?> =
        MutableSharedFlow(extraBufferCapacity = 10)

    private inner class ItemRowInternal(
        override val itemId: ItemId,
    ) : ItemListRow() {
        override fun itemUiFlow(scope: CoroutineScope): StateFlow<ItemUi?> {
            return sharedItemUiFlow(scope, itemId)
        }
    }

    private inner class ItemThreadInternal(
        override val itemId: ItemId,
        private val threadDepth: Int,
    ) : ItemTreeRow() {
        override fun itemUiFlow(scope: CoroutineScope): Flow<ItemUiWithThreadDepth> {
            return sharedItemUiFlow(scope, itemId)
                .onEach { itemUpdatesSharedFlow.emit(it) }
                .map { ItemUiWithThreadDepth(threadDepth, it) }
        }
    }

    fun cleanup() {
        itemCache.clear()
    }

    suspend fun upvoteItemJob(
        preferences: Preferences,
        itemId: ItemId,
        isUpvote: Boolean = true,
    ) {
        try {
            remoteDataSource.upvoteItem(
                preferences = preferences,
                itemId = itemId,
                flag = isUpvote,
            )

            if (isUpvote) {
                upvoteLocalDataSource.upvoteInsert(
                    UpvoteDb(
                        preferences.username.string,
                        itemId.long
                    )
                )
            } else {
                upvoteLocalDataSource.upvoteDelete(
                    UpvoteDb(
                        preferences.username.string,
                        itemId.long
                    )
                )
            }
        } catch (throwable: Throwable) {
            currentCoroutineContext().ensureActive()
            logger.recordException(
                messageString = "upvoteItemJob",
                throwable = throwable,
                tag = TAG,
            )
        }
    }

    suspend fun favoriteItemJob(
        preferences: Preferences,
        itemId: ItemId,
        isFavorite: Boolean = true,
    ) {
        try {
            remoteDataSource.favoriteRequest(
                preferences = preferences,
                itemId = itemId,
                flag = isFavorite,
            )

            if (isFavorite) {
                favoriteLocalDataSource.favoriteInsert(
                    FavoriteDb(
                        preferences.username.string,
                        itemId.long
                    )
                )
            } else {
                favoriteLocalDataSource.favoriteDelete(
                    FavoriteDb(
                        preferences.username.string,
                        itemId.long
                    )
                )
            }
        } catch (throwable: Throwable) {
            currentCoroutineContext().ensureActive()
            logger.recordException(
                messageString = "favoriteItemJob",
                throwable = throwable,
                tag = TAG,
            )
        }
    }

    suspend fun flagItemJob(
        preferences: Preferences,
        itemId: ItemId,
        isFlag: Boolean = true,
    ) {
        try {
            remoteDataSource.flagRequest(
                preferences = preferences,
                itemId = ItemId(itemId.long),
            )

            if (isFlag) {
                flagLocalDataSource.flagInsert(FlagDb(preferences.username.string, itemId.long))
            } else {
                flagLocalDataSource.flagDelete(FlagDb(preferences.username.string, itemId.long))
            }
        } catch (throwable: Throwable) {
            currentCoroutineContext().ensureActive()
            logger.recordException(
                messageString = "flagItemJob",
                throwable = throwable,
                tag = TAG,
            )
        }
    }

    fun itemUiTreeFlow(rootItemId: ItemId): Flow<List<ItemTreeRow>> = flow {
        var itemTree: ItemTree = withContext(ioDispatcher) {
            suspend fun recur(itemId: ItemId): ItemTree {
                val itemWithKids = async { itemLocalDataSource.itemByIdWithKidsById(itemId.long) }
                val isExpanded = async { expandedLocalDataSource.isExpanded(itemId.long) }

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
                    isExpanded = isExpanded.await()
                )
            }

            val rootItemWithKids = itemLocalDataSource.itemByIdWithKidsById(rootItemId.long)

            ItemTree(
                itemId = rootItemId,
                kids = rootItemWithKids?.kids?.map { async { recur(ItemId(it.id)) } }?.awaitAll(),
                isExpanded = true
            )
        }

        fun traverse(itemTree: ItemTree): List<ItemTreeRow> {
            return buildList {
                fun recur(itemTree: ItemTree, threadDepth: Int = 0) {
                    add(
                        ItemThreadInternal(
                            itemId = itemTree.itemId,
                            threadDepth = threadDepth
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
            itemUpdatesSharedFlow.filterNotNull().map { itemUi ->
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
                                            isExpanded = false
                                        )
                                }
                            }
                        )
                    } else {
                        itemTree.copy(kids = itemTree.kids?.map { recur(it) })
                    }
                }

                itemTree = recur(itemTree)
                traverse(itemTree)
            }
        )
    }.distinctUntilChanged()

    fun itemUiList(itemIds: List<ItemId>): List<ItemListRow> {
        return itemIds.map { itemId -> ItemRowInternal(itemId) }
    }

    private fun sharedItemUiFlow(
        scope: CoroutineScope,
        itemId: ItemId,
    ): StateFlow<ItemUiInternal?> = flow {
        // network requests are are not canceled when flow is canceled
        coroutineScope.launch {
            val item = itemLocalDataSource.itemById(itemId.long)

            if (
                item?.lastUpdate == null ||
                (Clock.System.now() - Instant.fromEpochSeconds(item.lastUpdate))
                    .inWholeMinutes > 5
            ) {
                try {
                    itemLocalDataSource.itemApiInsert(remoteDataSource.getItem(itemId))
                } catch (throwable: Throwable) {
                    currentCoroutineContext().ensureActive()
                    logger.recordException(
                        messageString = "sharedItemUiFlow",
                        throwable = throwable,
                        tag = TAG,
                    )
                }
            }
        }

        emitAll(
            combine(
                combine(
                    itemLocalDataSource.itemByIdWithKidsByIdFlow(itemId.long)
                        .filterNotNull()
                        .distinctUntilChanged(),
                    expandedLocalDataSource.isExpandedFlow(itemId.long)
                        .distinctUntilChanged(),
                    followedLocalDataSource.isFollowedFlow(itemId.long)
                        .distinctUntilChanged(),
                    ::Triple,
                ).distinctUntilChanged(),
                preferences.data
                    .map { it.username }
                    .distinctUntilChanged()
                    .flatMapLatest { username ->
                        combine(
                            upvoteLocalDataSource
                                .isUpvoteFlow(itemId.long, username.string)
                                .distinctUntilChanged(),
                            favoriteLocalDataSource
                                .isFavoriteFlow(itemId.long, username.string)
                                .distinctUntilChanged(),
                            flagLocalDataSource
                                .isFlagFlow(itemId.long, username.string)
                                .distinctUntilChanged(),
                            ::Triple,
                        )
                    }
            ) { (itemWithKids, isExpanded, isFollowed), (isUpvote, isFavorite, isFlag) ->
                ItemUiInternal(
                    item = itemWithKids.item,
                    kids = itemWithKids.kids.map { ItemId(it.id) },
                    isUpvote = isUpvote,
                    isFavorite = isFavorite,
                    isFlag = isFlag,
                    isExpanded = isExpanded,
                    isFollowed = isFollowed,
                )
            }
        )
    }
        .onEach {
            // make sure we're accessing itemCache from main thread
            withContext(Dispatchers.Main) {
                // keep ordered by most recently updated
                itemCache.remove(itemId)
                itemCache[itemId] = it

                itemCache.keys
                    .take(max(itemCache.size - MEMORY_CACHE_SIZE, 0))
                    .forEach { itemCache.remove(it) }
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = run {
                itemCache[itemId]
            }
        )

    private inner class ItemUiInternal(
        override val item: ItemDb,
        override val kids: List<ItemId>,
        override val isUpvote: Boolean,
        override val isFavorite: Boolean,
        override val isFlag: Boolean,
        override val isExpanded: Boolean,
        override val isFollowed: Boolean,
    ) : ItemUi() {
        override suspend fun toggleUpvote(onNavigateLogin: (LoginAction) -> Unit) {
            val authentication = preferences.data.first()

            if (authentication.password.string.isNotEmpty()) {
                upvoteItemJob(authentication, ItemId(item.id), isUpvote.not())
            } else {
                withContext(mainDispatcher.immediate) {
                    onNavigateLogin(LoginAction.Upvote(itemId = item.id))
                }
            }
        }

        override suspend fun toggleFavorite(onNavigateLogin: (LoginAction) -> Unit) {
            val authentication = preferences.data.first()

            if (authentication.password.string.isNotEmpty()) {
                favoriteItemJob(authentication, ItemId(item.id), isFavorite.not())
            } else {
                withContext(mainDispatcher.immediate) {
                    onNavigateLogin(LoginAction.Favorite(itemId = item.id))
                }
            }
        }

        override suspend fun toggleFlag(onNavigateLogin: (LoginAction) -> Unit) {
            val authentication = preferences.data.first()

            if (authentication.password.string.isNotEmpty()) {
                flagItemJob(authentication, ItemId(item.id), isFlag.not())
            } else {
                withContext(mainDispatcher.immediate) {
                    onNavigateLogin(LoginAction.Flag(itemId = item.id))
                }
            }
        }

        override suspend fun toggleExpanded() {
            if (isExpanded) {
                expandedLocalDataSource.expandedDelete(ExpandedDb(item.id))
            } else {
                expandedLocalDataSource.expandedInsert(ExpandedDb(item.id))
            }
        }

        override suspend fun toggleFollowed() {
            if (isFollowed) {
                followedLocalDataSource.followedDelete(FollowedDb(item.id))
            } else {
                followedLocalDataSource.followedInsert(FollowedDb(item.id))
            }
        }
    }

    companion object {
        private const val TAG = "ItemTreeRepository"
        private const val MEMORY_CACHE_SIZE = 500
    }
}
