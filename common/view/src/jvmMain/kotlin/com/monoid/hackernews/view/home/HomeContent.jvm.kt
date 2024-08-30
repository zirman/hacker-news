package com.monoid.hackernews.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.Item
import com.monoid.hackernews.common.navigation.BottomNav
import com.monoid.hackernews.view.itemlist.ItemsColumn
import com.monoid.hackernews.view.stories.StoriesViewModel
import com.monoid.hackernews.view.stories.createStoriesViewModel

@Composable
actual fun HomeContent(
    currentDestination: BottomNav,
    onClickBrowser: (Item) -> Unit,
    onClickLogin: () -> Unit,
    modifier: Modifier,
) {
    val key: String = "default"
    val viewModel: StoriesViewModel = createStoriesViewModel(key)
    val uiState by viewModel.uiState.collectAsState()
    ItemsColumn(
        listState = viewModel.listState,
        itemsList = uiState.itemsList,
        onItemVisible = { item ->
            viewModel.updateItem(item.id)
        },
        onItemClick = { item ->
//                    navigator.navigateTo(
//                        pane = ListDetailPaneScaffoldRole.Detail,
//                        content = ListItemDetailContentUiState(item.id, null),
//                    )
        },
        onOpenBrowser = onClickBrowser,
        modifier = Modifier
//                    .preferredWidth(320.dp)
            .fillMaxHeight(),
    )
//    onClickBrowser: (Item) -> Unit,
//    modifier: Modifier = Modifier,
//
    Box(modifier = modifier) {
//        val storiesNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
//        val favoritesNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
//        val profileNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
//        when (currentDestination) {
//            BottomNav.Stories -> {
//                StoriesScaffold(
//                    navigator = storiesNavigator,
//                    onClickBrowser = onClickBrowser,
//                )
//            }
//
//            BottomNav.Favorites -> {
//                StoriesScaffold(
//                    navigator = favoritesNavigator,
//                    onClickBrowser = onClickBrowser,
//                    key = "favorites",
//                )
//            }
//
//            BottomNav.Settings -> {
//                SettingsScaffold(
//                    navigator = profileNavigator,
//                    onClickLogin = onClickLogin,
//                )
//            }
//        }
//        Box(modifier = modifier) {
//            val uiState by viewModel.uiState.collectAsState()
//            val (loading, itemsList) = uiState
//
////            NavigableListDetailPaneScaffold(
////                navigator = navigator,
////                listPane = {
////                    StoriesListPane(
////                        listState = viewModel.listState,
////                        itemsList = itemsList,
////                        onVisibleItem = { item ->
////                            viewModel.updateItem(item.id)
////                        },
////                        onClickItem = { item ->
////                            navigator.navigateTo(
////                                pane = ListDetailPaneScaffoldRole.Detail,
////                                content = ListItemDetailContentUiState(item.id, null),
////                            )
////                        },
////                        onClickBrowser = onClickBrowser,
////                    )
////                },
////                detailPane = {
////                    StoriesDetailPane(
////                        itemId = (navigator.currentDestination?.content as? ListItemDetailContentUiState)?.itemId,
////                        onOpenBrowser = { item ->
////                            navigator.navigateTo(
////                                pane = ListDetailPaneScaffoldRole.Extra,
////                                content = ListItemDetailContentUiState(item.id, item.url),
////                            )
////                        },
////                    )
////                },
////                extraPane = {
////                    StoriesExtraPane(url = (navigator.currentDestination?.content as? ListItemDetailContentUiState)?.url)
////                },
////            )
////            if (loading) {
////                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
////            }
//        }
    }
}
