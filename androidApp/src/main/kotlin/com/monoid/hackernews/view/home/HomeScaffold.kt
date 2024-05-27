@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.monoid.hackernews.common.data.SimpleItemUiState
import com.monoid.hackernews.common.navigation.BottomNav
import com.monoid.hackernews.common.view.R

@Composable
fun HomeScaffold(onClickBrowser: (SimpleItemUiState) -> Unit, modifier: Modifier = Modifier) {
    var currentDestination by rememberSaveable { mutableStateOf(BottomNav.Stories) }
    BackHandler(currentDestination != BottomNav.Stories) {
        currentDestination = BottomNav.Stories
    }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            BottomNav.entries.forEach { story ->
                item(
                    selected = story == currentDestination,
                    onClick = { currentDestination = story },
                    icon = {
                        Icon(
                            imageVector = if (story == currentDestination) story.selectedIcon else story.icon,
                            contentDescription = stringResource(story.contentDescription),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(story.label),
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        },
        modifier = modifier.fillMaxSize(),
        content = {
            val storiesNavigator = rememberListDetailPaneScaffoldNavigator<SimpleItemUiState>()
            val storiesListState = rememberLazyListState()
            val storiesDetailListState = rememberLazyListState()

            val favoritesNavigator =
                rememberListDetailPaneScaffoldNavigator<SimpleItemUiState>()
            val favoritesListState = rememberLazyListState()
            val favoritesDetailListState = rememberLazyListState()

            val profileNavigator = rememberListDetailPaneScaffoldNavigator<SimpleItemUiState>()
            val profileListState = rememberLazyListState()
            val profileDetailListState = rememberLazyListState()

            when (currentDestination) {
                BottomNav.Stories -> {
                    StoriesNavHost(
                        listState = storiesListState,
                        detailListState = storiesDetailListState,
                        onClickBrowser = onClickBrowser,
                        navigator = storiesNavigator,
                    )
                }

                BottomNav.Favorites -> {
                    FavoritesNavHost(
                        listState = favoritesListState,
                        detailListState = favoritesDetailListState,
                        onClickBrowser = onClickBrowser,
                        navigator = favoritesNavigator,
                    )
                }

                BottomNav.Profile -> {
                    ProfileNavHost(
                        listState = profileListState,
                        detailListState = profileDetailListState,
                        onClickBrowser = onClickBrowser,
                        navigator = profileNavigator,
                    )
                }
            }
        },
    )
}

private inline val BottomNav.icon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.TwoTone.Home
        BottomNav.Favorites -> Icons.TwoTone.Bookmarks
        BottomNav.Profile -> Icons.TwoTone.AccountCircle
    }

private inline val BottomNav.selectedIcon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.Default.Home
        BottomNav.Favorites -> Icons.Default.Bookmarks
        BottomNav.Profile -> Icons.Default.AccountCircle
    }

private inline val BottomNav.contentDescription: Int
    get() = when (this) {
        BottomNav.Stories -> R.string.top_stories_description
        BottomNav.Favorites -> R.string.favorites_description
        BottomNav.Profile -> R.string.profile_description
    }

private inline val BottomNav.label: Int
    get() = when (this) {
        BottomNav.Stories -> R.string.top_stories
        BottomNav.Favorites -> R.string.favorites
        BottomNav.Profile -> R.string.profile
    }
