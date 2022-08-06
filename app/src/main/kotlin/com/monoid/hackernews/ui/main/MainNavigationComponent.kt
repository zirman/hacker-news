package com.monoid.hackernews.ui.main

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.MainNavigation
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.R
import com.monoid.hackernews.Stories
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.data.AskStoryRepository
import com.monoid.hackernews.data.BestStoryRepository
import com.monoid.hackernews.data.FavoriteStoryRepository
import com.monoid.hackernews.data.JobStoryRepository
import com.monoid.hackernews.data.NewStoryRepository
import com.monoid.hackernews.data.ShowStoryRepository
import com.monoid.hackernews.data.TopStoryRepository
import com.monoid.hackernews.data.UserStoryRepository
import com.monoid.hackernews.domain.LiveUpdateUseCase
import com.monoid.hackernews.navigation.LoginAction
import com.monoid.hackernews.ui.aboutus.AboutUs
import com.monoid.hackernews.ui.home.HomeScreen
import com.monoid.hackernews.ui.login.LoginContent
import com.monoid.hackernews.ui.reply.ReplyContent
import com.monoid.hackernews.ui.util.getNetworkConnectivityStateFlow
import com.monoid.hackernews.ui.util.itemIdSaver
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.dropWhile
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun MainNavigationComponent(
    windowSizeClass: WindowSizeClass,
    mainNavController: NavHostController,
    drawerState: DrawerState,
    onLoginError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mainViewModel: MainViewModel = viewModel()

    // bug workaround for bottom sheets not updating
    val windowSizeClassState: State<WindowSizeClass> =
        rememberUpdatedState(windowSizeClass)

    val snackbarHostState: SnackbarHostState =
        remember { SnackbarHostState() }

    val context: Context =
        LocalContext.current

    val lifecycleOwner: LifecycleOwner =
        LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            getNetworkConnectivityStateFlow(
                coroutineScope = this,
                connectivityManager = context.getSystemService()!!,
            )
                .debounce(2.toDuration(DurationUnit.SECONDS))
                .dropWhile { it }
                .collectLatest { hasConnectivity ->
                    if (hasConnectivity) {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.back_online),
                            duration = SnackbarDuration.Short,
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.offline),
                            duration = SnackbarDuration.Indefinite,
                        )
                    }
                }
        }
    }

    AnimatedNavHost(
        navController = mainNavController,
        startDestination = MainNavigation.Home.route,
        modifier = modifier,
    ) {
        composable(
            route = MainNavigation.User.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "http://news.ycombinator.com/user?id={username}" },
                navDeepLink { uriPattern = "https://news.ycombinator.com/user?id={username}" },
            ),
            arguments = MainNavigation.User.arguments,
            enterTransition = MainNavigation.User.enterTransition,
            exitTransition = MainNavigation.User.exitTransition,
            popEnterTransition = MainNavigation.User.popEnterTransition,
            popExitTransition = MainNavigation.User.popExitTransition,
        ) { navBackStackEntry ->
            val username: Username =
                MainNavigation.User.argsFromRoute(navBackStackEntry = navBackStackEntry)

            val (selectedItemId, setSelectedItemId) =
                rememberSaveable(stateSaver = itemIdSaver) { mutableStateOf(null) }

            // Used to keep track of if the story was scrolled last.
            val (detailInteraction, setDetailInteraction) =
                rememberSaveable { mutableStateOf(false) }

            HomeScreen(
                mainViewModel = mainViewModel,
                drawerState = drawerState,
                mainNavController = mainNavController,
                windowSizeClass = windowSizeClass,
                title = username.string,
                orderedItemRepo = remember(mainViewModel, username) {
                    LiveUpdateUseCase(
                        context.getSystemService()!!,
                        UserStoryRepository(
                            httpClient = mainViewModel.httpClient,
                            userDao = mainViewModel.userDao,
                            itemDao = mainViewModel.itemDao,
                            username = username,
                        )
                    )
                },
                snackbarHostState = snackbarHostState,
                selectedItemId = selectedItemId,
                setSelectedItemId = setSelectedItemId,
                detailInteraction = detailInteraction,
                setDetailInteraction = setDetailInteraction,
            )
        }

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
                mainNavController = mainNavController,
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
            )
        }

        bottomSheet(
            route = MainNavigation.Login.route,
            arguments = MainNavigation.Login.arguments,
        ) { navBackStackEntry ->
            val loginAction: LoginAction =
                MainNavigation.Login.argsFromRoute(navBackStackEntry = navBackStackEntry)

            LoginContent(
                windowSizeClassState = windowSizeClassState,
                onLogin = { authentication ->
                    when (loginAction) {
                        is LoginAction.Login -> {}
                        is LoginAction.Upvote -> {
                            mainViewModel.itemTreeRepository.upvoteItemJob(
                                authentication,
                                ItemId(loginAction.itemId)
                            )
                        }
                        is LoginAction.Favorite -> {
                            mainViewModel.itemTreeRepository.favoriteItemJob(
                                authentication,
                                ItemId(loginAction.itemId)
                            )
                        }
                        is LoginAction.Flag -> {
                            mainViewModel.itemTreeRepository.flagItemJob(
                                authentication,
                                ItemId(loginAction.itemId)
                            )
                        }
                        is LoginAction.Reply -> {
                            mainNavController.navigate(
                                MainNavigation.Reply.routeWithArgs(ItemId(loginAction.itemId))
                            )
                        }
                    }

                    mainNavController.navigateUp()
                },
                onLoginError = onLoginError,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState()),
            )
        }

        bottomSheet(
            route = MainNavigation.Reply.route,
            arguments = MainNavigation.Reply.arguments,
        ) { navBackStackEntry ->
            val itemId: ItemId =
                MainNavigation.Reply.argsFromRoute(navBackStackEntry = navBackStackEntry)

            ReplyContent(
                itemId = itemId,
                windowSizeClassState = windowSizeClassState,
                onSuccess = { mainNavController.navigateUp() },
                onError = onLoginError,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState()),
            )
        }

        bottomSheet(
            route = MainNavigation.AboutUs.route,
            arguments = MainNavigation.AboutUs.arguments,
        ) {
            AboutUs(
                windowSizeClassState = windowSizeClassState,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState()),
            )
        }
    }
}
