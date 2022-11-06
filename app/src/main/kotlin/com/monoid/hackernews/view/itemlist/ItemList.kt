package com.monoid.hackernews.view.itemlist

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.metrics.performance.PerformanceMetricsState
import com.monoid.hackernews.BuildConfig
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.ItemListRow
import com.monoid.hackernews.shared.data.LoginAction
import com.monoid.hackernews.shared.data.Username
import com.monoid.hackernews.shared.util.rememberMetricsStateHolder

@Composable
fun ItemList(
    itemRows: State<List<ItemListRow>?>,
    selectedItem: ItemId?,
    onClickDetail: (ItemId?) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
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
        contentPadding = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Bottom)
            .asPaddingValues(),
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
                onNavigateLogin = { onNavigateLogin(it) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
