package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.core.coroutines.doOnErrorThenThrow
import com.monoid.hackernews.common.data.WeakHashMap
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.StoriesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class StoriesViewModel(
    private val logger: LoggerAdapter,
    private val repository: StoriesRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = false,
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
    val events = _events.receiveAsFlow()

    val listState = LazyListState()

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.topStories.collect {
                _uiState.update { uiState ->
                    uiState.copy(itemsList = it)
                }
            }
        }
        updateItems()
    }

    private var updateItemsJob: Job? = null

    private fun updateItems(): Job = updateItemsJob
        .takeIf { it?.isActive == true } ?: viewModelScope
        .launch(coroutineExceptionHandler) {
            try {
                _uiState.update { it.copy(loading = true) }
                repository.updateBestStories()
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
                    repository.updateItem(item.id)
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

    fun toggleUpvoted(item: Item): Job = viewModelScope.launch(coroutineExceptionHandler) {
        if (settingsRepository.isLoggedIn.not()) {
            _events.send(Event.NavigateLogin)
            return@launch
        }
        runCatching {
            repository.toggleUpvoted(item)
        }.doOnErrorThenThrow {
            _events.send(Event.Error(it.message))
        }
    }

    companion object {
        private const val TAG = "StoriesViewModel"
    }
}
