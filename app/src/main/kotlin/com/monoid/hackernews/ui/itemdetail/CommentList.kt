package com.monoid.hackernews.ui.itemdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.repo.ItemTreeRow
import com.monoid.hackernews.repo.ItemUiWithThreadDepth

@Composable
fun CommentList(
    itemListState: State<List<ItemTreeRow>?>,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    BoxWithConstraints(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
                    .calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            itemsIndexed(
                items = itemListState.value ?: emptyList(),
                key = { _, itemRow -> itemRow.itemId.long },
                contentType = { index, _ -> index == 0 },
            ) { index, itemRow ->
                val itemUiState: State<ItemUiWithThreadDepth?> = itemRow.itemUiFlow
                    .collectAsState(initial = null)

                if (index == 0) {
                    RootItem(
                        itemUiState = itemUiState,
                        onClickReply = { onClickReply(it) },
                        onClickUser = { onClickUser(it) },
                        onClickBrowser = { onClickBrowser(it) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    CommentItem(
                        itemUiState = itemUiState,
                        onClickUser = { onClickUser(it) },
                        onClickReply = { onClickReply(it) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
