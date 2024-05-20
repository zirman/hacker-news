package com.monoid.hackernews.view.main

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.common.navigation.Story
import com.monoid.hackernews.common.ui.util.getNetworkConnectivityStateFlow
import com.monoid.hackernews.common.view.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.dropWhile
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun MainNavigation(
    mainViewModel: MainViewModel,
    windowSizeClass: WindowSizeClass,
    mainNavController: NavHostController,
    drawerState: DrawerState,
    onNavigateToUser: (Username) -> Unit,
    onNavigateToReply: (ItemId) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
    onNavigateUp: () -> Unit,
    onLoginError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val context: Context = LocalContext.current
    val owner: LifecycleOwner = LocalLifecycleOwner.current

    // TODO: move to ViewModel?
    LaunchedEffect(owner) {
        owner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            getNetworkConnectivityStateFlow(
                coroutineScope = this,
                connectivityManager = context.getSystemService()!!
            )
                .debounce(2.toDuration(DurationUnit.SECONDS))
                .dropWhile { it }
                .collectLatest { hasConnectivity ->
                    if (hasConnectivity) {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.back_online),
                            duration = SnackbarDuration.Short
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.offline),
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
        }
    }

    LaunchedEffect(owner) {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // ensures event handler doesn't suspend
            fun handleEvent(event: MainViewModel.Event) {
                when (event) {
                    is MainViewModel.Event.NavigateToLogin -> {

                    }

                    is MainViewModel.Event.NavigateToReply -> {

                    }

                    is MainViewModel.Event.NavigateToUser -> {
                        mainNavController.navigate(Route.User(event.user))
                    }

                    is MainViewModel.Event.NavigateToBrowser -> {
//                        mainNavController.navigate(Route.User(event.user))
                    }

                    is MainViewModel.Event.UrlIsNull -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.url_is_null),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            mainViewModel.events.collect(::handleEvent)
        }
    }

    NavHost(
        navController = mainNavController,
        startDestination = "start",
        modifier = modifier,
    ) {
        composable(route = "start") {
            LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
                mainNavController.navigate(Route.Home(Story.Top))
            }
        }

        homeScreen(
            mainViewModel = mainViewModel,
            windowSizeClass = windowSizeClass,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
//            onNavigateToUser = onNavigateToUser,
//            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
        )

        userScreen(
            mainViewModel = mainViewModel,
            context = context,
            windowSizeClass = windowSizeClass,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
        )
//        mainGraph(
//            mainViewModel = mainViewModel,
//            context = context,
//            snackbarHostState = snackbarHostState,
//            windowSizeClassState = windowSizeClassState,
//            drawerState = drawerState,
//            onNavigateToUser = onNavigateToUser,
//            onNavigateToReply = onNavigateToReply,
//            onNavigateToLogin = onNavigateToLogin,
//            onNavigateUp = onNavigateUp,
//            onLoginError = onLoginError
//        )
    }
}
