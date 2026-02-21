package com.monoid.hackernews.wear.view.itemlist

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import com.monoid.hackernews.common.data.api.ItemId
import kotlinx.coroutines.launch

@Composable
fun ItemList(
    state: ScalingLazyListState,
    title: String,
    onClickDetail: (ItemId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (true) { // TODO: debug config
//        val metricsStateHolder: PerformanceMetricsState.Holder =
//            rememberMetricsStateHolder()

        // Reporting scrolling state from compose should be done from side effect to prevent
        // recomposition.
//        LaunchedEffect(metricsStateHolder) {
//            snapshotFlow { state.isScrollInProgress }.collect { isScrolling ->
//                checkNotNull(metricsStateHolder.state).run {
//                    if (isScrolling) {
//                        putState("ItemList", "Scrolling")
//                    } else {
//                        removeState("ItemList")
//                    }
//                }
//            }
//        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val coroutineScope = rememberCoroutineScope()

    ScalingLazyColumn(
        modifier = modifier
            .onRotaryScrollEvent { rotaryScrollEvent ->
                coroutineScope.launch {
                    state.scrollBy(rotaryScrollEvent.verticalScrollPixels)
                }

                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        state = state,
    ) {
        item {
            ListHeader {
                Text(text = title)
            }
        }

//        items(itemRows, { it.itemId.long }) { itemRow ->
//            val itemUi by remember(itemRow.itemId) { itemRow.itemUiFlow(coroutineScope) }
//                .collectAsStateWithLifecycle()
//            Item(
//                itemUi = itemUi,
//                onClickDetail = { onClickDetail(it) },
//                modifier = Modifier.fillMaxWidth(),
//            )
//        }
    }
}
