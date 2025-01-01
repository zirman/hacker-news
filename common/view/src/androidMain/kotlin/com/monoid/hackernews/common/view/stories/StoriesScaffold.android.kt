@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.view.itemdetail.ListItemDetailContentUiState
import kotlinx.coroutines.launch

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
        val scope = rememberCoroutineScope()
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
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = ListItemDetailContentUiState(item.id, null),
                            )
                        }
                    },
                    onClickBrowser = onClickBrowser,
                )
            },
            detailPane = {
                StoriesDetailPane(
                    itemId = (navigator.currentDestination?.contentKey as? ListItemDetailContentUiState)?.itemId,
                    onOpenBrowser = { item ->
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Extra,
                                contentKey = ListItemDetailContentUiState(item.id, item.url),
                            )
                        }
                    },
                )
            },
            extraPane = {
                StoriesExtraPane(url = (navigator.currentDestination?.contentKey as? ListItemDetailContentUiState)?.url)
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
