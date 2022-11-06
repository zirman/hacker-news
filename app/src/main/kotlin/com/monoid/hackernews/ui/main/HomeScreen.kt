package com.monoid.hackernews.ui.main

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.shared.R
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.AskStoryRepository
import com.monoid.hackernews.shared.data.BestStoryRepository
import com.monoid.hackernews.shared.data.FavoriteStoryRepository
import com.monoid.hackernews.shared.data.JobStoryRepository
import com.monoid.hackernews.shared.data.NewStoryRepository
import com.monoid.hackernews.shared.data.ShowStoryRepository
import com.monoid.hackernews.shared.data.TopStoryRepository
import com.monoid.hackernews.shared.domain.LiveUpdateUseCase
import com.monoid.hackernews.shared.navigation.LoginAction
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.shared.navigation.Stories
import com.monoid.hackernews.shared.navigation.Username
import com.monoid.hackernews.shared.ui.util.itemIdSaver
import com.monoid.hackernews.ui.home.HomeScreen

fun NavGraphBuilder.homeScreen(
    context: Context,
    windowSizeClass: WindowSizeClass,
    mainViewModel: MainViewModel,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    onNavigateToUser: (Username) -> Unit,
    onNavigateToReply: (ItemId) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
) {
    composable(
        route = MainNavigation.Home.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = "http://news.ycombinator.com/item?id={itemId}" },
            navDeepLink { uriPattern = "https://news.ycombinator.com/item?id={itemId}" },
            navDeepLink { uriPattern = "http://news.ycombinator.com/{deepLinkRoute}" },
            navDeepLink { uriPattern = "https://news.ycombinator.com/{deepLinkRoute}" },
        ),
        arguments = MainNavigation.Home.arguments,
        enterTransition = MainNavigation.Home.enterTransition,
        exitTransition = MainNavigation.Home.exitTransition,
        popEnterTransition = MainNavigation.Home.popEnterTransition,
        popExitTransition = MainNavigation.Home.popExitTransition,
    ) { navBackStackEntry ->
        val stories: Stories = remember { MainNavigation.Home.argsFromRoute(navBackStackEntry) }

        LaunchedEffect(navBackStackEntry) {
            ShortcutManagerCompat.reportShortcutUsed(context, stories.name)
        }

        val (selectedItemId, setSelectedItemId) =
            rememberSaveable(stateSaver = itemIdSaver) {
                mutableStateOf(
                    navBackStackEntry.arguments
                        ?.getString("itemId")
                        ?.toLong()
                        ?.let { ItemId(it) }
                )
            }

        // Used to keep track of if the story was scrolled last.
        val (detailInteraction, setDetailInteraction) =
            rememberSaveable { mutableStateOf(selectedItemId != null) }

        HomeScreen(
            mainViewModel = mainViewModel,
            drawerState = drawerState,
            windowSizeClass = windowSizeClass,
            title = stringResource(
                id = when (stories) {
                    Stories.Top ->
                        R.string.top_stories
                    Stories.New ->
                        R.string.new_stories
                    Stories.Best ->
                        R.string.best_stories
                    Stories.Ask ->
                        R.string.ask_hacker_news
                    Stories.Show ->
                        R.string.show_hacker_news
                    Stories.Job ->
                        R.string.jobs
                    Stories.Favorite ->
                        R.string.favorites
                }
            ),
            orderedItemRepo = remember(stories) {
                LiveUpdateUseCase(
                    context.getSystemService()!!,
                    when (stories) {
                        Stories.Top ->
                            TopStoryRepository(
                                httpClient = mainViewModel.httpClient,
                                topStoryDao = mainViewModel.topStoryDao,
                            )
                        Stories.New ->
                            NewStoryRepository(
                                httpClient = mainViewModel.httpClient,
                                newStoryDao = mainViewModel.newStoryDao,
                            )
                        Stories.Best ->
                            BestStoryRepository(
                                httpClient = mainViewModel.httpClient,
                                bestStoryDao = mainViewModel.bestStoryDao,
                            )
                        Stories.Ask ->
                            AskStoryRepository(
                                httpClient = mainViewModel.httpClient,
                                askStoryDao = mainViewModel.askStoryDao,
                            )
                        Stories.Show ->
                            ShowStoryRepository(
                                httpClient = mainViewModel.httpClient,
                                showStoryDao = mainViewModel.showStoryDao,
                            )
                        Stories.Job ->
                            JobStoryRepository(
                                httpClient = mainViewModel.httpClient,
                                jobStoryDao = mainViewModel.jobStoryDao,
                            )
                        Stories.Favorite ->
                            FavoriteStoryRepository(
                                context = context,
                                favoriteDao = mainViewModel.favoriteDao,
                            )
                    }
                )
            },
            snackbarHostState = snackbarHostState,
            selectedItemId = selectedItemId,
            setSelectedItemId = setSelectedItemId,
            detailInteraction = detailInteraction,
            setDetailInteraction = setDetailInteraction,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}
