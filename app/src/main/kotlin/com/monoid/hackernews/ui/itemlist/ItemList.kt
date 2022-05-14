package com.monoid.hackernews.ui.itemlist

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.repo.ItemRepo

@Composable
fun ItemList(
    itemRows: State<List<ItemRepo.ItemRow>?>,
    selectedItem: ItemId?,
    onClickDetail: (ItemId?) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = listState,
        contentPadding = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Bottom)
            .asPaddingValues(),
        modifier = modifier,
    ) {
        items(itemRows.value ?: emptyList(), { it.itemId.long }) { itemRow ->
            Item(
                itemUiState = remember(itemRow.itemId) { itemRow.itemUiFlow }
                    .collectAsState(initial = null),
                isSelected = itemRow.itemId == selectedItem,
                onClickDetail = { onClickDetail(it) },
                onClickReply = { onClickReply(it) },
                onClickUser = { onClickUser(it) },
                onClickBrowser = { onClickBrowser(it) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
