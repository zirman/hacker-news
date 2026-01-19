package com.monoid.hackernews.common.view.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.Newspaper
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.favorites
import com.monoid.hackernews.common.view.favorites_description
import com.monoid.hackernews.common.view.profile_description
import com.monoid.hackernews.common.view.settings
import com.monoid.hackernews.common.view.stories
import com.monoid.hackernews.common.view.trending_description
import org.jetbrains.compose.resources.StringResource

val TOP_LEVEL_ROUTES: Map<BottomNav, NavBarItem> = mapOf(
    BottomNav.Stories to NavBarItem(
        icon = Icons.TwoTone.Newspaper,
        selectedIcon = Icons.Filled.Newspaper,
        description = Res.string.trending_description,
        label = Res.string.stories,
    ),
    BottomNav.Favorites to NavBarItem(
        icon = Icons.TwoTone.Bookmarks,
        selectedIcon = Icons.Filled.Bookmarks,
        description = Res.string.favorites_description,
        label = Res.string.favorites,
    ),
    BottomNav.Settings to NavBarItem(
        icon = Icons.TwoTone.Settings,
        selectedIcon = Icons.Filled.Settings,
        description = Res.string.profile_description,
        label = Res.string.settings,
    ),
)

data class NavBarItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val description: StringResource,
    val label: StringResource,
)
