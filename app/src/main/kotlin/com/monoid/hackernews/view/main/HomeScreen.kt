package com.monoid.hackernews.view.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.domain.LiveUpdateUseCase
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.common.navigation.Stories
import com.monoid.hackernews.common.ui.util.itemIdSaver
import com.monoid.hackernews.view.home.HomeScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun NavGraphBuilder.homeScreen(
    mainViewModel: MainViewModel,
    context: Context,
    windowSizeClassState: State<WindowSizeClass>,
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

        val coroutineScope = rememberCoroutineScope()

        HomeScreen(
            itemTreeRepository = mainViewModel.itemTreeRepository,
            drawerState = drawerState,
            windowSizeClass = windowSizeClassState.value,
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
                            mainViewModel.topStoryRepository
                        Stories.New ->
                            mainViewModel.newStoryRepository
                        Stories.Best ->
                            mainViewModel.bestStoryRepository
                        Stories.Ask ->
                            mainViewModel.askStoryRepository
                        Stories.Show ->
                            mainViewModel.showStoryRepository
                        Stories.Job ->
                            mainViewModel.jobStoryRepository
                        Stories.Favorite ->
                            mainViewModel.favoriteStoryRepository
                    }
                )
            },
            snackbarHostState = snackbarHostState,
            selectedItemId = selectedItemId,
            setSelectedItemId = setSelectedItemId,
            detailInteraction = detailInteraction,
            setDetailInteraction = setDetailInteraction,
            onNavigateToLogin = onNavigateToLogin,
            onClickUser = { user ->
                if (user != null) {
                    onNavigateToUser(user)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.url_is_null),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onClickReply = { itemId ->
                coroutineScope.launch {
                    val auth = mainViewModel.authentication.data.first()

                    if (auth.password.isNotEmpty()) {
                        onNavigateToReply(itemId)
                    } else {
                        onNavigateToLogin(LoginAction.Reply(itemId.long))
                    }
                }
            },
            onClickBrowser = { url ->
                val uri = url?.let { Uri.parse(it) }

                if (uri != null) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.url_is_null),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}
