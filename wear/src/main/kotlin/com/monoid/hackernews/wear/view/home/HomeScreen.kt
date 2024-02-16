package com.monoid.hackernews.wear.view.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    itemTreeRepository: ItemTreeRepository,
    title: String,
    orderedItemRepo: LiveUpdateUseCase<OrderedItem>,
    onSelectItemId: (ItemId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemStaleMinutes = integerResource(id = R.integer.item_stale_minutes)

    val itemRows: ImmutableList<ItemListRow> by remember(itemStaleMinutes) {
        orderedItemRepo
            .getItems(TimeUnit.MINUTES.toMillis(itemStaleMinutes.toLong()))
            .map { orderedItems ->
                itemTreeRepository.itemUiList(orderedItems.map { it.itemId })
            }
    }.collectAsStateWithLifecycle(persistentListOf())

    val state = rememberScalingLazyListState()

    Scaffold(
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = {
            if (state.isScrollInProgress) {
                PositionIndicator(state)
            }
        },
        timeText = { TimeText() },
        modifier = modifier,
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
