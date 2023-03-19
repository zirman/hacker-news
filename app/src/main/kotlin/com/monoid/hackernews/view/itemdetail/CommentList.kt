package com.monoid.hackernews.view.itemdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.metrics.performance.PerformanceMetricsState
import com.monoid.hackernews.BuildConfig
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemTreeRow
import com.monoid.hackernews.common.data.ItemUiWithThreadDepth
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.util.rememberMetricsStateHolder

@Composable
fun CommentList(
    itemTreeRows: List<ItemTreeRow>?,
    paddingValues: PaddingValues,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    BoxWithConstraints(modifier = modifier) {
        if (BuildConfig.DEBUG.not()) {
            val metricsStateHolder: PerformanceMetricsState.Holder =
                rememberMetricsStateHolder()

            // Reporting scrolling state from compose should be done from side effect to prevent
            // recomposition.
            LaunchedEffect(metricsStateHolder) {
                snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
                    metricsStateHolder.state!!.run {
                        if (isScrolling) {
                            putState("CommentList", "Scrolling")
                        } else {
                            removeState("CommentList")
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp + paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                top = 16.dp + paddingValues.calculateTopPadding(),
                end = 16.dp + paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                bottom = 16.dp + paddingValues.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = itemTreeRows ?: emptyList(),
                key = { _, itemRow -> itemRow.itemId.long },
                contentType = { index, _ -> index == 0 }
            ) { index, itemRow ->
                val itemUiState: State<ItemUiWithThreadDepth?> =
                    remember { itemRow.itemUiFlow }.collectAsState(initial = null)

                if (index == 0) {
                    RootItem(
                        itemUiState = itemUiState,
                        onClickReply = onClickReply,
                        onClickUser = onClickUser,
                        onClickBrowser = onClickBrowser,
                        onNavigateLogin = onNavigateLogin,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    CommentItem(
                        itemUiState = itemUiState,
                        onClickUser = onClickUser,
                        onClickReply = onClickReply,
                        onNavigateLogin = onNavigateLogin,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
