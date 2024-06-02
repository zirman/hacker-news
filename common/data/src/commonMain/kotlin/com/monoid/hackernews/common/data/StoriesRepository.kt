package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getItem
import com.monoid.hackernews.common.api.getTopStories
import com.monoid.hackernews.common.injection.LoggerAdapter
import com.monoid.hackernews.common.room.ItemDao
import com.monoid.hackernews.common.room.TopStoryDao
import com.monoid.hackernews.common.room.TopStoryDb
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
    private val _cache = MutableStateFlow(persistentMapOf<ItemId, SimpleItemUiState>())
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
                cache[id] ?: makeSimpleItemUiState(id = id)
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
                cache.put(itemId, localData.item.toSimpleItemUiState(localData.kids.map { ItemId(it.id) }))
            }
        }
        // Only query remote if data is older than 5 minutes
        if (lastUpdate == null || (currentInstant - lastUpdate).inWholeMinutes > 5) {
            val remoteData = remoteDataSource.getItem(itemId = itemId)
            _cache.update { cache ->
                cache.put(itemId, remoteData.toSimpleItemUiState(currentInstant))
            }
            itemLocalDataSource.itemApiInsert(remoteData, currentInstant)
        }
    }

    fun getCachedItem(itemId: ItemId): SimpleItemUiState? = _cache.value[itemId]

    suspend fun getItem(itemId: ItemId): SimpleItemUiState {
        val currentInstant = Clock.System.now()
        val localData = itemLocalDataSource.itemByIdWithKidsById(itemId = itemId.long)
        val lastUpdate = localData?.item?.lastUpdate?.let { Instant.fromEpochSeconds(it) }
        if (localData != null) {
            _cache.update { cache ->
                cache.put(itemId, localData.item.toSimpleItemUiState(localData.kids.map { ItemId(it.id) }))
            }
        }
        // Only query remote if data is older than 5 minutes
        return if (lastUpdate == null || (currentInstant - lastUpdate).inWholeMinutes > 5) {
            val remoteData = remoteDataSource.getItem(itemId = itemId)
            _cache.update { cache ->
                cache.put(itemId, remoteData.toSimpleItemUiState(currentInstant))
            }
            itemLocalDataSource.itemApiInsert(itemApi = remoteData, instant = currentInstant)
            remoteData.toSimpleItemUiState(currentInstant)
        } else {
            localData.item.toSimpleItemUiState(localData.kids.map { ItemId(it.id) })
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
