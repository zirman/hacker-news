package com.monoid.hackernews.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.monoid.hackernews.common.data.Item
import com.monoid.hackernews.common.navigation.BottomNav
import com.monoid.hackernews.common.view.R

@Composable
fun HomeScaffold(
    onClickBrowser: (Item) -> Unit,
    onClickLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentDestination by rememberSaveable { mutableIntStateOf(0) }
    BackHandler(currentDestination != 0) {
        currentDestination = 0
    }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            BottomNav.entries.forEach { story ->
                item(
                    selected = story == BottomNav.entries[currentDestination],
                    onClick = { currentDestination = story.ordinal },
                    icon = {
                        Icon(
                            imageVector = if (story == BottomNav.entries[currentDestination]) story.selectedIcon else story.icon,
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
            HomeContent(
                currentDestination = BottomNav.entries[currentDestination],
                onClickBrowser = onClickBrowser,
                onClickLogin = onClickLogin,
            )
        },
    )
}

private inline val BottomNav.icon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.TwoTone.Home
        BottomNav.Favorites -> Icons.TwoTone.Bookmarks
        BottomNav.Settings -> Icons.TwoTone.Settings
    }

private inline val BottomNav.selectedIcon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.Default.Home
        BottomNav.Favorites -> Icons.Default.Bookmarks
        BottomNav.Settings -> Icons.Default.Settings
    }

private inline val BottomNav.contentDescription: Int
    get() = when (this) {
        BottomNav.Stories -> R.string.top_stories_description
        BottomNav.Favorites -> R.string.favorites_description
        BottomNav.Settings -> R.string.profile_description
    }

private inline val BottomNav.label: Int
    get() = when (this) {
        BottomNav.Stories -> R.string.top_stories
        BottomNav.Favorites -> R.string.favorites
        BottomNav.Settings -> R.string.settings
    }
