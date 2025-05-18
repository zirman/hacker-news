package com.monoid.hackernews.common.data.model

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.core.coroutines.doOnErrorThenThrow
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.api.favoriteRequest
import com.monoid.hackernews.common.data.api.flagRequest
import com.monoid.hackernews.common.data.api.getAskStories
import com.monoid.hackernews.common.data.api.getFavorites
import com.monoid.hackernews.common.data.api.getHotStories
import com.monoid.hackernews.common.data.api.getItem
import com.monoid.hackernews.common.data.api.getJobStories
import com.monoid.hackernews.common.data.api.getNewStories
import com.monoid.hackernews.common.data.api.getShowStories
import com.monoid.hackernews.common.data.api.getTopStories
import com.monoid.hackernews.common.data.api.upvoteItem
import com.monoid.hackernews.common.data.room.AskStoryDao
import com.monoid.hackernews.common.data.room.AskStoryDb
import com.monoid.hackernews.common.data.room.HotStoryDao
import com.monoid.hackernews.common.data.room.HotStoryDb
import com.monoid.hackernews.common.data.room.ItemDao
import com.monoid.hackernews.common.data.room.JobStoryDao
import com.monoid.hackernews.common.data.room.JobStoryDb
import com.monoid.hackernews.common.data.room.NewStoryDao
import com.monoid.hackernews.common.data.room.NewStoryDb
import com.monoid.hackernews.common.data.room.ShowStoryDao
import com.monoid.hackernews.common.data.room.ShowStoryDb
import com.monoid.hackernews.common.data.room.TrendingStoryDb
import com.monoid.hackernews.common.data.room.TrendingStoryDao
import io.ktor.client.HttpClient
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Single

@Single
class StoriesRepository(
    private val logger: LoggerAdapter,
    private val remoteDataSource: HttpClient,
    private val trendingStoryLocalDataSource: TrendingStoryDao,
    private val newStoryLocalDataSource: NewStoryDao,
    private val hotStoryLocalDataSource: HotStoryDao,
    private val showStoryLocalDataSource: ShowStoryDao,
    private val askStoryLocalDataSource: AskStoryDao,
    private val jobStoryLocalDataSource: JobStoryDao,
    private val itemLocalDataSource: ItemDao,
    private val settingsRepository: SettingsRepository,
) {
    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    private val scope = CoroutineScope(context)
    private val _cache = MutableStateFlow(persistentMapOf<ItemId, Item>())
    val cache = _cache.asStateFlow()

    private val trendingStoryIds: StateFlow<List<ItemId>?> = trendingStoryLocalDataSource
        .getTrendingStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    private val newStoryIds: StateFlow<List<ItemId>?> = newStoryLocalDataSource
        .getNewStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    private val hotStoryIds: StateFlow<List<ItemId>?> = hotStoryLocalDataSource
        .getHotStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    private val showStoryIds: StateFlow<List<ItemId>?> = showStoryLocalDataSource
        .getShowStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    private val askStoryIds: StateFlow<List<ItemId>?> = askStoryLocalDataSource
        .getAskStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    private val jobStoryIds = jobStoryLocalDataSource
        .getJobStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )

    // TODO: refactor to database
    private val favoriteStoryIds: MutableStateFlow<List<ItemId>> = MutableStateFlow(listOf())

    val trendingStories = combine(_cache, trendingStoryIds, ::Pair).toStories()
    val newStories = combine(_cache, newStoryIds, ::Pair).toStories()
    val hotStories = combine(_cache, hotStoryIds, ::Pair).toStories()
    val showStories = combine(_cache, showStoryIds, ::Pair).toStories()
    val askStories = combine(_cache, askStoryIds, ::Pair).toStories()
    val jobStories: StateFlow<List<Item>?> = combine(_cache, jobStoryIds, ::Pair).toStories()
    val favoriteStories: StateFlow<List<Item>?> = combine(_cache, favoriteStoryIds, ::Pair).toStories()

    suspend fun updateFavoriteStories(username: Username) {
        val html = remoteDataSource.getFavorites(username)
        favoriteStoryIds.value = withContext(Dispatchers.Default) {
            buildList {
                val ksoupHtmlParser = KsoupHtmlParser(
                    KsoupHtmlHandler
                        .Builder()
                        .onOpenTag { name, attributes, isImplied ->
                            if (
                                attributes["class"].equals(
                                    "athing submission",
                                    ignoreCase = true,
                                )
                            ) {
                                attributes["id"]?.toLongOrNull()?.run {
                                    add(ItemId(this))
                                }
                            }
                        }
                        .build(),
                )
                ksoupHtmlParser.write(html)
                ksoupHtmlParser.end()
            }
        }
    }

    private fun Flow<Pair<PersistentMap<ItemId, Item>, List<ItemId>?>>.toStories(
    ): StateFlow<List<Item>?> = map { (cache, itemIds) ->
        itemIds?.map { id ->
            cache[id] ?: Item(id = id)
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = null,
    )

    suspend fun updateTrendingStories() {
        trendingStoryLocalDataSource.replaceTopStories(
            remoteDataSource.getTopStories().mapIndexed { order, storyId ->
                TrendingStoryDb(
                    itemId = storyId,
                    order = order,
                )
            },
        )
    }

    suspend fun updateNewStories() {
        newStoryLocalDataSource.replaceNewStories(
            remoteDataSource.getNewStories().mapIndexed { order, storyId ->
                NewStoryDb(
                    itemId = storyId,
                    order = order,
                )
            },
        )
    }

    suspend fun updateHotStories() {
        hotStoryLocalDataSource.replaceHotStories(
            remoteDataSource.getHotStories().mapIndexed { order, storyId ->
                HotStoryDb(
                    itemId = storyId,
                    order = order,
                )
            },
        )
    }

    suspend fun updateShowStories() {
        showStoryLocalDataSource.replaceShowStories(
            remoteDataSource.getShowStories().mapIndexed { order, storyId ->
                ShowStoryDb(
                    itemId = storyId,
                    order = order,
                )
            },
        )
    }

    suspend fun updateAskStories() {
        askStoryLocalDataSource.replaceAskStories(
            remoteDataSource.getAskStories().mapIndexed { order, storyId ->
                AskStoryDb(
                    itemId = storyId,
                    order = order,
                )
            },
        )
    }

    suspend fun updateJobStories() {
        jobStoryLocalDataSource.replaceJobStories(
            remoteDataSource.getJobStories().mapIndexed { order, storyId ->
                JobStoryDb(
                    itemId = storyId,
                    order = order,
                )
            },
        )
    }

    suspend fun updateItem(itemId: ItemId): Unit = coroutineScope {
        val currentInstant = Clock.System.now()
        val localData = itemLocalDataSource.itemByIdWithKidsById(itemId = itemId.long)
        val lastUpdate = localData?.item?.lastUpdate?.let { Instant.fromEpochSeconds(it) }
        if (localData != null) {
            _cache.update { cache ->
                cache.put(
                    itemId,
                    localData.item.toSimpleItemUiState(localData.kids.map { ItemId(it.id) }),
                )
            }
        }
        // Only query remote if data is older than 5 minutes
        if (lastUpdate == null || (currentInstant - lastUpdate).inWholeMinutes > 5) {
            val remoteData = remoteDataSource.getItem(itemId = itemId)
            if (remoteData != null) {
                var item: Item? = null
                _cache.update { cache ->
                    item = cache[itemId]
                    cache.put(
                        itemId,
                        remoteData.toSimpleItemUiState(
                            instant = currentInstant,
                            item = item,
                        ),
                    )
                }
                itemLocalDataSource.itemApiInsert(
                    instant = currentInstant,
                    itemApi = remoteData,
                    item = item,
                )
            }
        }
    }

    suspend fun toggleUpvote(item: Item) {
        val upvoted = item.upvoted == true
        // optimistically update the cache
        _cache.update { cache ->
            cache.put(
                key = item.id,
                value = cache.getValue(item.id).copy(upvoted = upvoted.not()),
            )
        }
        runCatching {
            remoteDataSource.upvoteItem(
                settings = settingsRepository.preferences.value,
                itemId = item.id,
                flag = upvoted.not(),
            )
        }.doOnErrorThenThrow {
            // revert cache if an error occurred
            _cache.update { cache ->
                cache.put(
                    key = item.id,
                    value = cache.getValue(item.id).copy(upvoted = upvoted),
                )
            }
        }
        // update the local data store
        itemLocalDataSource.setUpvotedByItemId(itemId = item.id.long, upvoted = upvoted.not())
    }

    suspend fun toggleFavorite(item: Item) {
        val favorited = item.favorited == true
        // optimistically update the cache
        _cache.update { cache ->
            cache.put(
                key = item.id,
                value = cache.getValue(item.id).copy(favorited = favorited.not()),
            )
        }
        runCatching {
            remoteDataSource.favoriteRequest(
                settings = settingsRepository.preferences.value,
                itemId = item.id,
                flag = favorited.not(),
            )
        }.doOnErrorThenThrow {
            // revert cache if an error occurred
            _cache.update { cache ->
                cache.put(
                    key = item.id,
                    value = cache.getValue(item.id).copy(favorited = favorited),
                )
            }
        }
        // update the local data store
        itemLocalDataSource.setFavoritedByItemId(itemId = item.id.long, favorited = favorited.not())
    }

    suspend fun toggleFollow(item: Item) {
        val followed = item.followed
        // optimistically update the cache
        _cache.update { cache ->
            cache.put(
                key = item.id,
                value = cache.getValue(item.id).copy(followed = followed.not()),
            )
        }
        // update the local data store
        itemLocalDataSource.setFollowedByItemId(itemId = item.id.long, followed = followed.not())
    }

    suspend fun toggleFlagged(item: Item) {
        val flagged = item.flagged == true
        // optimistically update the cache
        _cache.update { cache ->
            cache.put(
                key = item.id,
                value = cache.getValue(item.id).copy(flagged = flagged.not()),
            )
        }
        runCatching {
            remoteDataSource.flagRequest(
                settings = settingsRepository.preferences.value,
                itemId = item.id,
                flag = flagged.not(),
            )
        }.doOnErrorThenThrow {
            // revert cache if an error occurred
            _cache.update { cache ->
                cache.put(
                    key = item.id,
                    value = cache.getValue(item.id).copy(flagged = flagged),
                )
            }
        }
        // update the local data store
        itemLocalDataSource.setFlaggedByItemId(itemId = item.id.long, flagged = flagged.not())
    }

    suspend fun itemToggleExpanded(itemId: ItemId) {
        val itemWithKids = itemLocalDataSource.itemToggleExpanded(itemId = itemId.long) ?: return
        val item = itemWithKids.item.toSimpleItemUiState(itemWithKids.kids.map { ItemId(it.id) })
        _cache.update { cache ->
            cache.put(itemId, item)
        }
    }

//    fun clearCache() {
//        _cache.update {
//            it.clear()
//        }
//    }
}

private const val TAG = "StoriesRepository"
