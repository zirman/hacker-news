package com.monoid.hackernews.wear.view.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemListRow
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.OrderedItem
import com.monoid.hackernews.common.domain.LiveUpdateUseCase
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.wear.view.itemlist.ItemList
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    itemTreeRepository: ItemTreeRepository,
    title: String,
    orderedItemRepo: LiveUpdateUseCase<OrderedItem>,
    onSelectItemId: (ItemId?) -> Unit,
) {
    val itemStaleMinutes = integerResource(id = R.integer.item_stale_minutes)

    val itemRows: State<List<ItemListRow>?> =
        remember(itemStaleMinutes) {
            orderedItemRepo
                .getItems(TimeUnit.MINUTES.toMillis(itemStaleMinutes.toLong()))
                .map { orderedItems ->
                    itemTreeRepository.itemUiList(orderedItems.map { it.itemId })
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
