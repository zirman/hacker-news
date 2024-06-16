package com.monoid.hackernews.view.itemdetail

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.Item
import com.monoid.hackernews.common.data.StoriesRepository
import com.monoid.hackernews.common.data.makeItem
import com.monoid.hackernews.common.injection.LoggerAdapter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.WeakHashMap

class ItemDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val logger: LoggerAdapter,
    private val repository: StoriesRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = true,
        val comments: List<ThreadItemUiState>? = null,
    )

    data class ThreadItemUiState(val item: Item, val depth: Int, val decendents: Int)

    sealed interface Event {
        data class Error(val message: String?) : Event
    }

    private val itemId: ItemId by lazy { savedStateHandle[ITEM_ID]!! }

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    private val loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        loading,
        repository.cache.map { cache ->
            withContext(Dispatchers.Default) {
                cache.traverse(itemId)
            }
        },
        ::UiState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = UiState(
            loading = false,
            comments = listOf(
                ThreadItemUiState(
                    item = repository.cache.value[itemId] ?: makeItem(id = itemId),
                    depth = 0,
                    decendents = 0,
                ),
            ),
        ),
    )

    private val _events: Channel<Event> = Channel()
    val events = _events.receiveAsFlow()

    val lazyListState = LazyListState()

    private val updateItemJob = WeakHashMap<ItemId, Job>()

    fun updateItem(itemId: ItemId): Job {
        return updateItemJob[itemId]?.takeIf { it.isActive }
            ?: viewModelScope.launch(context) { repository.updateItem(itemId) }
                .also { updateItemJob[itemId] = it }
    }

    fun toggleCommentExpanded(itemId: ItemId): Job = viewModelScope.launch(context) {
        repository.itemToggleExpanded(itemId)
    }

    companion object {
        private const val TAG = "HomeViewModel"
        const val ITEM_ID = "item_id"
    }
}
