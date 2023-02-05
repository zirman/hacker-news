package com.monoid.hackernews.view.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.icons.twotone.Login
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.monoid.hackernews.BuildConfig
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.view.navigationdrawer.NavigationDrawerContent
import com.monoid.hackernews.view.navigationdrawer.NavigationRailContent
import com.monoid.hackernews.common.util.rememberMetricsStateHolder

@Composable
fun MainContent(
    mainViewModel: MainViewModel,
    windowSizeClass: WindowSizeClass
) {
    val drawerState: DrawerState =
        rememberDrawerState(DrawerValue.Closed)

    val (showLoginErrorDialog, setShowLoginErrorDialog) =
        remember { mutableStateOf<Throwable?>(null) }

    Box {
        val mainNavController: NavHostController =
            rememberAnimatedNavController()

        val metricsStateHolder: PerformanceMetricsState.Holder =
            rememberMetricsStateHolder()

        // save route to jank stats
        LaunchedEffect(Unit) {
            if (BuildConfig.DEBUG.not()) {
                var route: String? = null

                mainNavController.addOnDestinationChangedListener { _, destination, _ ->
                    if (route != null) {
                        metricsStateHolder.state!!.removeState("route")
                    }

                    route = destination.route

                    if (route != null) {
                        metricsStateHolder.state!!.putState("route", "$route")
                    }
                }
            }
        }

        // handle deep links
        LaunchedEffect(mainNavController) {
            for (intent in mainViewModel.newIntentChannel) {
                mainNavController.handleDeepLink(intent)
            }
        }

        val modalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )

        val bottomSheetNavigator = remember(modalBottomSheetState) {
            BottomSheetNavigator(sheetState = modalBottomSheetState)
        }

        mainNavController.navigatorProvider += bottomSheetNavigator

        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator
        ) {
            val fullyExpandedState =
                rememberUpdatedState(
                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded
                )

            ModalNavigationDrawer(
                drawerContent = {
                    // hide drawer when expanded
                    LaunchedEffect(fullyExpandedState.value) {
                        if (fullyExpandedState.value) {
                            drawerState.close()
                        }
                    }

                    // hide drawer content because it may layout under navigation bars.
                    AnimatedVisibility(visible = fullyExpandedState.value.not()) {
                        NavigationDrawerContent(
                            authentication = mainViewModel.authentication,
                            mainNavController = mainNavController,
                            drawerState = drawerState,
                            onClickUser = { username ->
                                mainNavController.navigate(
                                    MainNavigation.User.routeWithArgs(username)
                                )
                            }
                        )
                    }
                },
                drawerState = drawerState,
                gesturesEnabled = fullyExpandedState.value.not()
            ) {
                Row {
                    AnimatedVisibility(visible = fullyExpandedState.value) {
                        NavigationRail(
                            header = {
                                val authenticationState: State<Authentication?> =
                                    remember { mainViewModel.authentication.data }
                                        .collectAsState(initial = null)

                                val authentication: Authentication? =
                                    authenticationState.value

                                if (authentication?.password?.isNotEmpty() == true) {
                                    NavigationRailItem(
                                        selected = false,
                                        onClick = {
                                            mainNavController.navigate(
                                                MainNavigation.User.routeWithArgs(
                                                    Username(authentication.username)
                                                )
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.TwoTone.Face,
                                                contentDescription = authentication.username
                                            )
                                        },
                                        label = { Text(text = authentication.username) }
                                    )
                                } else {
                                    NavigationRailItem(
                                        selected = false,
                                        onClick = {
                                            mainNavController.navigate(
                                                MainNavigation.Login
                                                    .routeWithArgs(LoginAction.Login)
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.TwoTone.Login,
                                                contentDescription = stringResource(id = R.string.login),
                                            )
                                        },
                                        label = {
                                            Text(text = stringResource(id = R.string.login))
                                        }
                                    )
                                }
                            },
                        ) {
                            NavigationRailContent(
                                mainNavController = mainNavController
                            )
                        }
                    }

                    MainNavigation(
                        mainViewModel = mainViewModel,
                        windowSizeClass = windowSizeClass,
                        mainNavController = mainNavController,
                        drawerState = drawerState,
                        onNavigateToUser = { username ->
                            mainNavController.navigate(MainNavigation.User.routeWithArgs(username))
                        },
                        onNavigateToReply = { itemId ->
                            mainNavController.navigate(MainNavigation.Reply.routeWithArgs(itemId))
                        },
                        onNavigateToLogin = { loginAction ->
                            mainNavController
                                .navigate(MainNavigation.Login.routeWithArgs(loginAction))
                        },
                        onNavigateUp = { mainNavController.navigateUp() },
                        onLoginError = { setShowLoginErrorDialog(it) }
                    )
                }
            }
        }

        // status bar scrim on top
        Spacer(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        Pair(0f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)),
                        Pair(.4f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        Pair(1f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f))
                    )
                )
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.safeDrawing)
                .align(Alignment.TopCenter)
        )

        // navigation bar scrim when on bottom
        Spacer(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        Pair(0f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)),
                        Pair(.6f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        Pair(1f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f))
                    )
                )
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.safeDrawing)
                .align(Alignment.BottomCenter)
        )

        // navigation bar scrim on start
        Spacer(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .fillMaxHeight()
                .windowInsetsStartWidth(WindowInsets.safeDrawing)
                .align(Alignment.CenterStart)
        )

        // navigation bar scrim on end
        Spacer(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .fillMaxHeight()
                .windowInsetsEndWidth(WindowInsets.safeDrawing)
                .align(Alignment.CenterEnd)
        )
    }

    if (showLoginErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { setShowLoginErrorDialog(null) },
            confirmButton = {
                TextButton(onClick = { setShowLoginErrorDialog(null) }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.TwoTone.Login,
                    contentDescription = stringResource(id = R.string.login)
                )
            },
            title = { Text(text = stringResource(id = R.string.login_error)) },
            text = {
                Text(
                    text = showLoginErrorDialog.message
                        ?: stringResource(id = R.string.invalid_username_or_password)
                )
            },
            iconContentColor = MaterialTheme.colorScheme.tertiary,
            titleContentColor = MaterialTheme.colorScheme.tertiary,
            textContentColor = MaterialTheme.colorScheme.tertiary
        )
    }
}
