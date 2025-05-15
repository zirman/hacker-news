package com.monoid.hackernews.common.view.comment

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
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.CommentRepository
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.StoriesRepository
import com.monoid.hackernews.common.data.room.CommentDb
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel

@KoinViewModel
class CommentViewModel(
    savedStateHandle: SavedStateHandle,
    private val commentRepository: CommentRepository,
    storiesRepository: StoriesRepository,
) : ViewModel() {
    private val parentId = ItemId(checkNotNull(savedStateHandle[PARENT_ID]))

    data class UiState(
        val parentComment: Item?,
        val loading: Boolean = false,
        val text: String = "",
    )

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState(parentComment = storiesRepository.cache.value[parentId]))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(text = commentRepository.comment(parentId).text)
            }
        }
    }

    suspend fun comment(): CommentDb = commentRepository.comment(parentId)

    fun updateComment(text: String): Job = viewModelScope.launch {
        _uiState.update { it.copy(text = text) }
        commentRepository.updateComment(parentId = parentId, string = text)
    }

    var job: Job? = null

    fun sendComment(): Job {
        job?.takeIf { it.isActive }?.run { return this }
        return viewModelScope.launch {
            try {
                _uiState.update { it.copy(loading = true) }
                commentRepository.sendComment(parentId = parentId)
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }.also {
            job = it
        }
    }

    companion object {
        const val PARENT_ID = "PARENT_ID"
    }
}

@Composable
fun createCommentViewModel(parentId: ItemId): CommentViewModel = koinViewModel(
    key = parentId.toString(),
    extras = parentId.toCommentViewModelExtras(),
)

@Composable
internal fun ItemId.toCommentViewModelExtras(): CreationExtras = MutableCreationExtras().apply {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    set(
        DEFAULT_ARGS_KEY,
        savedState {
            putLong(CommentViewModel.PARENT_ID, this@toCommentViewModelExtras.long)
        },
    )
    set(VIEW_MODEL_STORE_OWNER_KEY, viewModelStoreOwner)
    set(SAVED_STATE_REGISTRY_OWNER_KEY, viewModelStoreOwner as SavedStateRegistryOwner)
}
