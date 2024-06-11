package com.monoid.hackernews.view.profile

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.Item
import com.monoid.hackernews.common.injection.LoggerAdapter
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.androidx.compose.koinViewModel

class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val logger: LoggerAdapter,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = true,
        val item: Item? = null,
        val commentMap: PersistentMap<ItemId, Int>? = null,
        val commentItems: PersistentList<Item>? = null,
    )

    sealed interface Event {
        data class Error(val message: String?) : Event
    }

    private val context = CoroutineExceptionHandler { _, throwable ->
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

    val lazyListState = LazyListState()

    companion object {
        private const val TAG = "ProfileViewModel"

        @Composable
        fun create(): ProfileViewModel = koinViewModel()
    }
}
