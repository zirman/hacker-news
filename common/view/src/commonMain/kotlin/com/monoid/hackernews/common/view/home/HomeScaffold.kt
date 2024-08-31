package com.monoid.hackernews.common.view.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.favorites
import com.monoid.hackernews.common.view.favorites_description
import com.monoid.hackernews.common.view.profile_description
import com.monoid.hackernews.common.view.settings
import com.monoid.hackernews.common.view.top_stories
import com.monoid.hackernews.common.view.top_stories_description
import org.jetbrains.compose.resources.StringResource

inline val BottomNav.icon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.TwoTone.Home
        BottomNav.Favorites -> Icons.TwoTone.Bookmarks
        BottomNav.Settings -> Icons.TwoTone.Settings
    }

inline val BottomNav.selectedIcon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.Default.Home
        BottomNav.Favorites -> Icons.Default.Bookmarks
        BottomNav.Settings -> Icons.Default.Settings
    }

inline val BottomNav.contentDescription: StringResource
    get() = when (this) {
        BottomNav.Stories -> Res.string.top_stories_description
        BottomNav.Favorites -> Res.string.favorites_description
        BottomNav.Settings -> Res.string.profile_description
    }

inline val BottomNav.label: StringResource
    get() = when (this) {
        BottomNav.Stories -> Res.string.top_stories
        BottomNav.Favorites -> Res.string.favorites
        BottomNav.Settings -> Res.string.settings
    }

@Composable
expect fun HomeScaffold(
    onClickBrowser: (Item) -> Unit,
    onClickLogin: () -> Unit,
    modifier: Modifier = Modifier,
)
