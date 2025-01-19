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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType
import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ItemDetailPane(
    itemId: ItemId,
    onOpenBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = createItemDetailViewModel(itemId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(itemId) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                viewModel.updateItem(itemId)
                delay(5.toDuration(DurationUnit.SECONDS))
            }
        }
    }
    LazyColumn(
        state = viewModel.lazyListState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
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
                        onClickUser = {},
                        onClickReply = {},
                        onNavigateLogin = {},
                        onVisible = viewModel::updateItem,
                        onClick = viewModel::toggleCommentExpanded,
                    )
                }

                ItemType.Story, ItemType.Job, ItemType.Poll, ItemType.PollOpt -> {
                    ItemDetail(
                        item = item.item,
                        onOpenBrowser = onOpenBrowser,
                    )
                }
            }
        }
    }
}
