package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.DefaultDispatcherQualifier
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.WeakHashMap
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.StoriesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
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
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Named

@KoinViewModel
class ItemDetailViewModel(
    savedStateHandle: SavedStateHandle,
    @Named(type = DefaultDispatcherQualifier::class)
    defaultDispatcher: CoroutineDispatcher,
    private val logger: LoggerAdapter,
    private val repository: StoriesRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = true,
        val comments: List<ThreadItemUiState>? = null,
    )

    data class ThreadItemUiState(val item: Item, val depth: Int, val descendants: Int)

    sealed interface Event {
        data class Error(val message: String?) : Event
    }

    private val itemId: ItemId by lazy { ItemId(checkNotNull(savedStateHandle[ITEM_ID])) }

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
            withContext(defaultDispatcher) {
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
                    item = repository.cache.value[itemId] ?: Item(id = itemId),
                    depth = 0,
                    descendants = 0,
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
