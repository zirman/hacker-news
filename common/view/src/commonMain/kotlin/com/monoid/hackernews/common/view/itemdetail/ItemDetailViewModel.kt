package com.monoid.hackernews.common.view.itemdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.coroutines.doOnErrorThenThrow
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ViewModelKey
import com.monoid.hackernews.common.core.metro.ViewModelScope
import com.monoid.hackernews.common.data.WeakHashMap
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.StoriesRepository
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(ItemDetailViewModel::class)
@Inject
class ItemDetailViewModel(
    savedStateHandle: SavedStateHandle,
//    @DefaultDispatcherQualifier
//    defaultDispatcher: CoroutineDispatcher,
    private val logger: LoggerAdapter,
    private val storiesRepository: StoriesRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = true,
        val comments: List<ThreadItemUiState>? = null,
    )

    data class ThreadItemUiState(val item: Item, val depth: Int, val descendants: Int)

    sealed interface Event {
        data class Error(val message: String?) : Event
        data object NavigateLogin : Event
    }

    private val itemId: ItemId by lazy { ItemId(checkNotNull(savedStateHandle[ITEM_ID])) }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    private val loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        loading,
        storiesRepository.cache.map { cache ->
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
                    item = storiesRepository.cache.value[itemId] ?: Item(id = itemId),
                    depth = 0,
                    descendants = 0,
                ),
            ),
        ),
    )
    private val _events: Channel<Event> = Channel()
    val events: ReceiveChannel<Event> = _events
    private val updateItemJob = WeakHashMap<ItemId, Job>()

    fun updateItem(itemId: ItemId): Job = updateItemJob[itemId]
        ?.takeIf { it.isActive }
        ?: viewModelScope
            .launch(coroutineExceptionHandler) {
                storiesRepository.updateItem(
                    itemId
                )
            }
            .also { updateItemJob[itemId] = it }

    fun toggleCommentExpanded(itemId: ItemId): Job =
        viewModelScope.launch(coroutineExceptionHandler) {
            storiesRepository.itemToggleExpanded(itemId)
        }

    fun toggleUpvote(item: Item): Job = viewModelScope.launch(coroutineExceptionHandler) {
        if (settingsRepository.isLoggedIn.not()) {
            _events.send(Event.NavigateLogin)
            return@launch
        }
        runCatching {
            storiesRepository.toggleUpvote(item)
        }.doOnErrorThenThrow {
            _events.send(Event.Error(it.message))
        }
    }

    fun toggleFavorite(item: Item): Job = viewModelScope.launch(coroutineExceptionHandler) {
        if (settingsRepository.isLoggedIn.not()) {
            _events.send(Event.NavigateLogin)
            return@launch
        }
        runCatching {
            storiesRepository.toggleFavorite(item)
        }.doOnErrorThenThrow {
            _events.send(Event.Error(it.message))
        }
    }

    fun toggleFollow(item: Item): Job = viewModelScope.launch(coroutineExceptionHandler) {
        if (settingsRepository.isLoggedIn.not()) {
            _events.send(Event.NavigateLogin)
            return@launch
        }
        runCatching {
            storiesRepository.toggleFollow(item)
        }.doOnErrorThenThrow {
            _events.send(Event.Error(it.message))
        }
    }

    fun toggleFlagged(item: Item): Job = viewModelScope.launch(coroutineExceptionHandler) {
        if (settingsRepository.isLoggedIn.not()) {
            _events.send(Event.NavigateLogin)
            return@launch
        }
        runCatching {
            storiesRepository.toggleFlagged(item)
        }.doOnErrorThenThrow {
            _events.send(Event.Error(it.message))
        }
    }

    companion object {
        const val ITEM_ID = "item_id"
    }
}

private const val TAG = "ItemDetailViewModel"
