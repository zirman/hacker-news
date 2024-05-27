package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getItem
import com.monoid.hackernews.common.api.getTopStories
import com.monoid.hackernews.common.api.toItemDb
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
    private val cache = MutableStateFlow(persistentMapOf<ItemId, SimpleItemUiState>())

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

    val topStories = combine(cache, topStoryIds, ::Pair)
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
        val localData = itemLocalDataSource.itemById(itemId = itemId.long)
        val lastUpdate = localData?.lastUpdate?.let { Instant.fromEpochSeconds(it) }
        if (localData != null) {
            cache.update { map ->
                map.put(itemId, localData.toSimpleItemUiState())
            }
        }
        // Only query remote if data is older than 5 minutes
        if (lastUpdate == null || (currentInstant - lastUpdate).inWholeMinutes > 5) {
            val remoteData = remoteDataSource.getItem(itemId = itemId)
            cache.update { map ->
                map.put(itemId, remoteData.toSimpleItemUiState(currentInstant))
            }
            itemLocalDataSource.itemUpsert(remoteData.toItemDb(currentInstant))
        }
    }

    fun clearCache() {
        cache.update {
            it.clear()
        }
    }

    companion object {
        private const val TAG = "StoriesRepository"
    }
}
