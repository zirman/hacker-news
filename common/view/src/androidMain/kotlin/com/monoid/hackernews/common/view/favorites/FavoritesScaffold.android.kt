@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.settings.SettingsViewModel
import com.monoid.hackernews.common.view.stories.StoriesDetailPane
import com.monoid.hackernews.common.view.stories.listContentPadding
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val viewModel: SettingsViewModel = koinViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                if (uiState.username.string.isNotBlank()) {
                    val scope = rememberCoroutineScope()
                    FavoriteStoriesListPane(
                        username = uiState.username,
                        onClickItem = { item ->
                            scope.launch {
                                navigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail,
                                    contentKey = "${item.id.long}",
                                )
                            }
                        },
                        onClickReply = onClickReply,
                        onClickUser = onClickUser,
                        onClickUrl = onClickUrl,
                        onClickLogin = onClickLogin,
                        contentPadding = listContentPadding(),
                    )
                } else {
                    LaunchedEffect(Unit) {
                        onClickLogin()
                    }
                }
            },
            detailPane = {
                val itemId = (navigator.currentDestination?.contentKey as? String)
                    ?.toLong()
                    ?.let { ItemId(it) }
                StoriesDetailPane(
                    itemId = itemId,
                    onClickUrl = onClickUrl,
                    onClickUser = onClickUser,
                    onClickReply = onClickReply,
                    onClickLogin = onClickLogin,
                )
            },
        )
    }
}
