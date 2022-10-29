package com.monoid.hackernews.wear.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.ItemListRow
import com.monoid.hackernews.shared.data.OrderedItem
import com.monoid.hackernews.shared.domain.LiveUpdateUseCase
import com.monoid.hackernews.wear.MainViewModel
import com.monoid.hackernews.wear.ui.itemlist.ItemList
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    title: String,
    orderedItemRepo: LiveUpdateUseCase<OrderedItem>,
    onSelectItemId: (ItemId?) -> Unit
) {
    val itemRows: State<List<ItemListRow>?> =
        remember {
            orderedItemRepo
                .getItems(
                    //context.resources.getInteger(R.integer.item_stale_minutes).toLong()
                    TimeUnit.MINUTES.toMillis(5L)
                )
                .map { orderedItems ->
                    mainViewModel.itemTreeRepository.itemUiList(orderedItems.map { it.itemId })
                }
        }.collectAsState(initial = null)

    val state = rememberScalingLazyListState()

    Scaffold(
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = {
            if (state.isScrollInProgress) {
                PositionIndicator(state)
            }
        },
        timeText = { TimeText() }
    ) {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "start"
        ) {
            composable(route = "start") {
                ItemList(
                    state = state,
                    title = title,
                    itemRows = itemRows,
                    onClickDetail = onSelectItemId,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
