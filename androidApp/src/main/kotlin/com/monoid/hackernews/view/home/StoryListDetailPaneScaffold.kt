@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.monoid.hackernews.common.data.SimpleItemUiState
import com.monoid.hackernews.view.itemdetail.ItemDetail
import com.monoid.hackernews.view.itemlist.ItemsColumn

@Composable
fun StoryListDetailPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<SimpleItemUiState>,
    listState: LazyListState,
    detailListState: LazyListState,
    itemsList: List<SimpleItemUiState>,
    onItemVisible: (SimpleItemUiState) -> Unit,
    onClickBrowser: (SimpleItemUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }
    ListDetailPaneScaffold(
        directive = PaneScaffoldDirective.Default,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                ItemsColumn(
                    listState = listState,
                    itemsList = itemsList,
                    onItemVisible = onItemVisible,
                    onItemClick = { item ->
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, item)
                    },
                    onOpenBrowser = onClickBrowser,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val item = navigator.currentDestination?.content ?: return@AnimatedPane
                LifecycleEventEffect(Lifecycle.Event.ON_START) {
                    onItemVisible(item)
                }
                ItemDetail(listState = detailListState, item = item)
            }
        },
        modifier = modifier,
    )
}
