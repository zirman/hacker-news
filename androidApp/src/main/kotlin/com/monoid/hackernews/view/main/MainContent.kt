@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)

package com.monoid.hackernews.view.main

import android.app.Activity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Login
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.common.data.Preferences
import com.monoid.hackernews.common.navigation.Route
import com.monoid.hackernews.common.util.rememberMetricsStateHolder
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.view.TooltipPopupPositionProvider
import com.monoid.hackernews.view.navigationdrawer.NavigationDrawerContent
import com.monoid.hackernews.view.navigationdrawer.NavigationRailContent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(LocalContext.current as Activity),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // TODO: refactor to not use Throwable
    val (showLoginErrorDialog, setShowLoginErrorDialog) =
        remember { mutableStateOf<Throwable?>(null) }

    Box(modifier = modifier) {
        val mainNavController: NavHostController =
            rememberNavController()

        val metricsStateHolder: PerformanceMetricsState.Holder =
            rememberMetricsStateHolder()

        // save route to jank stats
        LaunchedEffect(Unit) {
            if (false) { // TODO: prod build
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

//        val modalBottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
//            initialValue = ModalBottomSheetValue.Hidden,
//            skipHalfExpanded = true,
//        )

//        val bottomSheetNavigator = remember(modalBottomSheetState) {
//            BottomSheetNavigator(sheetState = modalBottomSheetState)
//        }

//        mainNavController.navigatorProvider += bottomSheetNavigator
//
//        ModalBottomSheetLayout(
//            bottomSheetNavigator = bottomSheetNavigator
//        ) {
//            val fullyExpandedState =
//                rememberUpdatedState(
//                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
//                        windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded
//                )
//
//
//        }

//        MainNavigation(
//            mainViewModel = mainViewModel,
//            windowSizeClass = windowSizeClass,
//            mainNavController = mainNavController,
//            drawerState = drawerState,
//            onNavigateToUser = { username ->
////                mainNavController.navigate(User(username))
//            },
//            onNavigateToReply = { itemId ->
////                mainNavController.navigate(Reply(itemId))
//            },
//            onNavigateToLogin = { loginAction ->
////                loginViewModel
////                mainNavController.navigate(Login(loginAction))
//            },
//            onNavigateUp = { mainNavController.navigateUp() },
//            onLoginError = { setShowLoginErrorDialog(it) },
//        )
        val fullyExpandedState = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded

        var showLogin by remember { mutableStateOf(false) }
        var showAboutUs by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }

        ModalNavigationDrawer(
            drawerContent = {
                // hide drawer when expanded
                LaunchedEffect(fullyExpandedState) {
                    if (fullyExpandedState) {
                        drawerState.close()
                    }
                }

                // hide drawer content because it may layout under navigation bars.
                AnimatedVisibility(visible = fullyExpandedState.not()) {
                    NavigationDrawerContent(
                        preferences = mainViewModel.preferencesDataSource,
                        mainNavController = mainNavController,
                        drawerState = drawerState,
                        onClickUser = { username ->
                            if (username?.takeIf { it.string.isNotBlank() } == null) {
                                showLogin = true
                            } else {
                                mainNavController.navigate(Route.User(username))
                            }
                        },
                        onClickAboutUs = {
                            showAboutUs = true
                        },
                        onClickSettings = {
                            showSettings = true
                        },
                    )
                }
            },
            drawerState = drawerState,
            gesturesEnabled = fullyExpandedState.not(),
        ) {
            Row {
                AnimatedVisibility(visible = fullyExpandedState) {
                    NavigationRail(
                        header = {
                            val preferencesState: Preferences? by
                            remember { mainViewModel.preferencesDataSource.data }
                                .collectAsStateWithLifecycle(null)

                            val preferences: Preferences? =
                                preferencesState

                            if (preferences?.password?.string?.isNotEmpty() == true) {
                                NavigationRailItem(
                                    selected = false,
                                    onClick = {
//                                        mainNavController.navigate(
//                                            MainNavigation.User(
//                                                authentication.username,
//                                            ),
//                                        )
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.TwoTone.Face,
                                            contentDescription = preferences.username.string,
                                        )
                                    },
                                    label = { Text(text = preferences.username.string) },
                                )
                            } else {
                                val scope = rememberCoroutineScope()
                                val state = rememberTooltipState()

                                TooltipBox(
                                    positionProvider = TooltipPopupPositionProvider(),
                                    tooltip = {
                                        Surface {
                                            Row {
                                                Text(text = stringResource(id = R.string.login))
                                                Text(text = stringResource(id = R.string.login_description))

                                                Row {
                                                    Spacer(modifier = Modifier.weight(1f))

                                                    TextButton(
                                                        onClick = { scope.launch { state.dismiss() } }
                                                    ) {
                                                        Text(text = stringResource(id = R.string.dismiss))
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    state = state,
                                ) {
                                    NavigationRailItem(
                                        selected = false,
                                        onClick = {
//                                            mainNavController.navigate(
//                                                MainNavigation.Login(LoginAction.Login),
//                                            )
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.TwoTone.Login,
                                                contentDescription = stringResource(id = R.string.login),
                                            )
                                        },
                                        label = {
                                            Text(text = stringResource(id = R.string.login))
                                        },
                                    )
                                }
                            }
                        },
                    ) {
                        NavigationRailContent(mainNavController = mainNavController)
                    }
                }

                LoginBottomSheet(windowSizeClass = windowSizeClass)

                if (showAboutUs) {
                    AboutUsBottomSheet(
                        windowSizeClass = windowSizeClass,
                        onHide = { showAboutUs = false },
                    )
                }

                if (showSettings) {
                    SettingsBottomSheet(
                        windowSizeClass = windowSizeClass,
                        onHide = { showSettings = false },
                    )
                }

//                if (showReply) {
//                    ReplyBottomSheet(
//                        authentication = mainViewModel.authentication,
//                        itemTreeRepository = mainViewModel.itemTreeRepository,
//                        httpClient = mainViewModel.httpClient,
//                        windowSizeClassState = windowSizeClassState,
//                        onNavigateUp = onNavigateUp,
//                        onLoginError = onLoginError,
//                    )
//                }

                MainNavigation(
                    mainViewModel = mainViewModel,
                    windowSizeClass = windowSizeClass,
                    mainNavController = mainNavController,
                    drawerState = drawerState,
                    onNavigateToUser = { username ->
//                        mainNavController.navigate(MainNavigation.User(username))
                    },
                    onNavigateToReply = { itemId ->
//                        mainNavController.navigate(MainNavigation.Reply(itemId))
                    },
                    onNavigateToLogin = { loginAction ->
//                        mainNavController.navigate(MainNavigation.Login(loginAction))
                    },
                    onNavigateUp = { mainNavController.navigateUp() },
                    onLoginError = { setShowLoginErrorDialog(it) },
                )
            }
        }

        // status bar scrim on top
        Spacer(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        Pair(0f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)),
                        Pair(.4f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        Pair(1f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)),
                    ),
                )
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.safeDrawing)
                .align(Alignment.TopCenter),
        )

        // navigation bar scrim when on bottom
        Spacer(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        Pair(0f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)),
                        Pair(.6f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        Pair(1f, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)),
                    ),
                )
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.safeDrawing)
                .align(Alignment.BottomCenter),
        )

        // navigation bar scrim on start
        Spacer(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .fillMaxHeight()
                .windowInsetsStartWidth(WindowInsets.safeDrawing)
                .align(Alignment.CenterStart),
        )

        // navigation bar scrim on end
        Spacer(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .fillMaxHeight()
                .windowInsetsEndWidth(WindowInsets.safeDrawing)
                .align(Alignment.CenterEnd),
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
                    imageVector = Icons.AutoMirrored.TwoTone.Login,
                    contentDescription = stringResource(id = R.string.login),
                )
            },
            title = { Text(text = stringResource(id = R.string.login_error)) },
            text = {
                Text(
                    text = showLoginErrorDialog.message
                        ?: stringResource(id = R.string.invalid_username_or_password),
                )
            },
            iconContentColor = MaterialTheme.colorScheme.tertiary,
            titleContentColor = MaterialTheme.colorScheme.tertiary,
            textContentColor = MaterialTheme.colorScheme.tertiary,
        )
    }
}
