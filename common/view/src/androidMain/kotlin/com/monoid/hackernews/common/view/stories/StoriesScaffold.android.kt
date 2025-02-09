@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import kotlinx.coroutines.launch

private val contentKeyRegex = """^(\d*):(.*)$""".toRegex()

@Composable
fun StoriesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onOpenUrl: (Item) -> Unit,
    onNavigateLogin: () -> Unit,
    modifier: Modifier = Modifier,
    key: String = "default",
    storiesViewModel: StoriesViewModel = createStoriesViewModel(key),
) {
    Box(modifier = modifier) {
        LaunchedEffect(Unit) {
            storiesViewModel.events.collect { event ->
                when (event) {
                    is StoriesViewModel.Event.Error -> {
                        // TODO
                    }

                    is StoriesViewModel.Event.NavigateLogin -> {
                        onNavigateLogin()
                    }
                }
            }
        }
        val uiState by storiesViewModel.uiState.collectAsStateWithLifecycle()
        val (loading, itemsList) = uiState
        val scope = rememberCoroutineScope()
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                StoriesListPane(
                    listState = storiesViewModel.listState,
                    itemsList = itemsList,
                    onVisibleItem = storiesViewModel::updateItem,
                    onClickItem = { item ->
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = "${item.id.long}:",
                            )
                        }
                    },
                    onClickReply = {
                    },
                    onClickUser = {
                    },
                    onOpenUrl = onOpenUrl,
                    onClickUpvote = storiesViewModel::toggleUpvoted,
                    onClickFavorite = {
                    },
                    onClickFollow = {
                    },
                    onClickFlag = {
                    },
                )
            },
            detailPane = {
                val (itemId, _) = checkNotNull(
                    contentKeyRegex.matchEntire(
                        navigator.currentDestination?.contentKey as? String ?: ":",
                    ),
                ).destructured
                StoriesDetailPane(
                    itemId = itemId.toLongOrNull()?.let { ItemId(it) },
                    onOpenBrowser = { item ->
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Extra,
                                contentKey = "$itemId:${item.url}",
                            )
                        }
                    },
                )
            },
            extraPane = {
                val (_, url) = checkNotNull(
                    contentKeyRegex.matchEntire(
                        navigator.currentDestination?.contentKey as? String ?: ":",
                    ),
                ).destructured
                StoriesExtraPane(url = url)
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
