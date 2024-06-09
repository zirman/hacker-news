package com.monoid.hackernews.view.itemdetail

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.api.ItemId

@Composable
fun ItemDetailPane(
    itemId: ItemId,
    onOpenBrowser: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = ItemDetailViewModel.create(itemId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.updateItem(itemId)
    }
    val commentItems = uiState.comments

    LazyColumn(
        state = viewModel.lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
    ) {
        itemsIndexed(
            items = commentItems.orEmpty(),
            key = { _, item -> item.id.long },
            contentType = { _, item -> item.type },
        ) { index, item ->
            when (item.type ?: if (index == 0) "story" else "comment") {
                "comment" -> {
                    ItemComment(
                        itemUi = item,
                        onClickUser = {},
                        onClickReply = {},
                        onNavigateLogin = {},
                        onVisible = viewModel::updateItem,
                        onClick = viewModel::toggleCommentExpanded,
                    )
                }

                "story", "job", "poll", "pollopt" -> {
                    ItemDetail(item, onOpenBrowser)
                }
            }
        }
    }
}
