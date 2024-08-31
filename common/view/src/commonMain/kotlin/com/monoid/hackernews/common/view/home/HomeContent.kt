package com.monoid.hackernews.common.view.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.domain.navigation.BottomNav

@Composable
expect fun HomeContent(
    currentDestination: BottomNav,
    onClickBrowser: (Item) -> Unit,
    onClickLogin: () -> Unit,
    modifier: Modifier = Modifier,
)
//{
//    Box(modifier = modifier) {
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
//    }
//}
