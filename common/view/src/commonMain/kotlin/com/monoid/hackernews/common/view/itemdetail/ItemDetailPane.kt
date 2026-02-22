@file:OptIn(ExperimentalFoundationApi::class)

package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.ItemType
import com.monoid.hackernews.common.data.model.Username
import io.ktor.http.Url
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ItemDetailPane(
    itemId: ItemId,
    onClickUrl: (Url) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickLogin: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val viewModel = createItemDetailViewModel(itemId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(itemId) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                viewModel.updateItem(itemId)
                delay(5.seconds)
            }
        }
    }
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            for (event in viewModel.events) {
                when (event) {
                    is ItemDetailViewModel.Event.Error -> {
                        // TODO
                    }

                    is ItemDetailViewModel.Event.NavigateLogin -> {
                        onClickLogin()
                    }
                }
            }
        }
    }
    LazyColumn(
        state = rememberLazyListState(
            cacheWindow = LazyLayoutCacheWindow(ahead = 500.dp),
        ),
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = uiState.comments.orEmpty(),
            key = { _, item -> item.item.id.long },
            contentType = { _, item -> item.item.type },
        ) { index, item ->
            when (item.item.type ?: if (index == 0) ItemType.Story else ItemType.Comment) {
                ItemType.Comment -> {
                    ItemComment(
                        threadItem = item,
                        onClickUser = onClickUser,
                        onClickReply = onClickReply,
                        onVisible = viewModel::updateItem,
                        onClick = viewModel::toggleCommentExpanded,
                    )
                }

                ItemType.Story, ItemType.Job, ItemType.Poll, ItemType.PollOpt -> {
                    ItemDetail(
                        item = item.item,
                        onClickUser = onClickUser,
                        onClickUrl = onClickUrl,
                        onClickReply = onClickReply,
                        onClickUpvote = viewModel::toggleUpvote,
                        onClickFavorite = viewModel::toggleFavorite,
                        onClickFollow = viewModel::toggleFollow,
                        onClickFlag = viewModel::toggleFlagged,
                    )
                }
            }
        }
    }
}
