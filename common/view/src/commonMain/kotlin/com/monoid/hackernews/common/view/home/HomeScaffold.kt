package com.monoid.hackernews.common.view.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.Newspaper
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.favorites
import com.monoid.hackernews.common.view.favorites_description
import com.monoid.hackernews.common.view.profile_description
import com.monoid.hackernews.common.view.settings
import com.monoid.hackernews.common.view.stories
import com.monoid.hackernews.common.view.trending_description
import org.jetbrains.compose.resources.StringResource

inline val Route.BottomNav.icon: ImageVector
    get() = when (this) {
        Route.BottomNav.Stories -> Icons.TwoTone.Newspaper
        Route.BottomNav.Favorites -> Icons.TwoTone.Bookmarks
        Route.BottomNav.Settings -> Icons.TwoTone.Settings
    }

inline val Route.BottomNav.selectedIcon: ImageVector
    get() = when (this) {
        Route.BottomNav.Stories -> Icons.Filled.Newspaper
        Route.BottomNav.Favorites -> Icons.Filled.Bookmarks
        Route.BottomNav.Settings -> Icons.Filled.Settings
    }

inline val Route.BottomNav.contentDescription: StringResource
    get() = when (this) {
        Route.BottomNav.Stories -> Res.string.trending_description
        Route.BottomNav.Favorites -> Res.string.favorites_description
        Route.BottomNav.Settings -> Res.string.profile_description
    }

inline val Route.BottomNav.label: StringResource
    get() = when (this) {
        Route.BottomNav.Stories -> Res.string.stories
        Route.BottomNav.Favorites -> Res.string.favorites
        Route.BottomNav.Settings -> Res.string.settings
    }
