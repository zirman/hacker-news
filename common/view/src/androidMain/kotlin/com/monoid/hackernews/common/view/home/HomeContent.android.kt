@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.Route.BottomNav
import com.monoid.hackernews.common.view.favorites.FavoritesScaffold
import com.monoid.hackernews.common.view.settings.SettingsScaffold
import com.monoid.hackernews.common.view.stories.StoriesScaffold
import io.ktor.http.Url

@Composable
fun HomeContent(
    currentDestination: BottomNav,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
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
                    onClickLogin = onClickLogin,
                    onClickUser = onClickUser,
                    onClickReply = onClickReply,
                    onClickUrl = onClickUrl,
                )
            }

            BottomNav.Favorites -> {
                FavoritesScaffold(
                    navigator = favoritesNavigator,
                    onClickLogin = onClickLogin,
                    onClickUser = onClickUser,
                    onClickReply = onClickReply,
                    onClickUrl = onClickUrl,
                )
            }

            BottomNav.Settings -> {
                SettingsScaffold(
                    navigator = profileNavigator,
                    onClickLogin = onClickLogin,
                    onClickLogout = onClickLogout,
                )
            }
        }
    }
}
