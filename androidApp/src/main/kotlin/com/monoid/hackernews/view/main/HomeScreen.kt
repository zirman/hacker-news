package com.monoid.hackernews.view.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.domain.LiveUpdateUseCase
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.common.navigation.Story
import com.monoid.hackernews.common.navigation.StoryNavType
import com.monoid.hackernews.common.ui.util.itemIdSaver
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.home.HomeScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.homeScreen(
    windowSizeClass: WindowSizeClass,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: (LoginAction) -> Unit,
    mainViewModel: MainViewModel,
) {
    composable<Route.Home>(
        typeMap = mapOf(typeOf<Story>() to NavType.StoryNavType),
//        deepLinks = listOf(
//            navDeepLink { uriPattern = "http://news.ycombinator.com/item?id={itemId}" },
//            navDeepLink { uriPattern = "https://news.ycombinator.com/item?id={itemId}" },
//            navDeepLink { uriPattern = "http://news.ycombinator.com/{deepLinkRoute}" },
//            navDeepLink { uriPattern = "https://news.ycombinator.com/{deepLinkRoute}" },
//        ),
    ) { navBackStackEntry ->
        val stories = navBackStackEntry.toRoute<Route.Home>().story
        val context = LocalContext.current

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
            itemTreeRepository = mainViewModel.itemTreeRepository,
            drawerState = drawerState,
            windowSizeClass = windowSizeClass,
            title = stringResource(
                id = when (stories) {
                    Story.Top -> R.string.top_stories
                    Story.New -> R.string.new_stories
                    Story.Best -> R.string.best_stories
                    Story.Ask -> R.string.ask_hacker_news
                    Story.Show -> R.string.show_hacker_news
                    Story.Job -> R.string.jobs
                    Story.Favorite -> R.string.favorites
                }
            ),
            orderedItemRepo = remember(stories) {
                LiveUpdateUseCase(
                    connectivityManager = context.getSystemService()!!,
                    repository = when (stories) {
                        Story.Top -> mainViewModel.topStoryRepository
                        Story.New -> mainViewModel.newStoryRepository
                        Story.Best -> mainViewModel.bestStoryRepository
                        Story.Ask -> mainViewModel.askStoryRepository
                        Story.Show -> mainViewModel.showStoryRepository
                        Story.Job -> mainViewModel.jobStoryRepository
                        Story.Favorite -> mainViewModel.favoriteStoryRepository
                    },
                    logger = mainViewModel.logger,
                )
            },
            snackbarHostState = snackbarHostState,
            selectedItemId = selectedItemId,
            setSelectedItemId = setSelectedItemId,
            detailInteraction = detailInteraction,
            setDetailInteraction = setDetailInteraction,
            onClickUser = mainViewModel::clickUser,
            onClickReply = mainViewModel::clickReply,
            onNavigateToLogin = onNavigateToLogin,
            onClickBrowser = mainViewModel::clickBrowser,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
