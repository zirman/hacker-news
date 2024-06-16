@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Item
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.view.itemdetail.ListItemDetailContentUiState
import com.monoid.hackernews.view.itemdetail.WebViewPane
import com.monoid.hackernews.view.itemlist.ItemsColumn

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
                // TODO: AnimatedPane(modifier = Modifier.preferredWidth(320.dp)).
                Box(
                    modifier = Modifier
                        .preferredWidth(320.dp)
                        .fillMaxHeight(),
                ) {
                    ItemsColumn(
                        listState = viewModel.listState,
                        itemsList = itemsList,
                        onItemVisible = { item ->
                            viewModel.updateItem(item.id)
                        },
                        onItemClick = { item ->
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                content = ListItemDetailContentUiState(item.id, null),
                            )
                        },
                        onOpenBrowser = onClickBrowser,
                    )
                }
            },
            detailPane = {
                // TODO: AnimatedPane(modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.fillMaxSize()) {
                    val itemId =
                        (navigator.currentDestination?.content as? ListItemDetailContentUiState)?.itemId

                    if (itemId == null) {
                        Text(
                            text = stringResource(id = R.string.no_story_selected),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        key(itemId) {
                            ItemDetailPane(
                                itemId = itemId,
                                onOpenBrowser = { url ->
                                    navigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.Extra,
                                        content = ListItemDetailContentUiState(itemId, url),
                                    )
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
                            )
                        }
                    }
                }
            },
            extraPane = {
                val url =
                    (navigator.currentDestination?.content as? ListItemDetailContentUiState)?.url
                // TODO: AnimatedPane(modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues())) {
                    WebViewPane(
                        url = url,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
