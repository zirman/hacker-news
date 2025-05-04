package com.monoid.hackernews.common.view.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.Newspaper
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.favorites
import com.monoid.hackernews.common.view.favorites_description
import com.monoid.hackernews.common.view.profile_description
import com.monoid.hackernews.common.view.settings
import com.monoid.hackernews.common.view.stories
import com.monoid.hackernews.common.view.trending_description
import org.jetbrains.compose.resources.StringResource

inline val BottomNav.icon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.TwoTone.Newspaper
        BottomNav.Favorites -> Icons.TwoTone.Bookmarks
        BottomNav.Settings -> Icons.TwoTone.Settings
    }

inline val BottomNav.selectedIcon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.Filled.Newspaper
        BottomNav.Favorites -> Icons.Filled.Bookmarks
        BottomNav.Settings -> Icons.Filled.Settings
    }

inline val BottomNav.contentDescription: StringResource
    get() = when (this) {
        BottomNav.Stories -> Res.string.trending_description
        BottomNav.Favorites -> Res.string.favorites_description
        BottomNav.Settings -> Res.string.profile_description
    }

inline val BottomNav.label: StringResource
    get() = when (this) {
        BottomNav.Stories -> Res.string.stories
        BottomNav.Favorites -> Res.string.favorites
        BottomNav.Settings -> Res.string.settings
    }

@Composable
expect fun HomeScaffold(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
)
