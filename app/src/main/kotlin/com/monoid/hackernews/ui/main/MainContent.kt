package com.monoid.hackernews.ui.main

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.monoid.hackernews.BuildConfig
import com.monoid.hackernews.MainNavigation
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.R
import com.monoid.hackernews.Username
import com.monoid.hackernews.datastore.Authentication
import com.monoid.hackernews.navigation.LoginAction
import com.monoid.hackernews.settingsDataStore
import com.monoid.hackernews.ui.navigationdrawer.NavigationDrawerContent
import com.monoid.hackernews.ui.navigationdrawer.NavigationRailContent
import com.monoid.hackernews.util.rememberMetricsStateHolder

@Composable
fun MainContent(windowSizeClass: WindowSizeClass) {
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

        val mainViewModel: MainViewModel =
            viewModel()

        // handle deep links
        LaunchedEffect(mainNavController) {
            for (intent in mainViewModel.newIntentChannel) {
                mainNavController.handleDeepLink(intent)
            }
        }

        val modalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )

        val bottomSheetNavigator = remember(modalBottomSheetState) {
            BottomSheetNavigator(sheetState = modalBottomSheetState)
        }

        mainNavController.navigatorProvider += bottomSheetNavigator

        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            modifier = Modifier
                // color under system bars when in landscape
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing
                        .only(WindowInsetsSides.Horizontal)
                ),
        ) {
            val fullyExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
                    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded

            ModalNavigationDrawer(
                drawerContent = {
                    // hide drawer when expanded
                    LaunchedEffect(fullyExpanded) {
                        if (fullyExpanded) {
                            drawerState.close()
                        }
                    }

                    // hide drawer content because it may layout under navigation bars.
                    AnimatedVisibility(visible = fullyExpanded.not()) {
                        NavigationDrawerContent(
                            mainNavController = mainNavController,
                            drawerState = drawerState,
                            onClickUser = { username ->
                                mainNavController.navigate(
                                    MainNavigation.User.routeWithArgs(username)
                                )
                            },
                            modifier = Modifier.windowInsetsPadding(
                                WindowInsets.safeDrawing
                                    .only(WindowInsetsSides.Top + WindowInsetsSides.Bottom)
                            ),
                        )
                    }
                },
                drawerState = drawerState,
                gesturesEnabled = fullyExpanded.not(),
            ) {
                Row {
                    AnimatedVisibility(visible = fullyExpanded) {
                        NavigationRail(
                            header = {
                                val context: Context =
                                    LocalContext.current

                                val authenticationState: State<Authentication?> =
                                    remember { context.settingsDataStore.data }
                                        .collectAsState(initial = null)

                                val authentication: Authentication? =
                                    authenticationState.value

                                val modifier: Modifier = Modifier
                                    .windowInsetsPadding(
                                        WindowInsets.safeDrawing
                                            .only(WindowInsetsSides.Top)
                                    )

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
                                                contentDescription = authentication.username,
                                            )
                                        },
                                        modifier = modifier,
                                        label = { Text(text = authentication.username) },
                                    )
                                } else {
                                    NavigationRailItem(
                                        selected = false,
                                        onClick = {
                                            mainNavController.navigate(
                                                MainNavigation.Login.routeWithArgs(
                                                    LoginAction.Login
                                                )
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.TwoTone.Login,
                                                contentDescription = stringResource(id = R.string.login),
                                            )
                                        },
                                        modifier = modifier,
                                        label = {
                                            Text(text = stringResource(id = R.string.login))
                                        },
                                    )
                                }
                            },
                        ) {
                            NavigationRailContent(
                                mainNavController = mainNavController,
                            )
                        }
                    }

                    MainNavigationComponent(
                        windowSizeClass = windowSizeClass,
                        mainNavController = mainNavController,
                        drawerState = drawerState,
                        onLoginError = { setShowLoginErrorDialog(it) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing
                                    .only(WindowInsetsSides.Top)
                            ),
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        Pair(0f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)),
                        Pair(.4f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        Pair(1f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)),
                    )
                )
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.safeDrawing)
                .align(Alignment.TopCenter),
        )

        Spacer(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        Pair(0f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)),
                        Pair(.6f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        Pair(1f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)),
                    )
                )
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.safeDrawing)
                .align(Alignment.BottomCenter),
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
                    contentDescription = stringResource(id = R.string.login),
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
            textContentColor = MaterialTheme.colorScheme.tertiary,
        )
    }
}
