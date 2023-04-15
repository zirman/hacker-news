package com.monoid.hackernews.view.itemlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.metrics.performance.PerformanceMetricsState
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.BuildConfig
import com.monoid.hackernews.common.data.ItemListRow
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.util.rememberMetricsStateHolder
import kotlinx.coroutines.launch

@Composable
fun ItemList(
    itemRows: List<ItemListRow>?,
    paddingValues: PaddingValues,
    onClickDetail: (ItemId?) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    if (BuildConfig.DEBUG.not()) {
        val metricsStateHolder: PerformanceMetricsState.Holder =
            rememberMetricsStateHolder()

        // Reporting scrolling state from compose should be done from side effect to prevent
        // recomposition.
        LaunchedEffect(metricsStateHolder) {
            snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
                metricsStateHolder.state!!.run {
                    if (isScrolling) {
                        putState("ItemList", "Scrolling")
                    } else {
                        removeState("ItemList")
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = paddingValues
    ) {
        items(itemRows.orEmpty(), { it.itemId.long }) { itemRow ->
            val coroutineScope = rememberCoroutineScope()

            val itemUiState = remember(itemRow.itemId) { itemRow.itemUiFlow(coroutineScope) }
                .collectAsStateWithLifecycle()

            Item(
                itemUi = itemUiState.value,
                onClickDetail = { itemUiState.value?.item?.id?.let { onClickDetail(ItemId(it)) } },
                onClickReply = { itemUiState.value?.item?.id?.let { onClickReply(ItemId(it)) } },
                onClickUser = { onClickUser(it) },
                onClickBrowser = { onClickBrowser(itemUiState.value?.item?.url) },
                onClickUpvote = {
                    coroutineScope.launch {
                        itemUiState.value?.toggleUpvote(onNavigateLogin)
                    }
                },
                onClickFavorite = {
                    coroutineScope.launch {
                        itemUiState.value?.toggleFavorite(onNavigateLogin)
                    }
                },
                onClickFlag = {
                    coroutineScope.launch {
                        itemUiState.value?.toggleFlag(onNavigateLogin)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }
    }
}
