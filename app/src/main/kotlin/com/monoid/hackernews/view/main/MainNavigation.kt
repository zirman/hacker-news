package com.monoid.hackernews.view.main

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.ui.util.getNetworkConnectivityStateFlow
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

    val windowSizeClassState = rememberUpdatedState(windowSizeClass)

    NavHost(
        navController = mainNavController,
        startDestination = mainGraphRoutePattern,
        modifier = modifier,
    ) {
        mainGraph(
            mainViewModel = mainViewModel,
            context = context,
            snackbarHostState = snackbarHostState,
            windowSizeClassState = windowSizeClassState,
            drawerState = drawerState,
            onNavigateToUser = onNavigateToUser,
            onNavigateToReply = onNavigateToReply,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateUp = onNavigateUp,
            onLoginError = onLoginError
        )
    }
}
