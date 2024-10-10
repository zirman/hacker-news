package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType

@Composable
fun ItemDetailPane(
    itemId: ItemId,
    onOpenBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = createItemDetailViewModel(itemId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val commentItems = uiState.comments
    LazyColumn(
        state = viewModel.lazyListState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
    ) {
        itemsIndexed(
            items = commentItems.orEmpty(),
            key = { _, item -> item.item.id.long },
            contentType = { _, item -> item.item.type },
        ) { index, item ->
            when (item.item.type ?: if (index == 0) ItemType.Story else ItemType.Comment) {
                ItemType.Comment -> {
                    ItemComment(
                        threadItem = item,
                        onVisible = viewModel::updateItem,
                    )
                }

                ItemType.Story, ItemType.Job, ItemType.Poll, ItemType.PollOpt -> {
                    ItemDetail(item.item, onOpenBrowser)
                }
            }
        }
    }
}
