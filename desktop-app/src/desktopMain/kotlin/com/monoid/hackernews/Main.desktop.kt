@file:OptIn(ExperimentalSplitPaneApi::class)

package com.monoid.hackernews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.home.ItemsColumn
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.StoryOrdering
import com.monoid.hackernews.common.view.theme.AppTheme
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import java.awt.Cursor
import java.awt.Desktop

fun main() {
    startKoin {
        modules(ApplicationModule().module)
    }
    application {
        Window(onCloseRequest = ::exitApplication) {
            AppTheme {
                var showLoginDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                if (showLoginDialog) {
                    LoginDialog(onDismissRequest = { showLoginDialog = false })
                }
                Scaffold(
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Localized description"
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Localized description",
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Mic,
                                        contentDescription = "Localized description",
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Image,
                                        contentDescription = "Localized description",
                                    )
                                }
                            },
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = { /* do something */ },
                                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    Icon(Icons.Filled.Add, "Localized description")
                                }
                            }
                        )
                    },
                ) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable<Route.Home> {
                            HNPanes(
                                onClickLogin = {
                                    showLoginDialog = true
                                },
                                onClickUrl = { url ->
                                    try {
                                        (if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null)
                                            ?.takeIf { it.isSupported(Desktop.Action.BROWSE) }
                                            ?.run {
                                                browse(url.toUri().uri)
                                                true
                                            }
                                            ?: false
                                    } catch (throwable: Throwable) {
                                        // TODO
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HNPanes(
    onClickLogin: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val splitterState = rememberSplitPaneState()
    var itemId by rememberSaveable {
        mutableLongStateOf(-1L)
    }
    HorizontalSplitPane(
        splitPaneState = splitterState,
        modifier = modifier,
    ) {
        first(640.dp) {
            val viewModel: StoriesViewModel = koinViewModel(
                extras = StoriesViewModel.extras(StoryOrdering.Trending),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            ItemsColumn(
                itemsList = uiState.itemsList,
                onVisibleItem = viewModel::updateItem,
                onClickItem = {
                    itemId = it.id.long
                },
                onClickReply = {},
                onClickUser = {},
                onClickUrl = onClickUrl,
                onClickUpvote = {},
                onClickFavorite = {},
                onClickFollow = {},
                onClickFlag = {},
                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                modifier = Modifier.fillMaxHeight(),
            ) {}
        }
        second(640.dp) {
            if (itemId != -1L) {
                ItemDetailPane(
                    itemId = ItemId(itemId),
                    onClickUrl = onClickUrl,
                    onClickUser = {},
                    onClickReply = {},
                    onClickLogin = onClickLogin,
                )
            }
        }
        splitter {
            visiblePart {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background),
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                        .background(SolidColor(Color.Gray), alpha = 0.50f)
                        .width(9.dp)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
