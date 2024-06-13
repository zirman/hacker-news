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
import org.koin.androidx.compose.koinViewModel
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

/**
 * Traverses an Item and it's descendants and builds a list with depth and descendant count.
 */
private fun Map<ItemId, Item>.traverse(
    itemId: ItemId,
): List<ItemDetailViewModel.ThreadItemUiState> = buildList {
    fun recurDescendants(itemId: ItemId): Int = 1 + this@traverse[itemId]?.kids.orEmpty().sumOf {
        recurDescendants(it)
    }

    fun recur(itemId: ItemId, depth: Int): Int {
        val item = this@traverse[itemId] ?: makeItem(id = itemId)
        val kids = item.kids.orEmpty()
        return 1 + if (item.expanded) {
            var decendents = 0
            for (i in kids.size - 1 downTo 0) {
                decendents += recur(kids[i], depth = depth + 1)
            }
            add(
                ItemDetailViewModel.ThreadItemUiState(
                    item = item,
                    depth = depth,
                    decendents = decendents,
                )
            )
            decendents
        } else {
            var decendents = 0
            for (i in kids.size - 1 downTo 0) {
                decendents += recurDescendants(kids[i])
            }
            add(
                ItemDetailViewModel.ThreadItemUiState(
                    item = item,
                    depth = depth,
                    decendents = decendents,
                )
            )
            decendents
        }
    }
    recur(itemId, depth = 0)
}.asReversed()
