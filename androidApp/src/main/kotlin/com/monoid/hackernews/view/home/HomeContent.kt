@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.Item
import com.monoid.hackernews.common.navigation.BottomNav
import com.monoid.hackernews.view.profile.ProfileScaffold
import com.monoid.hackernews.view.stories.StoriesScaffold

@Composable
fun HomeContent(
    currentDestination: BottomNav,
    onClickBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val storiesNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
        val favoritesNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
        val profileNavigator = rememberListDetailPaneScaffoldNavigator<Any>()

        when (currentDestination) {
            BottomNav.Stories -> {
                StoriesScaffold(
                    navigator = storiesNavigator,
                    onClickBrowser = onClickBrowser,
                )
            }

            BottomNav.Favorites -> {
                StoriesScaffold(
                    navigator = favoritesNavigator,
                    onClickBrowser = onClickBrowser,
                    key = "favorites",
                )
            }

            BottomNav.Profile -> {
                ProfileScaffold(
                    navigator = profileNavigator,
                )
            }
        }
    }
}
