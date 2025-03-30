@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.settings.SettingsScaffold
import com.monoid.hackernews.common.view.stories.StoriesScaffold

@Composable
actual fun HomeContent(
    currentDestination: BottomNav,
    onClickBrowser: (Item) -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateLogout: () -> Unit,
    modifier: Modifier,
) {
    Box(modifier = modifier) {
        val storiesNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
        val favoritesNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
        val profileNavigator = rememberListDetailPaneScaffoldNavigator<Any>()
        when (currentDestination) {
            BottomNav.Stories -> {
                StoriesScaffold(
                    navigator = storiesNavigator,
                    onOpenUrl = onClickBrowser,
                    onNavigateLogin = onNavigateLogin,
                )
            }

            BottomNav.Favorites -> {
                StoriesScaffold(
                    navigator = favoritesNavigator,
                    onOpenUrl = onClickBrowser,
                    key = "favorites",
                    onNavigateLogin = onNavigateLogin,
                )
            }

            BottomNav.Settings -> {
                SettingsScaffold(
                    navigator = profileNavigator,
                    onNavigateLogin = onNavigateLogin,
                    onNavigateLogout = onNavigateLogout,
                )
            }
        }
    }
}
