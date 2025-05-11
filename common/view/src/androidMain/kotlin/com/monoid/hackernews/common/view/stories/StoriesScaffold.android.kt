@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.stories

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
import com.monoid.hackernews.common.data.model.Username
import kotlinx.coroutines.launch

@Composable
fun StoriesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    onClickUser: (Username) -> Unit,
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
                    onClickUser = onClickUser,
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
                    onClickUser = onClickUser,
                    onClickReply = {},
                    onClickLogin = onClickLogin,
                )
            },
        )
    }
}
