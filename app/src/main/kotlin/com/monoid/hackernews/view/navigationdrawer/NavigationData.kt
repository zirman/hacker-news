package com.monoid.hackernews.view.navigationdrawer

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.NewReleases
import androidx.compose.material.icons.twotone.PresentToAll
import androidx.compose.material.icons.twotone.QuestionAnswer
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material.icons.twotone.TrendingUp
import androidx.compose.material.icons.twotone.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.common.navigation.Stories

data class NavigationDrawerItemData(
    val icon: ImageVector,
    @StringRes val titleId: Int,
    @StringRes val descriptionId: Int,
    val route: String,
)

val storiesToNavigationItem: Map<Stories, NavigationDrawerItemData> =
    mapOf(
        Pair(
            Stories.Top,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.TrendingUp,
                titleId = R.string.top_stories,
                descriptionId = R.string.top_stories_description,
                route = MainNavigation.Home.routeWithArgs(Stories.Top),
            )
        ),
        Pair(
            Stories.New,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.NewReleases,
                titleId = R.string.new_stories,
                descriptionId = R.string.new_stories_description,
                route = MainNavigation.Home.routeWithArgs(Stories.New),
            )
        ),
        Pair(
            Stories.Best,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.Star,
                titleId = R.string.best_stories,
                descriptionId = R.string.best_stories_description,
                route = MainNavigation.Home.routeWithArgs(Stories.Best),
            )
        ),
        Pair(
            Stories.Show,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.PresentToAll,
                titleId = R.string.show_hn,
                descriptionId = R.string.show_hn_description,
                route = MainNavigation.Home.routeWithArgs(Stories.Show),
            )
        ),
        Pair(
            Stories.Ask,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.QuestionAnswer,
                titleId = R.string.ask_hn,
                descriptionId = R.string.ask_hn_description,
                route = MainNavigation.Home.routeWithArgs(Stories.Ask),
            )
        ),
        Pair(
            Stories.Job,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.Work,
                titleId = R.string.jobs,
                descriptionId = R.string.jobs_description,
                route = MainNavigation.Home.routeWithArgs(Stories.Job),
            )
        ),
        Pair(
            Stories.Favorite,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.Bookmarks,
                titleId = R.string.favorites,
                descriptionId = R.string.favorites_description,
                route = MainNavigation.Home.routeWithArgs(Stories.Favorite),
            )
        ),
    )

val navigationItemList: List<NavigationDrawerItemData> =
    listOf(
        storiesToNavigationItem[Stories.Top]!!,
        storiesToNavigationItem[Stories.New]!!,
        storiesToNavigationItem[Stories.Best]!!,
        storiesToNavigationItem[Stories.Show]!!,
        storiesToNavigationItem[Stories.Ask]!!,
        storiesToNavigationItem[Stories.Job]!!,
        storiesToNavigationItem[Stories.Favorite]!!,
    )
