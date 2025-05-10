@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)
package com.monoid.hackernews.common.view.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.view.stories.StoriesDetailPane
import com.monoid.hackernews.common.view.stories.StoriesListPane
import com.monoid.hackernews.common.view.stories.listContentPadding
import kotlinx.coroutines.launch

@Composable
fun FavoritesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                val scope = rememberCoroutineScope()
                StoriesListPane(
                    onClickItem = { item ->
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = "${item.id.long}",
                            )
                        }
                    },
                    onClickReply = {},
                    onClickUser = {},
                    onClickUrl = onClickUrl,
                    onClickLogin = onClickLogin,
                    contentPadding = listContentPadding(),
                )
            },
            detailPane = {
                val itemId = (navigator.currentDestination?.contentKey as? String)
                    ?.toLong()
                    ?.let { ItemId(it) }
                StoriesDetailPane(
                    itemId = itemId,
                    onClickUrl = onClickUrl,
                    onClickUser = {},
                    onClickReply = {},
                    onClickLogin = onClickLogin,
                )
            },
        )
    }
}
