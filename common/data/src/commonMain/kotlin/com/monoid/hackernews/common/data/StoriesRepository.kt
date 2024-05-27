package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemApi
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getBestStories
import com.monoid.hackernews.common.api.getItem
import com.monoid.hackernews.common.api.toItemDb
import com.monoid.hackernews.common.injection.LoggerAdapter
import com.monoid.hackernews.common.room.BestStoryDao
import com.monoid.hackernews.common.room.BestStoryDb
import com.monoid.hackernews.common.room.ItemDao
import com.monoid.hackernews.common.room.ItemDb
import io.ktor.client.HttpClient
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class StoriesRepository(
    private val logger: LoggerAdapter,
    private val remoteDataSource: HttpClient,
    private val bestStoryLocalDataSource: BestStoryDao,
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

    private val bestStoryIds: StateFlow<List<ItemId>?> = bestStoryLocalDataSource
        .getBestStories()
        .map { items ->
            items.sortedBy { it.order }.map { item -> ItemId(item.itemId) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    val bestStories = combine(cache, bestStoryIds, ::Pair)
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
        bestStoryLocalDataSource.replaceBestStories(
            remoteDataSource.getBestStories().mapIndexed { order, storyId ->
                BestStoryDb(
                    itemId = storyId,
                    order = order,
                )
            },
        )
    }

    suspend fun updateItem(itemId: ItemId): Unit = coroutineScope {
        val localDataAsync = async { itemLocalDataSource.itemById(itemId = itemId.long) }
        val remoteDataAsync = async { remoteDataSource.getItem(itemId = itemId) }
        val localData = localDataAsync.await()
        if (localData != null) {
            cache.update { map ->
                map.put(itemId, localData.toSimpleItemUiState())
            }
        }
        val remoteData = remoteDataAsync.await()
        cache.update { map ->
            map.put(itemId, remoteData.toSimpleItemUiState())
        }
        itemLocalDataSource.itemUpsert(remoteData.toItemDb())
    }

    companion object {
        private const val TAG = "StoriesRepository"
    }
}

fun ItemDb.toSimpleItemUiState(): SimpleItemUiState = makeSimpleItemUiState(
    id = ItemId(id),
    lastUpdate = lastUpdate,
    type = type,
    time = time,
    deleted = deleted,
    by = by,
    descendants = descendants,
    score = score,
    title = title,
    text = title,
    url = url,
    parent = parent?.let { ItemId(it) },
)

fun ItemApi.toSimpleItemUiState(): SimpleItemUiState = when (this) {
    is ItemApi.Comment -> makeSimpleItemUiState(
        id = id,
        type = "comment",
        time = time,
        deleted = deleted,
        by = by,
        parent = parent,
    )

    is ItemApi.Job -> makeSimpleItemUiState(
        id = id,
        type = "job",
        time = time,
        deleted = deleted,
        by = by,
        title = title,
        text = title,
        url = url,
    )

    is ItemApi.Poll -> makeSimpleItemUiState(
        id = id,
        type = "poll",
        time = time,
        deleted = deleted,
        by = by,
        descendants = descendants,
        score = score,
        title = title,
        text = title,
    )

    is ItemApi.PollOpt -> makeSimpleItemUiState(
        id = id,
        lastUpdate = null,
        type = "poll_opt",
        time = time,
        deleted = deleted,
        by = by,
        score = score,
        title = title,
        text = title,
    )

    is ItemApi.Story -> makeSimpleItemUiState(
        id = id,
        type = "story",
        time = time,
        deleted = deleted,
        by = by,
        descendants = descendants,
        score = score,
        title = title,
        text = title,
        url = url,
    )
}
