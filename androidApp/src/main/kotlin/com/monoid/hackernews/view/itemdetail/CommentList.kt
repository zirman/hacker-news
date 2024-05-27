package com.monoid.hackernews.view.itemdetail

//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.metrics.performance.PerformanceMetricsState
//import com.monoid.hackernews.common.api.ItemId
//import com.monoid.hackernews.common.data.ItemTreeRow
//import com.monoid.hackernews.common.data.ItemUiWithThreadDepth
//import com.monoid.hackernews.common.data.LoginAction
//import com.monoid.hackernews.common.data.Username
//import com.monoid.hackernews.common.util.rememberMetricsStateHolder
//
//@Composable
//fun CommentList(
//    itemTreeRows: List<ItemTreeRow>?,
//    onClickUser: (Username) -> Unit,
//    onClickReply: (ItemId) -> Unit,
//    onClickBrowser: (String) -> Unit,
//    onNavigateLogin: (LoginAction) -> Unit,
//    modifier: Modifier = Modifier,
//    listState: LazyListState = rememberLazyListState(),
//) {
//    Box(modifier = modifier) {
//        if (false) { // TODO: prod build
//            val metricsStateHolder: PerformanceMetricsState.Holder =
//                rememberMetricsStateHolder()
//
//            // Reporting scrolling state from compose should be done from side effect to prevent
//            // recomposition.
//            LaunchedEffect(metricsStateHolder) {
//                snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
//                    metricsStateHolder.state!!.run {
//                        if (isScrolling) {
//                            putState("CommentList", "Scrolling")
//                        } else {
//                            removeState("CommentList")
//                        }
//                    }
//                }
//            }
//        }
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            state = listState,
//            contentPadding = PaddingValues(
//                start = 16.dp,
//                top = 16.dp,
//                end = 16.dp,
//                bottom = 16.dp,
//            ),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            itemsIndexed(
//                items = itemTreeRows.orEmpty(),
//                key = { _, itemRow -> itemRow.itemId.long },
//                contentType = { index, _ -> index == 0 },
//            ) { index, itemRow ->
//                val coroutineScope = rememberCoroutineScope()
//
//                val itemUi: ItemUiWithThreadDepth? by remember {
//                    itemRow.itemUiFlow(coroutineScope)
//                }.collectAsStateWithLifecycle(null)
//
//                if (index == 0) {
//                    RootItem(
//                        itemUi = itemUi,
//                        onClickReply = onClickReply,
//                        onClickUser = onClickUser,
//                        onClickBrowser = onClickBrowser,
//                        onNavigateLogin = onNavigateLogin,
//                        modifier = Modifier.fillMaxWidth(),
//                    )
//                } else {
//                    CommentItem(
//                        itemUi = itemUi,
//                        onClickUser = onClickUser,
//                        onClickReply = onClickReply,
//                        onNavigateLogin = onNavigateLogin,
//                        modifier = Modifier.fillMaxWidth(),
//                    )
//                }
//            }
//        }
//    }
//}
