package com.monoid.hackernews.wear.view.itemlist

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.metrics.performance.PerformanceMetricsState
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemListRow
import com.monoid.hackernews.common.util.rememberMetricsStateHolder
import com.monoid.hackernews.wear.BuildConfig
import kotlinx.coroutines.launch

@Composable
fun ItemList(
    state: ScalingLazyListState,
    title: String,
    itemRows: State<List<ItemListRow>?>,
    onClickDetail: (ItemId?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (BuildConfig.DEBUG.not()) {
        val metricsStateHolder: PerformanceMetricsState.Holder =
            rememberMetricsStateHolder()

        // Reporting scrolling state from compose should be done from side effect to prevent
        // recomposition.
        LaunchedEffect(metricsStateHolder) {
            snapshotFlow { state.isScrollInProgress }.collect { isScrolling ->
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
        contentPadding = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Bottom)
            .asPaddingValues(),
//        autoCentering = AutoCenteringParams,
    ) {
        item {
            ListHeader {
                Text(text = title)
            }
        }
        items(itemRows.value.orEmpty(), { it.itemId.long }) { itemRow ->
            Item(
                itemUiState = remember(itemRow.itemId) { itemRow.itemUiFlow }
                    .collectAsState(initial = null),
                onClickDetail = { onClickDetail(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
