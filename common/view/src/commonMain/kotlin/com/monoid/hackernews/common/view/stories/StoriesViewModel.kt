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

enum class StoryOrdering {
    Trending {
        override fun stories(repository: StoriesRepository): StateFlow<List<Item>?> =
            repository.trendingStories

        override suspend fun update(repository: StoriesRepository) {
            repository.updateTrendingStories()
        }
    },
    New {
        override fun stories(repository: StoriesRepository): StateFlow<List<Item>?> =
            repository.newStories

        override suspend fun update(repository: StoriesRepository) {
            repository.updateNewStories()
        }
    },
    Hot {
        override fun stories(repository: StoriesRepository): StateFlow<List<Item>?> =
            repository.hotStories

        override suspend fun update(repository: StoriesRepository) {
            repository.updateHotStories()
        }
    },
    Show {
        override fun stories(repository: StoriesRepository): StateFlow<List<Item>?> =
            repository.showStories

        override suspend fun update(repository: StoriesRepository) {
            repository.updateShowStories()
        }
    },
    Ask {
        override fun stories(repository: StoriesRepository): StateFlow<List<Item>?> =
            repository.askStories

        override suspend fun update(repository: StoriesRepository) {
            repository.updateAskStories()
        }
    },
    Jobs {
        override fun stories(repository: StoriesRepository): StateFlow<List<Item>?> =
            repository.jobStories

        override suspend fun update(repository: StoriesRepository) {
            repository.updateJobStories()
        }
    },
    ;

    abstract fun stories(repository: StoriesRepository): StateFlow<List<Item>?>
    abstract suspend fun update(repository: StoriesRepository)
}

@KoinViewModel
class StoriesViewModel(
    private val logger: LoggerAdapter,
    private val storiesRepository: StoriesRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val storyOrdering = StoryOrdering.Trending

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
            storyOrdering.stories(storiesRepository).collect {
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
                storyOrdering.update(storiesRepository)
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

    fun toggleUpvoted(item: Item): Job = viewModelScope.launch(coroutineExceptionHandler) {
        if (settingsRepository.isLoggedIn.not()) {
            _events.send(Event.NavigateLogin)
            return@launch
        }
        runCatching {
            storiesRepository.toggleUpvoted(item)
        }.doOnErrorThenThrow {
            _events.send(Event.Error(it.message))
        }
    }
}

private const val TAG = "StoriesViewModel"
