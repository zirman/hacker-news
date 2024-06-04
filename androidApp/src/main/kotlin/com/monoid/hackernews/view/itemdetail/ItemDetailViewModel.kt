package com.monoid.hackernews.view.itemdetail

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
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
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.SimpleItemUiState
import com.monoid.hackernews.common.data.StoriesRepository
import com.monoid.hackernews.common.data.makeSimpleItemUiState
import com.monoid.hackernews.common.injection.LoggerAdapter
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.WeakHashMap

class ItemDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val logger: LoggerAdapter,
    private val repository: StoriesRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = true,
        val item: SimpleItemUiState? = null,
        val commentMap: PersistentMap<ItemId, Int>? = null,
        val commentItems: PersistentList<SimpleItemUiState>? = null,
    )

    sealed interface Event {
        data class Error(val message: String?) : Event
    }

    private val itemId: ItemId = savedStateHandle[ITEM_ID]!!

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

    private val updateItemJob = WeakHashMap<ItemId, Job>()

    init {
        viewModelScope.launch(context) {
            repository.cache
                .map { cache -> cache[itemId] }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { item ->
                    // ensures collection doesn't suspend
                    fun collect() {
                        val kids = item.kids.orEmpty()
                        val commentMap = persistentMapOf(
                            *Array(kids.size) { index ->
                                kids[index] to index
                            },
                        )
                        val commentItems = persistentListOf(
                            *Array(kids.size) { index ->
                                val x = repository.getCachedItem(kids[index])
                                makeSimpleItemUiState(
                                    id = kids[index],
                                    lastUpdate = x?.lastUpdate,
                                    type = x?.type,
                                    time = x?.time,
                                    deleted = x?.deleted,
                                    by = x?.by,
                                    descendants = x?.descendants,
                                    score = x?.score,
                                    title = x?.title,
                                    text = x?.text,
                                    url = x?.url,
                                    parent = x?.parent,
                                    kids = x?.kids,
                                    isUpvote = x?.isUpvote,
                                    isFavorite = x?.isFavorite,
                                    isFlag = x?.isFlag,
                                    isExpanded = x?.isExpanded,
                                    isFollowed = x?.isFollowed,
                                )
                            },
                        )
                        _uiState.update { uiState ->
                            uiState.copy(
                                item = item,
                                commentMap = commentMap,
                                commentItems = commentItems,
                            )
                        }
                    }
                    collect()
                }

            repository.updateItem(itemId)
        }
    }

    fun updateItem(itemId: ItemId): Job {
        var job = updateItemJob[itemId]
        if (job?.isActive != true) {
            job = viewModelScope.launch(context) {
                val commentItem = repository.getItem(itemId)
                _uiState.update { uiState ->
                    val commentIndex = uiState.commentMap?.get(itemId)
                    if (commentIndex != null) {
                        uiState.copy(
                            commentItems = uiState.commentItems?.set(commentIndex, commentItem),
                        )
                    } else {
                        uiState
                    }
                }
            }
            updateItemJob[itemId] = job
        }
        return job
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val ITEM_ID = "item_id"

        @Composable
        fun create(itemId: ItemId): ItemDetailViewModel = koinViewModel(
            key = itemId.toString(),
            extras = itemId.toExtras(),
        )

        @Composable
        private fun ItemId.toExtras(): CreationExtras = MutableCreationExtras().apply {
            val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
            set(
                DEFAULT_ARGS_KEY,
                bundleOf(
                    ITEM_ID to this@toExtras,
                ),
            )
            set(VIEW_MODEL_STORE_OWNER_KEY, viewModelStoreOwner)
            set(SAVED_STATE_REGISTRY_OWNER_KEY, viewModelStoreOwner as SavedStateRegistryOwner)
        }
    }
}
