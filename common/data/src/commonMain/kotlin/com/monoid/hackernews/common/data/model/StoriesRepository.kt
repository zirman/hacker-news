package com.monoid.hackernews.common.data.model

import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.api.getItem
import com.monoid.hackernews.common.data.api.getTopStories
import com.monoid.hackernews.common.data.room.ItemDao
import com.monoid.hackernews.common.data.room.TopStoryDao
import com.monoid.hackernews.common.data.room.TopStoryDb
import com.monoid.hackernews.common.injection.LoggerAdapter
import io.ktor.client.HttpClient
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class StoriesRepository(
    private val logger: LoggerAdapter,
    private val remoteDataSource: HttpClient,
    private val topStoryLocalDataSource: TopStoryDao,
    private val itemLocalDataSource: ItemDao,
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

    private val topStoryIds: StateFlow<List<ItemId>?> = topStoryLocalDataSource
        .getTopStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    val topStories = combine(_cache, topStoryIds, ::Pair)
        .map { (cache, itemIds) ->
            itemIds?.map { id ->
                cache[id] ?: makeItem(id = id)
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    suspend fun updateBestStories() {
        topStoryLocalDataSource.replaceTopStories(
            remoteDataSource.getTopStories().mapIndexed { order, storyId ->
                TopStoryDb(
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
            _cache.update { cache ->
                cache.put(
                    itemId,
                    remoteData.toSimpleItemUiState(currentInstant),
                )
            }
            itemLocalDataSource.itemApiInsert(remoteData, currentInstant)
        }
    }

    suspend fun itemToggleExpanded(itemId: ItemId) {
        val itemWithKids = itemLocalDataSource.itemToggleExpanded(itemId = itemId.long) ?: return
        val item = itemWithKids.item.toSimpleItemUiState(itemWithKids.kids.map { ItemId(it.id) })
        _cache.update { cache ->
            cache.put(itemId, item)
        }
    }

    fun clearCache() {
        _cache.update {
            it.clear()
        }
    }

    companion object {
        private const val TAG = "StoriesRepository"
    }
}