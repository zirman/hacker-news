package com.monoid.hackernews.common.view.favorites

import androidx.compose.runtime.Composable
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.savedState
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.core.coroutines.doOnErrorThenThrow
import com.monoid.hackernews.common.data.WeakHashMap
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.StoriesRepository
import com.monoid.hackernews.common.data.model.Username
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class FavoritesViewModel(
    handle: SavedStateHandle,
    private val logger: LoggerAdapter,
    private val storiesRepository: StoriesRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = false,
        val isRefreshing: Boolean = false,
        val itemsList: List<Item>? = null,
    )

    sealed interface Event {
        data class Error(val message: String?) : Event
        data object NavigateLogin : Event
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events: Channel<Event> = Channel()
    val events: ReceiveChannel<Event> = _events

    private val username = Username(checkNotNull(handle[USERNAME]))

    init {
        viewModelScope.launch {
            storiesRepository.favoriteStories(username).collect {
                _uiState.update { uiState ->
                    uiState.copy(itemsList = it)
                }
            }
        }
//        updateItems()
    }

    private var updateItemsJob: Job? = null

    private fun updateItems(): Job = updateItemsJob
        .takeIf { it?.isActive == true } ?: viewModelScope
        .launch(coroutineExceptionHandler) {
            try {
                _uiState.update { it.copy(loading = true) }
//                storyOrdering.update(storiesRepository)
            } catch (throwable: Throwable) {
                currentCoroutineContext().ensureActive()
                _events.send(Event.Error(throwable.message))
                throw throwable
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }
        .also {
            updateItemsJob = it
        }

    private val updateItemJob: MutableMap<ItemId, Job> = WeakHashMap()

    fun updateItem(item: Item): Job {
        updateItemJob[item.id]?.takeIf { it.isActive }?.run { return this }
        return viewModelScope
            .launch(coroutineExceptionHandler) {
                try {
                    storiesRepository.updateItem(item.id)
                } catch (throwable: Throwable) {
                    currentCoroutineContext().ensureActive()
                    _events.send(Event.Error(throwable.message))
                    throw throwable
                }
            }
            .also {
                updateItemJob[item.id] = it
            }
    }

    private var refreshItemsJob: Job? = null

    fun refreshItems(): Job {
        refreshItemsJob?.takeIf { it.isActive }?.run { return this }
        return viewModelScope
            .launch {
                try {
                    _uiState.update { it.copy(isRefreshing = true) }
                    updateItems().join()
                } finally {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
            .also { refreshItemsJob = it }
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
        @Composable
        fun extras(username: Username): CreationExtras = MutableCreationExtras().apply {
            val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
            set(
                DEFAULT_ARGS_KEY,
                savedState {
                    putString(USERNAME, username.string)
                },
            )
            set(VIEW_MODEL_STORE_OWNER_KEY, viewModelStoreOwner)
            set(SAVED_STATE_REGISTRY_OWNER_KEY, viewModelStoreOwner as SavedStateRegistryOwner)
        }
    }
}

private const val TAG = "FavoritesViewModel"
private const val USERNAME = "USERNAME"
