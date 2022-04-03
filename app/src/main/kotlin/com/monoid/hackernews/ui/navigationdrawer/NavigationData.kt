package com.monoid.hackernews.ui.navigationdrawer

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Bookmark
import androidx.compose.material.icons.twotone.NewReleases
import androidx.compose.material.icons.twotone.PresentToAll
import androidx.compose.material.icons.twotone.QuestionAnswer
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material.icons.twotone.TrendingUp
import androidx.compose.material.icons.twotone.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.monoid.hackernews.MainNavigation
import com.monoid.hackernews.R
import com.monoid.hackernews.Stories

data class NavigationDrawerItemData(
    val icon: ImageVector,
    @StringRes val titleId: Int,
    val route: String,
)

val storiesToNavigationItem: Map<Stories, NavigationDrawerItemData> =
    mapOf(
        Pair(
            Stories.Top,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.TrendingUp,
                titleId = R.string.top_stories,
                route = MainNavigation.Home.routeWithArgs(Stories.Top),
            )
        ),
        Pair(
            Stories.New,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.NewReleases,
                titleId = R.string.new_stories,
                route = MainNavigation.Home.routeWithArgs(Stories.New),
            )
        ),
        Pair(
            Stories.Best,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.Star,
                titleId = R.string.best_stories,
                route = MainNavigation.Home.routeWithArgs(Stories.Best),
            )
        ),
        Pair(
            Stories.Show,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.PresentToAll,
                titleId = R.string.show_hn,
                route = MainNavigation.Home.routeWithArgs(Stories.Show),
            )
        ),
        Pair(
            Stories.Ask,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.QuestionAnswer,
                titleId = R.string.ask_hn,
                route = MainNavigation.Home.routeWithArgs(Stories.Ask),
            )
        ),
        Pair(
            Stories.Job,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.Work,
                titleId = R.string.jobs,
                route = MainNavigation.Home.routeWithArgs(Stories.Job),
            )
        ),
        Pair(
            Stories.Favorite,
            NavigationDrawerItemData(
                icon = Icons.TwoTone.Bookmark,
                titleId = R.string.favorites,
                route = MainNavigation.Home.routeWithArgs(Stories.Favorite),
            )
        ),
    )

val navigationItemToStories: Map<NavigationDrawerItemData, Stories> =
    storiesToNavigationItem.map { (a, b) -> Pair(b, a) }.toMap()

val navigationItemList: List<NavigationDrawerItemData?> =
    listOf(
        storiesToNavigationItem[Stories.Top],
        storiesToNavigationItem[Stories.New],
        storiesToNavigationItem[Stories.Best],
        null,
        storiesToNavigationItem[Stories.Show],
        storiesToNavigationItem[Stories.Ask],
        storiesToNavigationItem[Stories.Job],
        null,
        storiesToNavigationItem[Stories.Favorite],
    )
