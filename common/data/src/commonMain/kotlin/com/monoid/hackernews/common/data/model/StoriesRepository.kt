package com.monoid.hackernews.common.data.model

import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.api.getItem
import com.monoid.hackernews.common.data.api.getTopStories
import com.monoid.hackernews.common.data.api.upvoteItem
import com.monoid.hackernews.common.data.room.ItemDao
import com.monoid.hackernews.common.data.room.TopStoryDao
import com.monoid.hackernews.common.data.room.TopStoryDb
import io.ktor.client.HttpClient
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
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
import org.koin.core.annotation.Single

@Single
class StoriesRepository(
    private val logger: LoggerAdapter,
    private val remoteDataSource: HttpClient,
    private val topStoryLocalDataSource: TopStoryDao,
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
                cache[id] ?: Item(id = id)
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

    suspend fun toggleUpvoted(item: Item) {
        val upvoted = item.upvoted != false
        // optimistically update the ui state
        _cache.update { cache ->
            cache.put(
                key = item.id,
                value = cache.getValue(item.id).copy(upvoted = upvoted),
            )
        }
        try {
            remoteDataSource.upvoteItem(
                settings = settingsRepository.preferences.value,
                itemId = item.id,
                flag = upvoted,
            )
        } catch (throwable: Throwable) {
            currentCoroutineContext().ensureActive()
            // revert ui state if an error occurred
            _cache.update { cache ->
                cache.put(
                    key = item.id,
                    value = cache.getValue(item.id).copy(upvoted = upvoted.not()),
                )
            }
            throw throwable
        }
        // update the local data store
        itemLocalDataSource.setUpvotedByItemId(itemId = item.id.long, upvoted = upvoted)
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
