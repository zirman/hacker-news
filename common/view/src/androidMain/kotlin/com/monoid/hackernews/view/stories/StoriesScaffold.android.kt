@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.view.itemdetail.ListItemDetailContentUiState

@Composable
fun StoriesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
    key: String = "default",
    viewModel: StoriesViewModel = createStoriesViewModel(key),
) {
    Box(modifier = modifier) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val (loading, itemsList) = uiState

        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                StoriesListPane(
                    listState = viewModel.listState,
                    itemsList = itemsList,
                    onVisibleItem = { item ->
                        viewModel.updateItem(item.id)
                    },
                    onClickItem = { item ->
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail,
                            content = ListItemDetailContentUiState(item.id, null),
                        )
                    },
                    onClickBrowser = onClickBrowser,
                )
            },
            detailPane = {
                StoriesDetailPane(
                    itemId = (navigator.currentDestination?.content as? ListItemDetailContentUiState)?.itemId,
                    onOpenBrowser = { item ->
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Extra,
                            content = ListItemDetailContentUiState(item.id, item.url),
                        )
                    },
                )
            },
            extraPane = {
                StoriesExtraPane(url = (navigator.currentDestination?.content as? ListItemDetailContentUiState)?.url)
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
